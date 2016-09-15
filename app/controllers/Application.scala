package controllers

import javax.inject.Singleton

import biz.user.User
import com.google.inject.Inject
import play.api.cache._
import play.api.http.ContentTypes
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/** Application controller, handles authentication */
@Singleton
class Application extends Controller with Security {

  /** Serves the index page, see views/index.scala.html */
  def index = Action {
    Ok(views.html.index())
  }

  /**
   * Retrieves all routes via reflection.
   * http://stackoverflow.com/questions/12012703/less-verbose-way-of-generating-play-2s-javascript-router
   * @todo If you have controllers in multiple packages, you need to add each package here.
   */
  def jsRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.Application.login,
        routes.javascript.Application.logout,
        routes.javascript.Users.authUser,
        routes.javascript.Users.createUser,
        routes.javascript.Users.deleteUser,
        routes.javascript.Users.findUsers,
        routes.javascript.Users.updateUser,
        routes.javascript.Users.modifyPassword,
        routes.javascript.Users.user,
        routes.javascript.Users.validatePassword
      )
    ).as(ContentTypes.JAVASCRIPT)
  }


  /** Used for obtaining the email and password from the HTTP login request */
  case class LoginCredentials(email: String, password: String)

  /** JSON reader for [[LoginCredentials]]. */
  implicit val LoginCredentialsFromJson = (
    (__ \ "email").read[String](minLength[String](5)) ~
      (__ \ "password").read[String](minLength[String](2))
    )((email, password) => LoginCredentials(email, password))

  /**
   * Log-in a user. Expects the credentials in the body in JSON format.
   *
   * Set the cookie [[AuthTokenCookieKey]] to have AngularJS set the X-XSRF-TOKEN in the HTTP
   * header.
   *
   * @return The token needed for subsequent requests
   */
  def login() = AppAction(parse.json) { implicit request =>
    request.body.validate[LoginCredentials].fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      credentials => {
        // TODO Check credentials, log user in, return correct token
        val r = User.findByEmailAndPassword(credentials.email, credentials.password).map{ userOpt =>
         userOpt.fold {
           BadRequest(Json.obj("status" -> "KO", "message" -> "User not registered"))
         } { user =>
           /*
            * For this demo, return a dummy token. A real application would require the following,
            * as per the AngularJS documentation:
            *
            * > The token must be unique for each user and must be verifiable by the server (to
            * > prevent the JavaScript from making up its own tokens). We recommend that the token is
            * > a digest of your site's authentication cookie with a salt) for added security.
            *
            */
           //For testing purpose
           val token = java.util.UUID.randomUUID.toString
           //          val token = "068f7278-b6a4-4a60-94f3-308baf217eeb"
           Cache.set(token, user.id.get)
           Ok(Json.obj("token" -> token))
             .withCookies(Cookie(AuthTokenCookieKey, token, None, httpOnly = false))
         }
        }

        Await.result(r, 5 seconds)
      }
    )
  }

  /**
   * Log-out a user. Invalidates the authentication token.
   *
   * Discard the cookie [[AuthTokenCookieKey]] to have AngularJS no longer set the
   * X-XSRF-TOKEN in HTTP header.
   */
  def logout() = HasUserToken(parse.empty) { token => userId => implicit request =>
    Cache.remove(token)
    Ok.discardingCookies(DiscardingCookie(name = AuthTokenCookieKey))
  }


}
