package controllers

import play.api.mvc.{Action, Result, Request, ActionBuilder}

import scala.concurrent.Future

/**
 * Created by Barry on 4/26/15.
 */
object AppAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    block(request)
  }

  override def composeAction[A](action: Action[A]) = new ErrorHandlingAction[A](action)


}


