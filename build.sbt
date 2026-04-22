ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "RuleEngine",
    libraryDependencies ++= Seq(
      "mysql"                 %  "mysql-connector-java" % "8.0.33",
      "com.github.tototoshi" %% "scala-csv"             % "1.3.10"
    )
  )