package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._

object ComplexEvents {

  case class ChangeDuration(note: Option[Note] = None, onset: Option[Onset] = None, note2: Option[Note] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  // todo: needs multiple notes to do almost anything
  case class Convert(note: Option[Note] = None, onset: Option[Onset] = None, rest: Option[Rest] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Delete(note: Option[Note] = None, onset: Option[Onset] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  // todo: need to add a relative location
  case class Insert(note: Option[Note] = None, onset: Option[Onset] = None, rest: Option[Rest] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  // todo: needs second note to function
  case class Invert(note: Option[Note] = None, onset: Option[Onset] = None, pitch: Option[Pitch] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Repeat(note: Option[Note] = None, onset: Option[Onset] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  // todo: needs another note
  case class Replace(note: Option[Note] = None, onset: Option[Onset] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Reverse(note: Option[Note] = None, onset: Option[Onset] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  // todo: add another note
  //  case class Transpose(note: Option[Note] = None, note2: Option[Note] = None, direction: Option[Direction] = None, onset: Option[Onset] = None, step: Option[Step] = None) extends MusicaObj
  case class Transpose(note: Option[Note] = None, direction: Option[Direction] = None,
                       onset: Option[Onset] = None, step: Option[Step] = None, final_note: Option[Note] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }
}
