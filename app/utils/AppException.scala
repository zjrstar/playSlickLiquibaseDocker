package utils

/**
 * Created by Scala on 15-4-11.
 */
case class AppException(code: Int, message: Option[String]) extends RuntimeException {}
