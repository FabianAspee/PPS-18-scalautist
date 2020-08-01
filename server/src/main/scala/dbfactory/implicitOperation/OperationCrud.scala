package dbfactory.implicitOperation

import utils.Execution

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * @author Fabian Aspée Encina
 * Generic abstract class which enable make operations in any table in database
 * every class in the package [[dbfactory.operation]] must extend this abstract class
 * @param crud Interface [[dbfactory.implicitOperation.Crud]] which enable make call operation of type
 *             select, insert, update, delete
 * @tparam A Is a case class [[caseclass.CaseClassDB]] that is passed as a parameter
 */
abstract class OperationCrud[A](implicit crud:Crud[A]) {

  implicit protected val execution: ExecutionContextExecutor = Execution.executionContext

  /**
   * Generic Method which enable select one record in any table in database by Id
   * @param element Id of record what select in database
   * @return Future of Option of Type A -> case class [[caseclass.CaseClassDB]]
   */
  def select (element:Int):Future[Option[A]]=   crud.select(element)

  /**
   * Select all element in a table in the database
   * @return List of all element in the table of the database that we have selected
   */
  def selectAll : Future[Option[List[A]]] = crud.selectAll

  /**
   * Generic operation which enable insert any element in any table in database
   * @param element case class that represent instance of the table in database
   * @return Future of Int that represent status of operation
   */
  def insert(element:A): Future[Option[Int]] = crud.insert(element)


  /**
   * Generic operation which enable insert a List of any element in any table in database
   * @param element List of case class that represent instance of the table in database
   * @return Future of List of Int that represent status of operation
   */
  def insertAll (element:List[A]):Future[Option[List[Int]]]= crud.insertAll(element)


  /**
   * Generic operation which enable the insert of a List of any element in any table in database.
   * It is a bulk operation that doesn't return the list of ids inserted in the db but only the
   * status of the operation
   * @param element List of case class that represent instance of the table in database
   * @return Future of List of Int that represent status of operation
   */
  def insertAllBatch (element:List[A]):Future[Option[Int]]= crud.insertAllBatch(element)


  /**
   * Generic operation which enable delete one instance into database, this method receive a case class
   * as parameter and send this to Crud trait -> [[dbfactory.implicitOperation.Crud]]
   * @param element case class that represent instance of database  [[caseclass.CaseClassDB]]
   * @return Future of Int that represent status of operation
   */
  def delete(element:Int):Future[Option[Int]]= crud.delete(element)


  /**
   * Generic operation which enable delete one or more instance into database, this method receive a list of
   * case class as parameter and send this to Crud trait -> [[dbfactory.implicitOperation.Crud]]
   * @param element list of case class that represent instance of database  [[caseclass.CaseClassDB]]
   * @return Future of Int that represent status of operation
   */
  def deleteAll(element:List[Int]): Future[Option[Int]]= crud.deleteAll(element)
  /**
   * Generic operation which receive as input a case class [[caseclass.CaseClassDB]] that represent a instance
   * we want update into database
   *
   * @param element case class that represent instance into database we want update
   * @return Future of Int that represent status of operation
   *         Note that the return value will be None if an update was performed and Some if the operation was insert
   */
  def update(element:A):Future[Option[Int]]= crud.update(element)

}
