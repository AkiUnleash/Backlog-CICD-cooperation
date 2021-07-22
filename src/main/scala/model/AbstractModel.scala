package model

import spray.json.{DeserializationException, JsString, JsValue, JsonFormat}

import java.sql.Date
import java.text.SimpleDateFormat

trait AbstractModel {

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
