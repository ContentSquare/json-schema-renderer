package com.contentsquare

import argus.schema.Schema.{Field, ItemsRoot, ListSimpleTypeTyp, Root, SimpleTypeTyp, SimpleTypes}

object DefinitionChecker {
  private def isObject(root: Root): Boolean =
    root.typ.contains(SimpleTypeTyp(SimpleTypes.Object))

  private def isArray(root: Root): Boolean =
    root.typ.contains(SimpleTypeTyp(SimpleTypes.Array))

  def checkNoNestedObject(field: Field, isRoot: Boolean): Option[String] = {
    val selfCheck: Option[String] =
      if (isRoot && !isObject(field.schema)) {
        Some("The schema root should be an object")
      } else if (!isRoot && isObject(field.schema)) {
        Some(s"Please move the object definition for ${field.name}")
      } else if (isArray(field.schema)) {
        field.schema.items match {
          case Some(ItemsRoot(root)) if isObject(root) =>
            Some(s"Please move the object definitions for ${field.name} to definitions")
          case _ =>
            None
        }
      } else {
        None
      }

    val definitionsCheck: Option[String] =
      field.schema.definitions.toList.flatten.foldLeft[Option[String]](None) { case (acc, definition) =>
        acc.orElse(checkNoNestedObject(definition, isRoot = isRoot))
      }

    val propsChecks: Option[String] =
      field.schema.properties.toList.flatten.foldLeft[Option[String]](None) { case (acc, prop) =>
        acc.orElse(checkNoNestedObject(prop, isRoot = false))
      }

    selfCheck orElse definitionsCheck orElse propsChecks
  }
}
