package com.contentsquare.jsonschemarenderer

import scalatags.Text
import scalatags.Text.TypedTag
import scalatags.Text.all._
import scala.collection.mutable
import ujson.Js

object SchemaRenderer {
  private val validationFields = Set(
    "maximum",
    "exclusiveMaximum",
    "minimum",
    "exclusiveMinimum",
    "maxLength",
    "minLength",
    "pattern",
    "maxItems",
    "minItems",
    "uniqueItems",
    "maxProperties",
    "minProperties",
  )

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

    val allDefinitions = (title -> root) :: definitions

    Boilerplate.render(
      body(cls := "pt-5 pl-3")(
        div(cls := "container row")(
          renderNav(allDefinitions),
          div(cls := "col-sm-9 border-left pl-5")(
            renderDefinition(title, root.obj),
            for ((name, prop) <- definitions) yield {
              renderDefinition(name, prop.obj)
            }
          )
        )
      )
      )
  }

  private def renderNav(definitionNames: List[(String, Js.Value)]) = {
    Seq(
      div(id := "nav-accordion", cls := "col-sm-3")(
        for ((name, prop) <- definitionNames) yield {
          val collapseId = s"collapse-def-$name"
          val mainDef: TypedTag[String] =
            div(cls := "card-header text-dark", id := s"def-$name")(
              h5(cls := "mb-0")(
                span(
                  role := "button",
                  cls := "btn dropdown-toggle text-white",
                  data("toggle") := "collapse",
                  data("target") := "#" + collapseId,
                  aria.expanded := "false",
                  aria.controls := collapseId
                )(name)
              )
            )

          val props: TypedTag[String] =
            div(id := collapseId, cls := "collapse", data("parent") := "#nav-accordion")(
              div(cls := "card-body")(
                ul(cls := "nav flex-column")(
                  for (propName <- prop.obj.get("properties").toList.flatMap(_.obj.keys)) yield {
                    li(cls := "nav-item pl-3")(
                      a(href := s"#/definitions/$name/$propName")(small(cls := "text-white")(propName))
                    )
                  }
                )
              )
            )

          div(cls := "card border-light text-white bg-dark")(
            mainDef,
            props
          )
        }

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

    div(
      h1(id := anchor, cls := "display-3 pb-3")(title),
      dl(cls := "row")(
        for ((name, prop) <- fields("properties").obj.toList) yield {
          val required = requiredFields.contains(name)
          renderProp(name, prop.obj, required, title)
        }
      )
    )
  }

  private def renderProp(name: String, prop: mutable.Map[String, Js.Value], required: Boolean, parentName: String) = {
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

    val validations = {
      val els = prop.filterKeys(validationFields.contains).toList.map { case (k, v) =>
        p(strong(k + ": "), v.toString())
      }

      if (els.nonEmpty) {
        Some(
          p(cls := "text-secondary")(em("Validations:")) +: els
        )
      } else {
        None
      }
    }


    Seq(
      dt(cls := "col-sm-3")(
        span(id := s"/definitions/$parentName/$name", cls := "badge badge-pill badge-dark")(name)
      ),
      dd(cls := "col-sm-9")(
        p(strong(title)),
        p(cls := "text-muted")(typ + " - " + requirement),
        p(desc),
        link,
        arrayElLink,
        validations,
        examples
      )
    )
  }
}
