package servermodel.routes.masterroute

import akka.http.scaladsl.server.{Directives, Route}
import caseclass.CaseClassDB.Assenza
import caseclass.CaseClassHttpMessage.Dates
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.ws.rs.core.MediaType
import javax.ws.rs.{Consumes, POST, Path, Produces}
import servermodel.routes.subroute.AssenzaRoute._

/**
 * @author Fabian Aspée Encina
 * This object manage routes that act on the assenza entity and its related entities
 */
trait MasterRouteAssenza {

  @Path("/getholidaybyperson")
  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Get Holiday By Persona", description = "All Holiday by person in a year determinate",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Int])))),
    responses = Array(
      new ApiResponse(responseCode = "201", description = "Get All holiday by person correctly from database"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def getHolidayByPerson: Route

  @Path("/addabsence")
  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Add Absence", description = "Add Absence into database. this can be illness or holidays",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Assenza])))),
    responses = Array(
      new ApiResponse(responseCode = "201", description = "Insert Absence correctly into database"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def addAbsencePersona(): Route

  @Path("/getabsenceinyearforperson")
  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Get Absence and Holiday", description = "Get absence and holiday for a person in a determinate year",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Int])))),
    responses = Array(
      new ApiResponse(responseCode = "201", description = "Get all holiday and illness from database by person"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def getAbsenceInYearForPerson: Route

  @Path("/allabsences")
  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Get all absent employee", description = "Get all absent employee in a chosen date",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Dates])))),
    responses = Array(
      new ApiResponse(responseCode = "201", description = "Get all absent people in chosen date"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def absenceOnDay(): Route
}

object MasterRouteAssenza extends Directives with MasterRouteAssenza {

  override def getHolidayByPerson: Route =
    path("getholidaybyperson") {
      holidayByPerson()
    }

  override def addAbsencePersona(): Route =
    path("addabsence") {
      addAbsence()
    }

  override def getAbsenceInYearForPerson: Route =
    path("getAbsenceInYearForPerson") {
      absenceInYearForPerson()
    }

  override def absenceOnDay(): Route =
    path("allabsences") {
      absencesOnDay()
    }

  val routeAssenza: Route =
    concat(getHolidayByPerson,addAbsencePersona(),getAbsenceInYearForPerson, absenceOnDay())
}
