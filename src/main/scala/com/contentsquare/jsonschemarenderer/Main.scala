package com.contentsquare.jsonschemarenderer

import java.io.File

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.println("Usage: sbt run INPUT_FOLDER OUTPUT_FOLDER")
      sys.exit(1)
    }

    val folders = args.map(new File(_))

    if (!folders(0).isDirectory) {
      System.err.println("The input folder doesn't exist or is not a directory")
      sys.exit(1)
    }

    if (folders(1).exists() && !folders(1).isDirectory) {
      System.err.println("The output folder is not a directory")
      sys.exit(1)
    }

    SchemaExplorer.writeHtml(folders(0), folders(1))
  }
}
