package com.contentsquare.jsonschemarenderer

import java.io.File
import java.nio.file.{Files, Path}
import FileUtils._
import ujson._


import scala.io.Source

object SchemaExplorer {

  private def listFiles(inputFolder: File, originalFolder: File): Vector[Path] = {
    inputFolder.listFiles(

    ).toVector.flatMap { f =>
      if (f.isFile && f.getName.endsWith(".json")) {
        Vector(originalFolder.toPath.relativize(f.toPath))
      } else if (f.isDirectory && !f.getName.startsWith(".")) {
        listFiles(f, originalFolder)
      } else {
        Vector.empty
      }
    }
  }

  private def writeSchemaHtml(schemaPath: Path, inputFolder: Path, outputFolder: Path): Unit = {
    val out = outputFolder.resolve(stripExtension(schemaPath.toString) + ".html")
    val in = inputFolder.resolve(schemaPath).toFile
    val schema = ujson.read(Source.fromFile(in).mkString)
    val html = SchemaRenderer.render(capitalize(stripExtension(schemaPath.getFileName.toString)), schema)
    Files.createDirectories(out.getParent)
    Files.write(out, html.toString().getBytes)
  }

  def writeHtml(inputFolder: File, outputFolder: File): Unit = {
    val paths = listFiles(inputFolder, inputFolder)
    paths.foreach(p => writeSchemaHtml(p, inputFolder.toPath, outputFolder.toPath))
    val indexHtml = IndexRenderer.render(paths)
    Files.write(outputFolder.toPath.resolve("index.html"), indexHtml.toString().getBytes)
  }
}
