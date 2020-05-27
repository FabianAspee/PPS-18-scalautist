package servermodel.routes.masterroute

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

object MasterRouteTurni{

  val routeTurni: Route =
    concat(
      path("getturno" / IntNumber) {
        id => getTurno(id)
      },
      path("getallturno") {
        getAllTurno
      },
      path("createturno" ) {
        createTurno()
      },
      path("createallturno") {
        createAllTurno()
      },
      path("deleteturno") {
        deleteTurno()
      },
      path("deleteallturno") {
        deleteAllTurno()
      },
      path("updateturno") {
        updateTurno()
      }
    )

}
