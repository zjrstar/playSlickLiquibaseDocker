import com.github.sbtliquibase.Import._
import com.github.sbtliquibase.SbtLiquibase
import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.rjs.Import._
import com.typesafe.sbt.web.Import._

name := """play-scala-slick-liquibase-angular-docker"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).enablePlugins(SbtLiquibase).enablePlugins(sbtdocker.DockerPlugin)

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/maven-releases/"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  json,
  // WebJars (i.e. client-side) dependencies
  "org.webjars" % "requirejs" % "2.1.17",
  "org.webjars" % "underscorejs" % "1.8.3",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars" % "bootstrap" % "3.3.4" exclude("org.webjars", "jquery"),
  "org.webjars" % "angularjs" % "1.3.15" exclude("org.webjars", "jquery"),
  "com.roundeights" %% "hasher" % "1.0.0",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "org.liquibase" % "liquibase-core" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.zaxxer" % "HikariCP" % "2.4.7",
  "mysql" % "mysql-connector-java" % "5.1.31",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)


//database setup
lazy val dbHost = System.getProperty("DB_HOST", "127.0.0.1")

lazy val dbPort = System.getProperty("DB_PORT", "3306")

val dbName: String = System.getProperty("DB_NAME", "sampledb")

val jdbcUrl: String = s"jdbc:mysql://${dbHost}:${dbPort}/${dbName}?noAccessToProcedureBodies=true&createDatabaseIfNotExist=true"

lazy val jdbcUser = System.getProperty("DB_USER", "root")

lazy val jdbcPassword = System.getProperty("DB_PASSWORD", "1q2w3e4r5t")

val jdbcDriver: String = "com.mysql.jdbc.Driver"

//liquibase setup
liquibaseUsername := jdbcUser

liquibasePassword := jdbcPassword

liquibaseDriver := jdbcDriver

liquibaseUrl := jdbcUrl

liquibaseChangelog := file("migration/master.xml")

//slick code gen
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  println(jdbcPassword)
  val outputDir = (dir / "slick").getPath // place generated files in sbt's managed sources folder
  val slickDriver = "slick.driver.MySQLDriver"
  val pkg = "net.imadz.schema.gen"
  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, jdbcUrl, outputDir, pkg, jdbcUser, jdbcPassword), s.log))
  Seq(dir / "slick" / "net" / "imadz" / "schema" / "gen" / "Tables.scala")
}

sourceGenerators in Compile <+= slickCodeGenTask

// Scala Compiler Options
scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code"
)

// Configure the steps of the asset pipeline (used in stage and dist tasks)
// rjs = RequireJS, uglifies, shrinks to one file, replaces WebJars with CDN
// digest = Adds hash to filename
// gzip = Zips all assets, Asset controller serves them automatically when client accepts them
pipelineStages := Seq(rjs, digest)
// RequireJS with sbt-rjs (https://github.com/sbt/sbt-rjs#sbt-rjs)
// ~~~
RjsKeys.paths += ("jsRoutes" -> ("/jsroutes" -> "empty:"))
//RjsKeys.mainModule := "main"
// Asset hashing with sbt-digest (https://github.com/sbt/sbt-digest)
// ~~~
// md5 | sha1
//DigestKeys.algorithms := "md5"
//includeFilter in digest := "..."
//excludeFilter in digest := "..."
// HTTP compression with sbt-gzip (https://github.com/sbt/sbt-gzip)
// ~~~
// includeFilter in GzipKeys.compress := "*.html" || "*.css" || "*.js"
// excludeFilter in GzipKeys.compress := "..."

// JavaScript linting with sbt-jshint (https://github.com/sbt/sbt-jshint)
// ~~~
// JshintKeys.config := ".jshintrc"

// All work and no play...
emojiLogs

publishArtifact in (Compile, packageDoc) := false

publishArtifact in packageDoc := false

sources in (Compile,doc) := Seq.empty

//For Dockerization
// setting a maintainer which is used for all packaging types
//maintainer := "Barry Zhong"

// exposing the play ports
//dockerExposedPorts in Docker := Seq(9000, 9443)


dockerfile in docker := {
  val appDir: File = stage.value
  val targetDir = new File("/opt/app")
  println("stage folder: " + appDir.getAbsolutePath)
  val startScriptSource: File = baseDirectory.value / "dockerization" / "start.sh"
  val startScriptTarget: File = new File((dockerPath in docker).value, "start.sh")
  startScriptSource.setExecutable(true)
  startScriptTarget.setExecutable(true)

  println("startScriptTarget: " + startScriptTarget.getAbsolutePath)

  sbt.IO.copyFile(startScriptSource,  startScriptTarget , true)

  val migrationSource: File = baseDirectory.value / "migration"
  val migrationTarget: File = new File((dockerPath in docker).value,  "migration")

  sbt.IO.copyDirectory(migrationSource,  migrationTarget , true, true)

  new Dockerfile {
    from("java")
    maintainer("Barry Zhong")
    copy(appDir, targetDir)
    copy(startScriptTarget, targetDir / "start.sh")
    copy(migrationTarget, targetDir / "migration")
    workDir(targetDir.getAbsolutePath)
    entryPoint("sh", "start.sh")
  }
}