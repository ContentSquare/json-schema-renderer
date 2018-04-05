package com.contentsquare

import io.circe.Decoder
import io.circe.generic.semiauto._

sealed trait PropertyDefinition {
  type ExampleType
  def title: String
  def description: Option[String]
  def examples: Option[Vector[ExampleType]]
}

object PropertyDefinition {
  implicit class DecoderOps[A <: PropertyDefinition](dec: Decoder[A]) {
    def validateType(tpe: String): Decoder[A] =
      dec.validate(
        _.get[String]("type") == Right(tpe),
        s"Bad type, expected $tpe"
      )
  }

  implicit val decoder: Decoder[PropertyDefinition] =
    Decoder[ArrayPropertyDefinition].map[PropertyDefinition](identity) or
      Decoder[StringPropertyDefinition].map[PropertyDefinition](identity) or
      Decoder[IntegerPropertyDefinition].map[PropertyDefinition](identity) or
      Decoder[ReferencePropertyDefinition].map[PropertyDefinition](identity) or
      Decoder[ObjectPropertyDefinition].map[PropertyDefinition](identity) or
      Decoder[NumberPropertyDefinition].map[PropertyDefinition](identity) or
      Decoder[BooleanPropertyDefinition].map[PropertyDefinition](identity) or
      Decoder[NullablePropertyDefinition[StringPropertyDefinition]].map[PropertyDefinition](identity) or
      Decoder[NullablePropertyDefinition[IntegerPropertyDefinition]].map[PropertyDefinition](identity) or
      Decoder[NullablePropertyDefinition[NumberPropertyDefinition]].map[PropertyDefinition](identity)

}

case class ObjectPropertyDefinition(
  override val title: String,
  override val description: Option[String],
  definitions: Option[Map[String, ObjectPropertyDefinition]],
  properties: Map[String, PropertyDefinition]
) extends PropertyDefinition {
  type ExampleType = Nothing
  override def examples: Option[Vector[Nothing]] = None
}

object ObjectPropertyDefinition {
  implicit lazy val decoder: Decoder[ObjectPropertyDefinition] =
    deriveDecoder[ObjectPropertyDefinition].validateType("object")
}

case class BooleanPropertyDefinition(
  override val title: String,
  override val description: Option[String],
) extends PropertyDefinition {
  type ExampleType = Nothing
  override val examples: Option[Vector[Nothing]] = None
}


object BooleanPropertyDefinition {
  implicit val decoder: Decoder[BooleanPropertyDefinition] =
    deriveDecoder[BooleanPropertyDefinition].validateType("boolean")
}

case class StringPropertyDefinition(
  override val title: String,
  override val description: Option[String],
  override val examples: Option[Vector[String]],
  pattern: Option[String]
) extends PropertyDefinition {
  type ExampleType = String
}

object StringPropertyDefinition {
  implicit val decoder: Decoder[StringPropertyDefinition] =
    deriveDecoder[StringPropertyDefinition].validateType("string")
}

case class IntegerPropertyDefinition(
  override val title: String,
  override val description: Option[String],
  override val examples: Option[Vector[Long]],
  minimum: Option[Long],
  maximum: Option[Long],
  exclusiveMinimum: Option[Long],
  exclusiveMaximum: Option[Long]
) extends PropertyDefinition {
  type ExampleType = Long
}

object IntegerPropertyDefinition {
  implicit val decoder: Decoder[IntegerPropertyDefinition] =
    deriveDecoder[IntegerPropertyDefinition].validateType("integer")
}

case class NumberPropertyDefinition(
  override val title: String,
  override val description: Option[String],
  override val examples: Option[Vector[Double]],
  minimum: Option[Double],
  maximum: Option[Double],
  exclusiveMinimum: Option[Double],
  exclusiveMaximum: Option[Double]
) extends PropertyDefinition {
  type ExampleType = Double
}

object NumberPropertyDefinition {
  implicit val decoder: Decoder[NumberPropertyDefinition] =
    deriveDecoder[NumberPropertyDefinition].validateType("number")
}

case class ArrayPropertyDefinition(
  override val title: String,
  override val description: Option[String]
) extends PropertyDefinition {
  override type ExampleType = Nothing
  override def examples: Option[Vector[Nothing]] = None
}

object ArrayPropertyDefinition {
  implicit val decoder: Decoder[ArrayPropertyDefinition] =
    deriveDecoder[ArrayPropertyDefinition].validateType("array")
}

case class NullablePropertyDefinition[T <: PropertyDefinition : Decoder](
  override val title: String,
  override val description: Option[String],
  override val examples: Option[Vector[T]]
) extends PropertyDefinition {
  override type ExampleType = T
}

object NullablePropertyDefinition {
  implicit def decoder[T <: PropertyDefinition : Decoder]: Decoder[NullablePropertyDefinition[T]] =
    deriveDecoder[NullablePropertyDefinition[T]].validate(
      _.get[Vector[String]]("type").right.exists(_.contains("null")), // loose validation
      "Not an array containing null"
    )
}

case class ReferencePropertyDefinition(
  override val title: String
) extends PropertyDefinition {
  override type ExampleType = Nothing
  override val description: Option[String] = None
  override val examples: Option[Vector[Nothing]] = None
}

object ReferencePropertyDefinition {
  implicit val decoder: Decoder[ReferencePropertyDefinition] =
    Decoder.instance[ReferencePropertyDefinition] { c =>
      c.get[String]("$ref").map(ref =>
        ReferencePropertyDefinition(s"See the $ref definition")
      )
    }
}
