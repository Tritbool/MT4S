package com.tritcorp.mt4s.utils
/* MT4S - Multiple Tests 4 Spark - a simple Junit/Scalatest testing framework for spark
* Copyright (C) 2018  Gauthier LYAN
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
import java.io.File

/**
  * An utility to parse folders and find files within it
  */
object Files {

  def getFileTree(f: File): Stream[File] =
    f #:: (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
    else Stream.empty)
  

  def getAllFilesStartingWith(tree:Stream[File], start: String): Stream[File] = {
    tree.filter(_.getName.startsWith(start))
  }

  def getAllFilesEndingWith(tree:Stream[File], end: String): Stream[File] = {
    tree.filter(_.getName.endsWith(end))
  }

  def getAllFilesWith(tree:Stream[File], content: String): Stream[File] = {
    val res = tree.filter(_.getName.contains(content))
    res.force
  }

  def getAllFilesEquals(tree:Stream[File], toFind: String, ignoreCase: Boolean = false): Stream[File] = {
    var res:Stream[File]=Stream.empty
    if (ignoreCase) {
      res = tree.filter(_.getName.equalsIgnoreCase(toFind))
    }else {
      res = tree.filter(_.getName.equals(toFind))
    }
    res.force
  }

}