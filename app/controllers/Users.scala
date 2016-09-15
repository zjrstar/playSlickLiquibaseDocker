package controllers

import biz.user.User
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Example controller; see conf/routes for the the mapping to routes */
class Users extends Controller with Security {

  /** Retrieves a logged in user if the authentication token is valid.
    *
    * If the token is invalid, [[HasUserToken]] does not invoke this function.
    *
    * @return The user in JSON format.
    */
  def authUser() = HasUserTokenFuture(parse.empty) { token => userId => implicit request =>
    User.findOneById(userId).map {
      case Some(user) => Ok(Json.toJson(user))
      case _ => NotFound("")
    }
  }

  /** Retrieves the user for the given id as JSON */
  def user(id: Long) = HasUserTokenFuture(parse.empty) { token => userId => implicit request =>
    User.findOneById(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case _ => NotFound("")
    }
  }

  /** Creates a user from the given JSON */
  def createUser() = HasUserTokenFuture(parse.json) { token => userId => implicit request =>
    User.findOneById(userId).flatMap { userOpt =>
      val role: String = userOpt.map(_.role).getOrElse("")
      if ("admin".equals(role)) {
        val user = request.body.validate[User].get
        User.createUser(user).map(user => Ok(Json.toJson(user)))
      } else {
        Future.successful(Unauthorized)
      }
    }
  }

  /** Updates the user for the given id from the JSON body */
  def updateUser(id: Long) = HasUserTokenFuture(parse.json) { token => userId => implicit request =>
    val user = request.body.validate[User].get
    User.updateUser(user).map(result => Ok(""))
  }

  /** Deletes a user for the given id */
  def deleteUser(id: Long) = HasUserTokenFuture(parse.empty) { token => userId => implicit request =>
    User.deleteUser(id).map(user => Ok(Json.toJson(user)))
  }

  /** Find users by email */
  def findUsers(email: String) = HasUserTokenFuture(parse.empty) { token => userId => implicit request =>
    User.findUsers(email).map(user => Ok(Json.toJson(user)))
  }

  def modifyPassword(id: Long) = HasUserTokenFuture(parse.json) { token => userId => implicit request =>
    val newPwd = request.body.\("value").validate[String].get
    User.modifyPassword(id, newPwd).map(success => Ok(""))
  }

  def validatePassword(id: Long) = HasUserTokenFuture(parse.json) { token => userId => implicit request =>
    val password = request.body.\("value").validate[String].get
    User.validatePassword(id, password).map(isValid => Ok(""))
  }

}
