package model

import spray.json.{DeserializationException, JsString, JsValue, JsonFormat}
import java.sql.Date
import java.text.SimpleDateFormat

/** The original trait of the model */
trait AbstractModel {

  /** Spray does not have a standard Date format, so I added one.
   *
   * Reference site : https://stackoverflow.com/questions/41508948/marshalling-java-util-date-with-sprayjson
   */
  implicit object DateFormat extends JsonFormat[Date] {
    val formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

    def write(date: Date) = JsString(formatter.format(date))

    def read(value: JsValue) = {
      value match {
        case JsString(date) => new Date(formatter.parse(date).getTime())
        case _ => throw new DeserializationException("Expected JsString")
      }
    }
  }

}
