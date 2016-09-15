package utils

/**
 * Created by Scala on 15-4-11.
 */
object Errors {


  val verificationCodeAlreadySent = 100
  val phoneNumberNotRegister: Int = 101
  val verificationCodeNotMatching: Int = 102
  val verificationCodeExpired: Int = 103
  val verificationCodeNotGenerated: Int = 104

  val passwordInvalid: Int = 200
  val noUserFoundForSpecifiedId = 300
  val noOrderFoundForSpecifiedId = 400
  val roleNotAllowed: Int = 500

  val UnknownServerError: Int = 999
}
