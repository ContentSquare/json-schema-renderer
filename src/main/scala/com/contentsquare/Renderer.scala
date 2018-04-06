package com.contentsquare

import argus.schema.Schema
import argus.schema.Schema.{ItemsRoot, ListSimpleTypeTyp, Root, SimpleTypeTyp, SimpleTypes}
import scalatags.Text
import scalatags.Text.all._

object Renderer {
  private def renderType(root: Root): String =
    (root.typ, root.$ref) match {
      case (Some(SimpleTypeTyp(SimpleTypes.Array)), _) =>
        val innerType = root.items match {
          case Some(ItemsRoot(r)) =>
            " of " + renderType(r)
          case _ =>
            ""
        }
        "array" + innerType
      case (Some(SimpleTypeTyp(tp)), _) =>
        tp.name
      case (Some(ListSimpleTypeTyp(xs)), _) =>
        xs.map(_.name).mkString("", ",", "or")
      case (_, Some(ref)) =>
        ref
      case _ =>
        "unknown type"
    }

  def render(title: String, root: Root): Text.TypedTag[String] =
    html(lang := "en",
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1, shrink-to-fit=no"),
        link(href := "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css", rel := "stylesheet")
      ),
      body(
        renderDefinition(title, root),
        for (definition <- root.definitions.toList.flatten) yield renderDefinition(definition.name, definition.schema)
      )
    )

  private def renderDefinition(title: String, root: Root) = {
    div(cls := "container")(
      h1(id := title)(title),
      for (prop <- root.properties.toList.flatten) yield {
        renderProp(root, prop)
      }
    )
  }

  private def renderProp(parent: Root, prop: Schema.Field) = {
    val title = prop.schema.title.getOrElse("")
    val desc = prop.schema.description.getOrElse("")
    val link: Text.TypedTag[String] = prop.schema.$ref.map { ref =>
        a(href := ref)(s"See $ref")
    }.getOrElse(small())
    val arrayElLink = prop.schema.items match {
      case Some(ItemsRoot(r)) if r.$ref.isDefined =>
        val ref = r.$ref.get
        a(href := ref)(s"See $ref")
      case _ =>
        small()
    }

    val typ = renderType(prop.schema)
    val requirement = parent.required.map(rs =>
      if (rs.contains(prop.name)) {
        "required"
      } else {
        "optional"
      }
    ).getOrElse("optional")

    div(
      div(
        h3(prop.name),
        p(cls := "text-muted")(typ + " - " + requirement),
        p(cls := "lead")(title),
        p(desc),
        link,
        arrayElLink
      )
    )
  }
}
