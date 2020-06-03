package servermodel.routes.subroute

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.{as, complete, entity, post, _}
import caseclass.CaseClassDB.Zona
import caseclass.CaseClassHttpMessage.Id
import jsonmessages.JsonFormats._
import servermodel.routes.exception.RouteException
import dbfactory.operation.ZonaOperation
import servermodel.routes.exception.SuccessAndFailure.anotherSuccessAndFailure

import scala.util.Success

object ZonaRoute {
  def getAllZona: Route =
    post {
      onComplete(ZonaOperation.selectAll) {
        case Success(t) =>  complete((StatusCodes.Found,t))
        case t => anotherSuccessAndFailure(t)
      }
    }

  def createZona(): Route =
    post {
      entity(as[Zona]) { zona =>
        onComplete(ZonaOperation.insert(zona)) {
          case Success(t) =>  complete(StatusCodes.Created,Zona(zona.zones,t)) //TODO
          case t => anotherSuccessAndFailure(t)
        }
      }
    }

  def createAllZona(): Route =
    post {
      entity(as[List[Zona]]) { zona =>
        onComplete(ZonaOperation.insertAll(zona)) {
          case Success(t) =>  complete(StatusCodes.Created)//qualcosa
          case t => anotherSuccessAndFailure(t)
        }
      }
    }

  def deleteZona(): Route =
    post {
      entity(as[Id]) { zona =>
        onComplete(ZonaOperation.delete(zona.id)) {
          case Success(t)  =>  complete(StatusCodes.Gone)
          case t => anotherSuccessAndFailure(t)
        }
      }
    }

  def deleteAllZona(): Route =
    post {
      entity(as[List[Id]]) { zona =>
        onComplete(ZonaOperation.deleteAll(zona.map(_.id))) {
          case Success(t) =>  complete(StatusCodes.Gone)
          case t => anotherSuccessAndFailure(t)
        }
      }
    }

  def updateZona(): Route =
    post {
      entity(as[Zona]) { zona =>
        onComplete(ZonaOperation.update(zona)) {
          case Success(Some(t)) =>  complete((StatusCodes.Created,Id(t)))
          case Success(None) =>complete(StatusCodes.OK)
          case t => anotherSuccessAndFailure(t)
        }
      }
    }
}
