package servermodel.routes.masterroute

import akka.http.scaladsl.server.{Directives, Route}
import caseclass.CaseClassHttpMessage.{AlgorithmExecute, CheckResultRequest, Dates, Request}
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.ws.rs.core.MediaType
import javax.ws.rs.{Consumes, POST, Path, Produces}
import servermodel.routes.subroute.RisultatoRoute._
import servermodel.routes.exception.SuccessAndFailure.timeoutResponse

import scala.concurrent.duration._


/**
 * @author Francesco Cassano, Fabian Aspee Encina
 *         This object manage routes that act on the Risultato entity and its related entities
 */
trait MasterRouteRisultato {

  @Path("/checkresult")
  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(tags = Array("Result Operation"),summary = "Check result", description = "Check the old result before running the algorithm",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Request[CheckResultRequest]])))),
    responses = Array(
      new ApiResponse(responseCode = "200", description = "run success"),
      new ApiResponse(responseCode = "400", description = "Bad Request"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def checkResult(): Route

  @Path("/getresultalgorithm")
  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(tags = Array("Result Operation"),summary = "Get Result Algorithm", description = "Return result of algorithm from dateI to dateF",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[(Int, Dates, Dates)])))),
    responses = Array(
      new ApiResponse(responseCode = "200", description = "get success"),
      new ApiResponse(responseCode = "400", description = "Bad Request"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def resultAlgorithm(): Route

  @Path("/executealgorithm")
  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(tags = Array("Result Operation"),summary = "Run Algorithm", description = "Run algorithm for obtained free day and shift",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Request[AlgorithmExecute]])))),
    responses = Array(
      new ApiResponse(responseCode = "200", description = "run success"),
      new ApiResponse(responseCode = "400", description = "Bad Request"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def executeAlgorithm(): Route

  @Path("/replaceshift")
  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(tags = Array("Result Operation"),summary = "Replace shift", description = "Reassign a shift to another employee",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Request[Int]])))),
    responses = Array(
      new ApiResponse(responseCode = "200", description = "replace success"),
      new ApiResponse(responseCode = "400", description = "Bad Request"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def replaceShift(): Route
}

object MasterRouteRisultato extends Directives with MasterRouteRisultato {

  override def replaceShift(): Route =
    path("replaceshift") {
      updateShift()
    }

  override def executeAlgorithm(): Route =
    path("executealgorithm") {
      runAlgorithm()
    }

  override def resultAlgorithm(): Route =
    path("getresultalgorithm") {
      withRequestTimeout(10 minute) {
        withRequestTimeoutResponse(request => timeoutResponse) {
          getResultAlgorithm
        }

      }
    }

  override def checkResult(): Route =
    path("checkresultprealgorithm") {
      checkOldResult()
    }


  val routeRisultato: Route =
    concat(
      replaceShift(),executeAlgorithm(),resultAlgorithm(),checkResult()
    )
}
