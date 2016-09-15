package biz.user

import java.sql.Timestamp
import java.util.UUID

import com.roundeights.hasher.Algo
import net.imadz.schema.gen.Tables
import net.imadz.schema.gen.Tables._
import org.joda.time.DateTime
import play.api.libs.json.Json
import slick.driver.MySQLDriver.api._
import utils.TimestampDateTimeConverter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class User(id: Option[Long], email: String, password: String, name: String, createdAt: Option[DateTime] = Option(new DateTime), role: String)

object User extends TimestampDateTimeConverter {
  implicit val jsonFormat = Json.format[User]

  val db = Database.forConfig("db.default")

  def validatePassword(id: Long, password: String): Future[Boolean] = {
    val query = for {
      user <- Tbluser
      if user.id === id.toInt
    } yield (user.id, user.passPhrase, user.salt)

    db.run(query.result.headOption).map {
      case Some((id, pass, salt)) => Algo.md5(password + salt).hex == pass
      case None => false
    }
  }

  def modifyPassword(id: Long, newPwd: String): Future[Boolean] = {
    val uuid: String = UUID.randomUUID().toString
    val now: Timestamp = new Timestamp(System.currentTimeMillis)

    val query = for {
      user <- Tbluser
      if user.id === id.toInt
    } yield (user.salt, user.passPhrase, user.lastModifiedOn)

    db.run(query.update((uuid, Algo.md5(newPwd + uuid), now))).map(rows => rows == 1)
  }

  def updateUser(theUser: User): Future[Boolean] = {
    val query = for {
      user <- Tbluser
      if user.id === theUser.id.get.toInt
    } yield (user.email, user.name, user.role)

    db.run(query.update((theUser.email, theUser.name, Some(theUser.role)))).map(rows => rows == 1)
  }


  def findOneById(id: Long): Future[Option[User]] = {

    val query = for {
      user <- Tbluser
      if user.id === id.toInt
    } yield (user.id, user.email, user.passPhrase, user.name, user.createdOn, user.role)

    db.run(query.result.headOption).map {
      case Some((id, email, pass, name, createdOn, role)) =>
        Some(User(Some(id), email, pass, name, Some(new DateTime(createdOn.getTime)), role.getOrElse("")))
      case None => None
    }

  }

  def findByEmailAndPassword(email: String, password: String): Future[Option[User]] = {

    val query = for {
      user <- Tbluser
      if user.email === email
    } yield (user.id, user.passPhrase, user.salt)

    db.run(query.result.headOption).flatMap {
      case Some((id, pass, salt)) if Algo.md5(password + salt).hex == pass => findOneById(id)
      case _ => Future.successful(None)
    }
  }

  def createUser(user: User): Future[User] = {
    val now: Timestamp = new Timestamp(System.currentTimeMillis)
    val uuid: String = UUID.randomUUID().toString

    val query = (Tbluser returning Tbluser.map(_.id) into ((user, theId) => user.copy(id = theId))) +=
      TbluserRow(id = 0, email = user.email, salt = uuid, passPhrase = Algo.md5(user.password + uuid), name = user.name, createdOn = now, lastModifiedOn = now, role = Option(user.role))

    db.run(query)
      .map(TbluserRow2User)
  }

  private def TbluserRow2User(result: Tables.TbluserRow): User = {
    User(id = Some(result.id), email = result.email, password = "", name = result.name, createdAt = Some(new DateTime(result.createdOn.getTime)), role = result.role.getOrElse(""))
  }

  def findRoleById(id: Long): Future[Option[String]] = {
    val query = for {
      user <- Tbluser
      if user.id === id.toInt
    } yield (user.role)

    db.run(query.result.head)
  }

  def findUsers(email: String): Future[List[User]] = {
    val query = email match {
      case "%" => for {
        user <- Tbluser
      } yield user
      case _ => for {
        user <- Tbluser
        if user.email.startsWith(email)
      } yield user
    }

    db.run(query.result).map(s => s.toList.map(TbluserRow2User))
  }


  def deleteUser(id: Long): Future[Boolean] = {
    db.run({
      for {
        user <- Tbluser
        if user.id === id.toInt
      } yield user
    }.delete).map(_ == 1)
  }
}
