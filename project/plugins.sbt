//Liquibase
resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.sbtliquibase" % "sbt-liquibase" % "0.1.0-SNAPSHOT")

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/maven-releases/"

//Docker
resolvers += "Xerial repository" at "http://repo1.maven.org/maven2/org/xerial/sbt/"

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.3.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.8.0-RC2")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.7.9")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.7")

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.8")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.6")

