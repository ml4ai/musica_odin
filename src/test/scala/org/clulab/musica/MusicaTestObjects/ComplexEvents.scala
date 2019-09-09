package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._

object ComplexEvents {
  
  case class ChangeDuration(note: Option[Note] = None, rest: Option[Rest] = None, onset: Option[Onset] = None,
                            final_note: Option[Note] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Convert(note: Option[Note] = None, onset: Option[Onset] = None,
                     rest: Option[Rest] = None, final_note: Option[Note] = None,
                     chord: Option[Chord] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Delete(note: Option[Note] = None, rest: Option[Rest] = None, chord: Option[Chord] = None,
                    onset: Option[Onset] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  // todo: need to add a relative location
  case class Insert(note: Option[Note] = None, onset: Option[Onset] = None,
                    rest: Option[Rest] = None, chord: Option[Chord] = None,
                    loc_rel: Option[LocationRel] = None, note_prec: Option[Note] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Invert(note: Option[Note] = None, chord: Option[Chord] = None, onset: Option[Onset] = None,
                    pitch: Option[Pitch] = None, final_note: Option[Note] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Repeat(note: Option[Note] = None, chord: Option[Chord] = None, onset: Option[Onset] = None,
                    loc_rel: Option[LocationRel] = None, loc_abs: Option[LocationAbs] = None,
                    everything: Option[Everything] = None, note_two: Option[Note] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Replace(note: Option[Note] = None, chord: Option[Chord] = None, onset: Option[Onset] = None,
                     final_note: Option[Note] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Reverse(note: Option[Note] = None, chord: Option[Chord] = None,
                     onset: Option[Onset] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  // todo: add another note: e.g. the first note is changed from note to note
  case class Transpose(note: Option[Note] = None, direction: Option[Direction] = None,
                       onset: Option[Onset] = None, step: Option[Step] = None, chord: Option[Chord] = None,
                       final_note: Option[Note] = None, loc_rel: Option[LocationRel] = None,
                       loc_abs: Option[LocationAbs] = None, everything: Option[Everything] = None,
                       pitch: Option[Pitch] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }
}
