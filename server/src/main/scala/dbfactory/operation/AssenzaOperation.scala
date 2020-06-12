package dbfactory.operation
import java.sql.Date
import java.time.temporal.ChronoUnit
import java.util.Calendar

import slick.jdbc.SQLServerProfile.api._
import caseclass.CaseClassDB.Assenza
import caseclass.CaseClassHttpMessage.Ferie
import dbfactory.implicitOperation.ImplicitInstanceTableDB.{InstanceAssenza, InstancePersona}
import dbfactory.implicitOperation.OperationCrud
import dbfactory.setting.Table.{AssenzaTableQuery, PersonaTableQuery}
import messagecodes.StatusCodes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** @author Giovanni Mormone
 *  Trait which allows to perform operations on the assenze table.
 */
trait AssenzaOperation extends OperationCrud[Assenza]{
  /**
   * Returns a list of [[caseclass.CaseClassHttpMessage.Ferie]] for the year provided as input for all the conducenti
   * in the DB.
   * @param data
   *             The year of the remainig feries to get
   */
  def getAllFerie(data: Int): Future[Option[List[Ferie]]]

  /**
   * Returns the list of assenze for the person in the year provided
   * @param year
   *            The year that the assenze must have
   * @param idPersona
   *                  the persona which assenze are asked
   * @return
   *         A list of [[Assenza]] or None if no Assenze are in the DB.
   */
  def getAssenzeInYearForPerson(year: Int, idPersona: Int): Future[Option[List[Assenza]]]
}

object AssenzaOperation extends AssenzaOperation{

  import utils.DateConverter._
  private case class JoinResult(dataInizio:Date,dataFine:Date,idPersona:Int,nomePersona:String,cognomePersona:String)

  private object JoinResult{
    implicit def tuple5ToJoinResult(tuple:(Date,Date,Int,String,String)):JoinResult =
      JoinResult.apply _ tupled tuple
  }
  //caricare da qualche parte?
  private val GIORNI_FERIE_ANNUI: Int = 35
  //anche questo si può caricare da qualche parte
  private val CODICE_CONDUCENTE: Int = 3

  override def getAllFerie(data: Int):Future[Option[List[Ferie]]] = {
    val nextYear = dateFromYear(data+1)
    val currentYear = dateFromYear(data)
    constructFerie(currentYear,nextYear)
  }

    /**
     *
     * @param element case class that represent instance of the table in database
     * @return
     *         Future of Int that represent status of operation, returning the id of the assenza inserted or an error code:
     *            [[messagecodes.StatusCodes.ERROR_CODE1]] if the persona alredy has an assenza in the period provided.
     *            [[messagecodes.StatusCodes.ERROR_CODE2]]  if the days between the given day are > of [[GIORNI_FERIE_ANNUI]]
     *            [[messagecodes.StatusCodes.ERROR_CODE3]] if the dates given in input are not of the same year.
     *            [[messagecodes.StatusCodes.ERROR_CODE4]] if the start date is after the end date.
     *            [[messagecodes.StatusCodes.ERROR_CODE5]] if the days of the assenza to insert are greater than the remaninig day of assenza for the persona.
     *
     */
  override def insert(element: Assenza): Future[Option[Int]] ={
    for{
      absence <-InstanceAssenza.operation().selectFilter(f => f.dataInizio <= element.dataFine && f.dataFine >= element.dataInizio && f.personaId === element.personaId)
      result <- if (absence.isDefined) Future.successful(Some(StatusCodes.ERROR_CODE1)) else for(x <- insertPriv(element)) yield x
    }yield result
  }

  override def getAssenzeInYearForPerson(year: Int, idPersona: Int): Future[Option[List[Assenza]]] = {
    InstanceAssenza.operation().selectFilter(f => f.personaId === idPersona && f.dataInizio >= dateFromYear(year) && f.dataFine <= dateFromYear(year+1))
  }

  private def insertPriv(element: Assenza): Future[Option[Int]] = element match {
    case Assenza(_,start,end,false,_) if computeDaysBetweenDates(start,end) > GIORNI_FERIE_ANNUI => Future.successful(Some(StatusCodes.ERROR_CODE2))
    case Assenza(_,start,end,false,_) if notSameYear(start,end) => Future.successful(Some(StatusCodes.ERROR_CODE3))
    case Assenza(_,start,end,false,_) if start.compareTo(end) >= 0 => Future.successful(Some(StatusCodes.ERROR_CODE4))
    case Assenza(id,start,end,false,_) => tryInsert(Assenza(id,start,end,malattia = false))
    case _ => super.insert(element)
  }

  private def tryInsert(assenza:Assenza): Future[Option[Int]] = {
    val d:(Int,Assenza) => Int = (x,assenz) => x + computeDaysBetweenDates(assenz.dataInizio,assenz.dataFine)
    InstanceAssenza.operation()
      .selectFilter(filter => filter.personaId === assenza.personaId)
      .map(_.map(_.foldLeft(0)(d))).map(days => days.exists(_ + computeDaysBetweenDates(assenza.dataInizio,assenza.dataFine) > GIORNI_FERIE_ANNUI))
      .flatMap(outOfDays =>if(outOfDays) Future.successful(Option(StatusCodes.ERROR_CODE5)) else super.insert(assenza))
  }

  /**
   * Constructs the list of ferie in one year. First construct the list of ferie for all the conducenti as if they have
   * full days of ferie remaining; after that select all the assenze in the given year and reduces the output to the Ferie
   * for each conducente; lastly substitutes each Ferie in the starting List of ferie with the computed one, if the conducente
   * did days of assenza, else it remains the basic Ferie.
   */
  private def constructFerie(currentYear: Date, nextYear: Date): Future[Option[List[Ferie]]] ={
    val filterJoin = for{
      (persona,assenza) <- PersonaTableQuery.tableQuery() join AssenzaTableQuery.tableQuery() on (_.id === _.personaId)
      if assenza.dataFine < nextYear && assenza.dataInizio >= currentYear && !assenza.malattia
    }yield (assenza.dataInizio,assenza.dataFine,persona.id,persona.nome,persona.cognome)
    val ferieReduction: (Ferie,Ferie) => Ferie = (x,y) => Ferie(x.idPersona,x.nomeCognome,x.giorniVacanza + y.giorniVacanza)
    val toFerie: JoinResult => Ferie = join =>
      Ferie(join.idPersona,join.nomePersona.concat(join.cognomePersona).concat(join.idPersona.toString),computeDaysBetweenDates(join.dataInizio,join.dataFine))
    val setFerieDays: (List[Ferie],Ferie) => Int = (value, startingFerie)=> GIORNI_FERIE_ANNUI - value.find(_.idPersona == startingFerie.idPersona).getOrElse(Ferie(0,"",0)).giorniVacanza

    startingPersoneFerie()
      .flatMap(ferie => {
        InstanceAssenza.operation()
          .execJoin(filterJoin)
          .map(_.map(ass => ass.map(x => toFerie(x)).groupBy(_.idPersona).values.map(_.reduce(ferieReduction)).toList))
          .collect{
            case None => ferie
            case Some(value) => ferie.map(_.map(startingFerie => Ferie(startingFerie.idPersona,startingFerie.nomeCognome, setFerieDays(value,startingFerie))))
          }
      })
  }

  /**
   * It constructs the list of ferie with default remaining days for all the conducenti.
   * None is returned if there is no conducente in the db.
   */
  private def startingPersoneFerie(): Future[Option[List[Ferie]]] = {
    //prende id-nome-cognome e li concatena
    val tupToNameSurname: ((Int,String,String)) => String = x => x._2.concat(x._3).concat(x._1.toString)

    InstancePersona.operation()
      .execQueryFilter(field => (field.id,field.nome,field.cognome),_.ruolo === CODICE_CONDUCENTE)
      .map(_.map(_.map(x => Ferie(x._1,tupToNameSurname(x),GIORNI_FERIE_ANNUI))))
  }
}
