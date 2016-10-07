name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-core" % "4.3.10.Final",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.10.Final",
  "org.hibernate" % "hibernate-search-orm" % "5.3.0.Final",
  "mysql" % "mysql-connector-java" % "5.1.36",
  javaJdbc,
  cache,
  "org.projectlombok" % "lombok" % "1.16.8",
  "org.mockito" % "mockito-core" % "1.10.19",
  javaWs,
  filters,
  "org.apache.commons" % "commons-io" % "1.3.2"
)


fork in run := true