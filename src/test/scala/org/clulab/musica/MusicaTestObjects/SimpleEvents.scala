package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._

import scala.collection.mutable.ArrayBuffer

object SimpleEvents {

  case class Chord(cardinality: Option[String] = None, chordType: Option[ChordType] = None,
                   specifier: Option[Specifier] = None) extends MusicaObj {
    def toMentionString: String = {
      val args = new ArrayBuffer[String]
//      cardinality.foreach(c => args.append(c.toMentionString))
      if (cardinality.nonEmpty) args.append(s"Cardinality(${cardinality.get})")
      chordType.foreach(d => args.append(d.toMentionString))
      specifier.foreach(s => args.append(s.toMentionString))
      s"Step(${args.sorted.mkString(", ")})"
    }
  }

  case class Note(duration: Option[Duration] = None, pitch: Option[Pitch] = None, specifier: Option[Specifier] = None) extends MusicaObj {
    def this(d: String, p: String, s: String) = this(Some(Duration(d)), Some(Pitch(p)), Some(Specifier(s)))

    def toMentionString: String = {
      val args = new ArrayBuffer[String]
      duration.foreach(d => args.append(d.toMentionString))
      pitch.foreach(p => args.append(p.toMentionString))
      specifier.foreach(s => args.append(s.toMentionString))
      s"Note(${args.sorted.mkString(", ")})"
    }
  }

  case class Onset(measure: Option[Measure] = None, beat: Option[Beat] = None) extends MusicaObj {
    def toMentionString: String = {
      val args = new ArrayBuffer[String]
      if (beat.nonEmpty) args.append(beat.get.toMentionString)
      if (measure.nonEmpty) args.append(measure.get.toMentionString)
      s"Onset(${args.sorted.mkString(", ")})"
    }
  }

  case class Rest(specifier: Option[Specifier] = None, duration: Option[Duration] = None) extends MusicaObj {
    def toMentionString: String = {
      val args = new ArrayBuffer[String]
      specifier.foreach(s => args.append(s.toMentionString))
      duration.foreach(d => args.append(d.toMentionString))
      s"Step(${args.sorted.mkString(", ")})"
    }
  }

  case class Step(cardinality: Option[String], proportion: Option[String]) extends MusicaObj {
    def toMentionString: String = {
      val args = new ArrayBuffer[String]
      if (cardinality.nonEmpty) args.append(s"Cardinality(${cardinality.get})")
      if (proportion.nonEmpty) args.append(s"Proportion(${proportion.get})")
      s"Step(${args.sorted.mkString(", ")})"
    }
  }

}
