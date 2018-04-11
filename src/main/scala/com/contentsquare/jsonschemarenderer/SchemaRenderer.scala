package com.contentsquare.jsonschemarenderer

import scalatags.Text
import scalatags.Text.TypedTag
import scalatags.Text.all._
import scala.collection.mutable
import ujson.Js

object SchemaRenderer {
  private def renderType(prop: mutable.Map[String, Js.Value]): String =
    (prop.get("type"), prop.get("$ref").map(_.str)) match {
      case (Some(Js.Str("array")), _) =>
        val innerType = prop.get("items") match {
          case Some(Js.Obj(typ)) =>
            " of " + renderType(typ)
          case _ =>
            ""
        }
        "array" + innerType
      case (Some(Js.Str(tp)), _) =>
        tp
      case (Some(Js.Arr(xs)), _) =>
        xs.map(_.str).mkString(" or ")
      case (_, Some(ref)) =>
        ref
      case _ =>
        "unknown type"
    }

  def render(title: String, root: Js.Value): TypedTag[String] = {
    val definitions = root("definitions").obj.toList
    val definitionNames = title +: definitions.map(_._1)

    Boilerplate.render(
      body(
        div(cls := "container row")(
          div(cls := "col-sm-2 position-fixed", zIndex := 100)(
            ul(cls := "nav flex-column")(
              for (name <- definitionNames) yield {
                li(cls := "nav-item")(
                  a(href := "#/definitions/" + name)(name)
                )
              }
            )
          ),
          div(cls := "col-sm-2"),
          div(cls := "col-sm-10")(
            renderDefinition(title, root.obj),
            for ((name, prop) <- definitions) yield {
              renderDefinition(name, prop.obj)
            }
          )
        )
      )
      )
  }

  private def renderDefinition(title: String, fields: mutable.Map[String, Js.Value]) = {
    val anchor = "/definitions/" + title
    val requiredFields =
      fields.get("required")
        .map(_.arr.map(_.str))
        .getOrElse(Seq.empty)
        .toSet

    Seq(
      h1(id := anchor, cls := "display-3")(title),
      dl(cls := "row")(
        for ((name, prop) <- fields("properties").obj.toList) yield {
          val required = requiredFields.contains(name)
          renderProp(name, prop.obj, required)
        }
      )
    )
  }

  private def renderProp(name: String, prop: mutable.Map[String, Js.Value], required: Boolean) = {
    val title = prop.get("title").map(_.str).getOrElse("")
    val desc = prop.get("description").map(_.str).getOrElse("")

    val link: Option[Text.TypedTag[String]] = prop.get("$ref").map { ref =>
      val refStr = ref.str
        a(href := refStr)(s"See $refStr")
    }

    val arrayElLink = prop.get("items").flatMap(_.obj.get("$ref")) match {
      case Some(r) =>
        val refStr = r.str
        Some(a(href := refStr)(s"See $refStr"))
      case _ =>
        None
    }

    val requirement = if (required) "required" else "optional"

    val typ = renderType(prop)

    val examples =
        prop.get("examples").map { exs =>
          div(
            p(cls := "text-secondary")(em("Examples:")),
            ul(cls := "list-unstyled")(
              exs.arr.map { ex =>
                li(small(pre(cls := "text-secondary")(ex.toString())))
              }
            )
          )
        }


    Seq(
      dt(cls := "col-sm-2")(
        span(cls := "badge badge-pill badge-dark")(name)
      ),
      dd(cls := "col-sm-10")(
        p(strong(title)),
        p(cls := "text-muted")(typ + " - " + requirement),
        p(desc),
        link,
        arrayElLink,
        examples
      )
    )
  }
}
