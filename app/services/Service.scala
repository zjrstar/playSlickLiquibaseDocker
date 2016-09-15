package services

import org.slf4j.LoggerFactory
import utils.AppException

/**
 * Created by Barry on 4/12/15.
 */
trait Service[REQUEST, RESPONSE] {

  val logger = LoggerFactory.getLogger(classOf[Service[REQUEST, RESPONSE]])
  var request: REQUEST = _

  def execute(request: REQUEST): RESPONSE = {
    this.request = request
    preExec
    try {
      val result = doExec(request)
      postExec
      result
    } catch {
      case ex: AppException => throw ex
      case ex: Throwable =>
        ex.printStackTrace()
      logger.error("Failed to execute service. ", ex)
        throw new AppException(utils.Errors.UnknownServerError, None)
    } finally {
      doFinally
    }

  }

  def preExec(): Unit = {}

  def doExec(request: REQUEST): RESPONSE

  def postExec(): Unit = {}

  def doFinally(): Unit = {}
}
