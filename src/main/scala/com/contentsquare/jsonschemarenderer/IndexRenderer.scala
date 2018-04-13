package com.contentsquare.jsonschemarenderer

import java.nio.file.Path

import scalatags.Text.TypedTag
import scalatags.Text.all._
import FileUtils._

import scala.collection.mutable.ArrayBuffer

object IndexRenderer {
  sealed abstract class FileTree
  case class DirectoryNode(name: String, children: Vector[FileTree]) extends FileTree
  case class FileNode(path: Path) extends FileTree

  case class FileWithPath(dirs: Vector[String], fullPath: Path) {
    lazy val down: FileWithPath = FileWithPath(dirs.tail, fullPath)
  }

  object FileTree {
    def fromSegments(rootName: String, paths: Seq[FileWithPath]): DirectoryNode = {
      val (fileSegments, directorySegments) = paths.partition(_.dirs.isEmpty)
      val fileNodes = fileSegments.map(fwp => FileNode(fwp.fullPath)).toVector
      val directoryNodes = directorySegments.groupBy(_.dirs.head).values.map { fwp =>
        val rootName = fwp.head.dirs.head
        fromSegments(rootName, fwp.map(_.down))
      }.toVector

      DirectoryNode(rootName , fileNodes ++ directoryNodes)
    }
  }

  private def segment(path: Path): FileWithPath = {
    val segments = ArrayBuffer[String]()
    path.iterator().forEachRemaining(p => segments.append(p.toString))
    FileWithPath(segments.init.toVector, path)
  }

  private def renderTree(directoryNode: DirectoryNode, indent: Int): TypedTag[String] =
    div(
      p(" " * 2 * indent + directoryNode.name),
      for (file <- directoryNode.children.collect { case FileNode(file) => file }) yield {
        val name = stripExtension(file.getFileName.toString)
        val htmlFile = stripExtension(file.toString) + ".html"
        p(
          a(href := htmlFile)(" " * 2 * (indent + 1) + name)
        )
      },
      for (dir <- directoryNode.children.collect { case d@DirectoryNode(_, _) => d }) yield {
        renderTree(dir, indent + 1)
      }
    )

  def render(generatedFiles: Seq[Path]): TypedTag[String] = {
    val tree = FileTree.fromSegments("schemas", generatedFiles.map(segment))

    Boilerplate.render(
      body(
        div(cls := "container")(
          h1("Schemas"),
          pre(
            code(
              renderTree(tree, 0)
            )
          )
        )
      )
    )
  }
}
