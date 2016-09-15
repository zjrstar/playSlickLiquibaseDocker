package controllers

/**
 * Created by Barry on 4/26/15.
 */
case class AppError(errorCode: Int, errorMessage: Option[String])

object AppError {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val ErrorFromJson: Reads[AppError] = (
    (__ \ "errorCode").read[Int] ~
      (__ \ "errorMessage").readNullable[String]
    )(AppError.apply _)

  implicit val ErrorToJson: Writes[AppError] = (
    (__ \ "errorCode").write[Int] ~
      (__ \ "errorMessage").writeNullable[String]
    )((error: AppError) => (error.errorCode, error.errorMessage))
}