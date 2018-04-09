package com.contentsquare.jsonschemarenderer

object FileUtils {
  def stripExtension(fileName: String): String =
    fileName
      .reverse
      .dropWhile(_ != '.')
      .drop(1)
      .reverse

  def capitalize(str: String): String =
    str.head.toUpper +: str.tail
}
