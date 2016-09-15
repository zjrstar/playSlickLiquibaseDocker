package controllers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created by Barry on 4/12/15.
 */
case class Keyed(id: Int, createdOn: Option[DateTime]) {


}

object Keyed {
  implicit val KeyedFromJson: Reads[Keyed] = (
    (__ \ "id").read[Int] ~
      (__ \ "createdOn").readNullable[DateTime]

    )(Keyed.apply _)

  implicit val KeyedToJson: Writes[Keyed] = (
    (__ \ "id").write[Int] ~
      (__ \ "createdOn").writeNullable[DateTime]
    )(unlift(Keyed.unapply))

}