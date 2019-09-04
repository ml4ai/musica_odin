package org.clulab.musica.MusicaTestObjects

import scala.collection.mutable.ArrayBuffer

trait MusicaObj {
  def toMentionString: String
}

object AtomicObjects {

  case class Beat(b: String) extends MusicaObj {
    def toMentionString: String = toString
  }

//  // needed?
//  case class Cardinality(c: String) extends MusicaObj {
//    def toMentionString: String = toString
//  }

//  case class Chord(c: String) extends MusicaObj {
//    def toMentionString: String = toString
//  }

  case class ChordType(c: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Direction(d: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Duration(d: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Everything(e: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class LocationAbs(l: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class LocationRel(l: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Measure(m: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Pitch(p: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Proportion(p: String) extends MusicaObj {
    def toMentionString: String = toString
  }

  case class Specifier(s: String) extends MusicaObj {
    def toMentionString: String = toString
  }
}
