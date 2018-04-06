package com.contentsquare
import java.io.File
import java.nio.file.Files

import argus.schema.Schema
import argus.schema.Schema.Field

object Example extends App {
  val schema = Schema.fromResource("/session.json")

  val check = DefinitionChecker.checkNoNestedObject(Field("root", schema), true)

  check.foreach(msg =>
    sys.error(msg)
  )

  Files.write(
    new File("index.html").toPath,
    ("<!doctype html>\n" + Renderer.render("Session", schema).toString).getBytes
  )
}
