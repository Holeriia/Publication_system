import scala.sys.process.Process

ThisBuild / scalaVersion := "3.3.6"

lazy val frontendInstall = taskKey[Unit]("Install frontend dependencies")
lazy val frontendBuild = taskKey[Unit]("Build frontend")

lazy val root = (project in file("."))
  .settings(
    version := "0.1.0-SNAPSHOT",
    name := "publicationtracker",

    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "3.13.0-M3",

      // Cats
      "org.typelevel" %% "cats-core" % "2.13.0",
      "org.typelevel" %% "cats-effect" % "3.6.1",

      // HTTP4s
      "org.http4s" %% "http4s-dsl"          % "0.23.30",
      "org.http4s" %% "http4s-ember-server" % "0.23.30",
      "org.http4s" %% "http4s-circe"        % "0.23.30",
      "org.http4s" %% "http4s-server"       % "0.23.30",

      // Circe
      "io.circe" %% "circe-core"    % "0.14.13",
      "io.circe" %% "circe-generic" % "0.14.13",
      "io.circe" %% "circe-parser"  % "0.14.13",

      // Doobie
      "org.tpolecat" %% "doobie-core"      % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC4",

      // Config
      "com.typesafe" % "config" % "1.4.3",

      // Refined
      "eu.timepit" %% "refined" % "0.11.3",

      // Logging
      "org.typelevel" %% "log4cats-slf4j" % "2.7.1",
      "ch.qos.logback" % "logback-classic" % "1.5.18",

      // WordGeneration
      "org.apache.poi" % "poi-ooxml" % "5.4.1",

      // Flyway + PostgreSQL
      "org.flywaydb" % "flyway-core" % "11.9.0",
      "org.flywaydb" % "flyway-database-postgresql" % "11.9.0",
      "org.postgresql" % "postgresql" % "42.7.6",
    ),

    // ========== Настройки для фронтенда ==========
    frontendInstall := {
      val log = streams.value.log
      log.info("Installing frontend dependencies...")
      val npm = "D:\\Programs\\notejs\\npm.cmd"
      val exitCode = Process(npm :: "install" :: Nil, file("../frontend")).!
      if (exitCode != 0) throw new Exception("Frontend install failed")
    },

    frontendBuild := {
      val log = streams.value.log
      log.info("Building frontend...")
      val npm = "D:\\Programs\\notejs\\npm.cmd"
      val exitCode = Process(npm :: "run" :: "build" :: Nil, file("../frontend")).!
      if (exitCode != 0) throw new Exception("Frontend build failed")
    },

    Compile / resourceGenerators += Def.task {
      val from = file("../frontend/dist")
      val to = (Compile / resourceManaged).value / "public"
      IO.copyDirectory(from, to)
      (to ** "*").get
    }.taskValue,

    Compile / compile := ((Compile / compile).dependsOn(frontendInstall, frontendBuild)).value
  )