name := "taitra"
 
version := "1.0" 
      
lazy val root = (project in file(".")).enablePlugins(PlayScala, PlayEbean)

unmanagedBase := baseDirectory.value / "lib"
      
scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  javaJdbc,
  caffeine,
  ws,
  "org.postgresql" % "postgresql" % "42.7.3",
  "redis.clients" % "jedis" % "5.0.2",
  "org.apache.commons" % "commons-csv" % "1.4",
  "org.apache.commons" % "commons-email" % "1.4",
  "org.apache.commons" % "commons-lang3" % "3.7",
  "xerces" % "xercesImpl" % "2.11.0",
  "org.apache.commons" % "commons-vfs2" % "2.0",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.bitbucket.b_c" % "jose4j" % "0.6.5",
  "com.amazonaws" % "aws-java-sdk-core" % "1.12.405",
  "com.amazonaws" % "aws-java-sdk-kms" % "1.12.405",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.12.405",
  "org.jsoup" % "jsoup" % "1.14.2",
  "org.apache.commons" % "commons-text" % "1.1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.1"
)

libraryDependencies += guice
libraryDependencies += filters
