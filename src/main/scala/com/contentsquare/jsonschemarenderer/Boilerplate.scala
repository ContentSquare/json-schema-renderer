package com.contentsquare.jsonschemarenderer

import scalatags.Text.TypedTag
import scalatags.Text.all._

object Boilerplate {
  def render(htmlBody: TypedTag[String]): TypedTag[String] =
    html(lang := "en",
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1, shrink-to-fit=no"),
        link(href := "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css", rel := "stylesheet")
      ),
      htmlBody
    )
}
