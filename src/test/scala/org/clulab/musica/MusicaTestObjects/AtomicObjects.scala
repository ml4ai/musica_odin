package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.SimpleEvents.{Axis,MusicalEntity}

import scala.collection.mutable.ArrayBuffer

trait MusicaObj {
  def toMentionString: String
}

object AtomicObjects {

  case class Beat(b: String) extends MusicaObj {
    def toMentionString: String = toString
//    def toMentionString: String = convertOrd2Int(toString)
  }

  // needed?
  case class Cardinality(c: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class ChordType(c: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Direction(d: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Duration(d: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Everything(e: String) extends MusicaObj with MusicalEntity {
    def toMentionString: String = toString
  }

  case class Frequency(f: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class LocationTerm(l: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Measure(m: String) extends MusicaObj {
    def toMentionString: String = toString
//    def toMentionString: String = convertOrd2Int(toString)
  }

  case class Pitch(p: String) extends MusicaObj with Axis {
    def toMentionString: String = toString
  }

  case class Proportion(p: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Specifier(s: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  // helper method
  // change ordinals to string-typed ints for beats and measures
  def convertOrd2Int(found: String): String = {
    val ord2IntMap = Map("first" -> "1", "second" -> "2", "third" -> "3", "fourth" -> "4", "1st" -> "1",
                        "2nd" -> "2", "3rd" -> "3", "4th" -> "4")
//    println("This was found: " + found)
    if (ord2IntMap.contains(found)) {
//      println("This worked and converted " + found + " to:")
//      println(ord2IntMap(found))
      return ord2IntMap(found)
    } else {
      return found
    }
  }
}
