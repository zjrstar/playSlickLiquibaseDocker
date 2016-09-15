package controllers

import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc._
import utils.AppException

import scala.concurrent.Future

/**
 * Created by Barry on 4/26/15.
 */
case class ErrorHandlingAction[A](action: Action[A]) extends Action[A] {
  val logger = LoggerFactory.getLogger("ControllerLogger")

  override def parser: BodyParser[A] = action.parser

  val UnknownServerError = 999;

  override def apply(request: Request[A]): Future[Result] = {
    try {
      action(request)
    } catch {
      case AppException(code, messageOption) => Future.successful(Results.BadRequest(Json.toJson(AppError(code, messageOption))))
      case ex: Throwable =>
        ex.printStackTrace()
        logger.error("Unknown Exception ", ex)
        Future.successful(Results.BadRequest(Json.toJson(AppError(UnknownServerError, None))))
    }
  }
}