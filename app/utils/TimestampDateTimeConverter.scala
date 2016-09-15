package utils

import java.sql.Timestamp

import org.joda.time.DateTime
import play.api.libs.json.{JsString, Reads, Writes}

/**
 * Created by Scala on 15-4-24.
 */
trait TimestampDateTimeConverter {
  implicit val tsreads: Reads[Timestamp] = Reads.of[DateTime] map (x => new Timestamp(x.getMillis))
  implicit val tswrites: Writes[Timestamp] = Writes { (ts: Timestamp) => JsString((new DateTime(ts.getTime)).toString) }
}
