name := "vending_machine_play"

version := "1.0"

lazy val `vending_machine_play` = (project in file(".")).enablePlugins(PlayScala).dependsOn(`vending_machine`)

lazy val `vending_machine` = RootProject(uri("git://github.com/tokno/vending_machine_scala.git"))

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
