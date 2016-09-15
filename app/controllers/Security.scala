package controllers

import play.api.cache._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

/**
 * Security actions that should be used by all controllers that need to protect their actions.
 * Can be composed to fine-tune access control.
 */
trait Security {
  self: Controller =>

  implicit val app: play.api.Application = play.api.Play.current

  val AuthTokenHeader = "X-XSRF-TOKEN"
  val AuthTokenCookieKey = "XSRF-TOKEN"
  val AuthTokenUrlKey = "auth"

  /**
   * Checks that the token is:
   * - present in the cookie header of the request,
   * - either in the header or in the query string,
   * - matches a token already stored in the play cache
   */
  def HasUserToken[A](p: BodyParser[A] = parse.anyContent.asInstanceOf[BodyParser[A]])(
    f: String => Long => Request[A] => Result): Action[A] =
    AppAction(p) { implicit request =>
      request.cookies.get(AuthTokenCookieKey).fold {
        Unauthorized(Json.obj("message" -> "Invalid XSRF Token cookie"))
      } { xsrfTokenCookie =>
        val maybeToken = request.headers.get(AuthTokenHeader).orElse(request.getQueryString(AuthTokenUrlKey))
        maybeToken flatMap { token =>
          Cache.getAs[Long](token) map { userId =>
            if (xsrfTokenCookie.value.equals(token)) {
              f(token)(userId)(request)
            } else {
              Unauthorized(Json.obj("message" -> "Invalid Token"))
            }
          }
        } getOrElse Unauthorized(Json.obj("message" -> "No Token"))
      }
    }


  def HasUserTokenFuture[A](p: BodyParser[A] = parse.anyContent.asInstanceOf[BodyParser[A]])(
    f: String => Long => Request[A] => Future[Result]): Action[A] =
    AppAction.async(p) { implicit request =>
      request.cookies.get(AuthTokenCookieKey).fold {
        Future.successful(Unauthorized(Json.obj("message" -> "Invalid XSRF Token cookie")))
      } { xsrfTokenCookie => request.headers.get(AuthTokenHeader).orElse(request.getQueryString(AuthTokenUrlKey)) flatMap { token => Cache.getAs[Long](token) map { userId =>
        if (xsrfTokenCookie.value.equals(token)) {
          f(token)(userId)(request)
        } else {
          Future.successful(Unauthorized(Json.obj("message" -> "Invalid Token")))
        }
      }
      } getOrElse Future.successful(Unauthorized(Json.obj("message" -> "No Token")))
      }
    }

  val AuthConsumerTokenKey = "CSMR-TOKEN"

}
