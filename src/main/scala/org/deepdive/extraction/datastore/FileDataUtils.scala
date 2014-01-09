package org.deepdive.extraction.datastore

import org.deepdive.utils.FileUtils
import org.deepdive.Logging
import au.com.bytecode.opencsv.CSVReader
import scala.io.Source
import spray.json._
import spray.json.DefaultJsonProtocol._
import scala.collection.JavaConversions._

object FileDataUtils extends Logging {

  def queryAsJson[A](fileGlob: String, sep: Char)(block: Iterator[JsValue] => A) : A = {
    val files = FileUtils.absoluteFileOrGlob(fileGlob)
    
    // TODO: readAll loads the data into memory, this should be an iterator
    // Unfortunately the CSV library doesn't support that.
    // Either implement our own iterator or use a different library
    val iterator = files.iterator.map { filename =>
      val reader = new CSVReader(Source.fromFile(filename).reader, sep)
      try {
        reader.readAll.map(_.toJson)
      } finally {
        reader.close()
      }
    }.flatten
    block(iterator)


  }
    

}