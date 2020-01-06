package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.Transpose
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._
import org.clulab.utils.DisplayUtils

class TestTranspose extends ExtractionTest {
  // CONVERT SUBTYPE TRANSPOSE

  val t1 = "Transpose the C4 quarter note on beat 1 of measure 1 up 5 half steps"

  passingTest should s"extract correctly from $t1" in {

    val mentions = extractMentions(t1)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents.foreach(DisplayUtils.displayMention)
    transposeEvents should have length (1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("C4")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("on")), None,
      Some(Measure("measure 1")), Some(Beat("beat 1")), None, None)
    val direction = Direction("up")
    val step = Step(cardinality = Some("5"), proportion = Some("half"))
    val desired = Transpose(
      Some(note),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)
  }


  val t2 = "Transpose the C quarter note up 1 half step"

  passingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), Some(Pitch("C")), Some(Specifier("the")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)
  }

  val t3 = "Transpose the C in measure 1 up 1 half step"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("C")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val direction = Direction("up")
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }


  // todo: no longer found by rules?
  val t5 = "The quarter note in measure 1 is transposed down 2 steps"

  failingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val direction = Direction("down")
    val step = Step(cardinality = Some("2"), None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)
  }

  val t6 = "The second note in measure 1 should be transposed down 1 step"

  passingTest should s"extract correctly from $t6" in {
    val mentions = extractMentions(t6)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, None, Some(Specifier("The second")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val direction = Direction("down")
    val step = Step(cardinality = Some("1"), None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)
  }

  //  // requires 3 notes
  //  val t7 = "Change the second note in the first measure from a G to an A"
  //
  //  passingTest should s"extract correctly from $t7" in {
  //    val mentions = extractMentions(t7)
  //    val transposeEvents = mentions.filter(_ matches "Transpose")
  //
  //    transposeEvents should have length(1)
  //    val found = transposeEvents.head
  //
  //    val original_note = Note(None, Some(Pitch("G")), Some(Specifier("a")))
  //    val onset = Onset(Some(Measure("first")), None)
  //    val step = Step(cardinality = Some("1"), None)
  //    val final_note = Note(None, Some(Pitch("A")), Some(Specifier("an")))
  //    val generic_note = Note(None, None, Some(Specifier("the second")))
  //    val desired = Transpose(
  //      original_note = Some(original_note),
  //      onset = Some(onset),
  //      step = Some(step),
  //      final_note = Some(final_note),
  //      generic_note = Some(generic_note)
  //    )
  //
  //    testTransposeEvent(found, desired)
  //  }

  // do we want to extract transpose events with no pitch OR direction of change?
  val t8 = "Move the second quarter note in measure 2 two steps"

  passingTest should s"extract correctly from $t8" in {
    val mentions = extractMentions(t8)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the second")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 2")), None, None, None)
    val step = Step(cardinality = Some("two"), None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      None,
      Some(step)
    )

    testTransposeEvent(found, desired)
  }

  // needs 2 notes
  val t10 = "Raise the second quarter note in measure 1 2 half steps to a D"

  failingTest should s"extract correctly from $t10" in {
    val mentions = extractMentions(t10)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the second")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val step = Step(cardinality = Some("2"), proportion = Some("half"))
//    val final_note = Note(None, Some(Pitch("D")), Some(Specifier("a")))
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      None,
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

  /*
  todo: Eventually we will need to deal with the entire statement at once:
  The three notes in the third bar changed. The first note switched from a half note to a
  quarter note and moved up two notes. As a result, the second not emoved further down and
  the third note switched from a quarter note to a half note and moved down four steps.
   */
//  // only examining the transposition -- should we deal with 'notes' for steps?
//  val t11 = "The first note switched from a half note to a quarter note and moved up two notes"
//
//  failingTest should s"extract correctly from $t11" in {
//    val mentions = extractMentions(t11)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(None, None, Some(Specifier("The first")))
//    val direction = Direction("up")
//    val step = Step(cardinality = Some("two"), None)
//    val desired = Transpose(
//      note = Some(note),
//      direction = Some(direction),
//      step = Some(step)
//    )
//
//    testTransposeEvent(found, desired)
//  }

  //  todo: how do we deal with this?
  //  val t13 = "In the first score, the second bar starts with those four eigth notes: G,F,G,F. In the second score, the change to G,A,G,A."
  //
  //  passingTest should s"extract correctly from $t13" in {
  //    val mentions = extractMentions(t13)
  //    val transposeEvents = mentions.filter(_ matches "Transpose")
  //
  //    transposeEvents should have length(1)
  //    val found = transposeEvents.head
  //
  //    val note = Note(Some(Duration("eighth")), None, Some(Specifier("Those four")))
  //    val onset = Onset(Some(Measure("third")), None)
  //    val desired = Transpose(
  //      note = Some(note),
  //      onset = Some(onset)
  //    )
  //
  //    testTransposeEvent(found, desired)
  //  }

  val t14 = "Naturalize the whole note in the first measure."

  passingTest should s"extract correctly from $t14" in {
    val mentions = extractMentions(t14)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("whole")), None, Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("the first measure")), None, None, None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location)
    )

    testTransposeEvent(found, desired)

  }

  // todo: determine how to deal with 'sharp'
  val t15 = "In the second line of sheet music of the first measure, the G whole note no longer is sharp."

  failingTest should s"extract correctly from $t15" in {
    val mentions = extractMentions(t15)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("whole")), Some(Pitch("G")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("of")), None, Some(Measure("the first measure")), None, None, None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location)

    )

    testTransposeEvent(found, desired)

  }

//  // todo: needs sequence handling
//  // also needs ability to include a pitch and location
//  val t16 = "The first measure 8th note run should end with a quarter note changed to G, not F. "
//
//  failingTest should s"extract correctly from $t16" in {
//    val mentions = extractMentions(t16)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
//    val onset = Onset(Some(Measure("the first")), None)
//    val pitch = Pitch("G")
//    val loc_abs = LocationAbs("end with")
//    val desired = Transpose(
//      note = Some(note),
//      onset = Some(onset),
//      loc_abs = Some(loc_abs),
//      pitch = Some(pitch)
//    )
//
//    testTransposeEvent(found, desired)
//
//  }

  // todo: needs to use pitch after note
  // todo: multiple notes in transpose events?
  val t17 = "In the first measure, move to third note G to the B above it."

  failingTest should s"extract correctly from $t17" in {
    val mentions = extractMentions(t17)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("G")), Some(Specifier("third")))
    val location = Location(Some(LocationTerm("In")), None, Some(Measure("the first measure")), None, None, None)
//    val final_note = Note(None, Some(Pitch("B")), Some(Specifier("the")))
    // todo: add 'above/below' to the list direction words for transpose events
    val direction = Direction("above")
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction)
    )

    testTransposeEvent(found, desired)

  }

  val t19 = "Everything should be moved up one step"

  passingTest should s"extract correctly from $t19" in {
    val mentions = extractMentions(t19)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Everything("Everything")
    val direction = Direction("up")
    val step = Step(cardinality = Some("one"), None)
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step),
    )

    testTransposeEvent(found, desired)

  }

  val t20 = "Transpose everything down 1 half step"

  passingTest should s"extract correctly from $t20" in {
    val mentions = extractMentions(t20)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Everything("everything")
    val direction = Direction("down")
    val step = Step(cardinality = Some("1"), Some("half"))
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step),
    )

    testTransposeEvent(found, desired)

  }

  // todo: not found properly
  val t22 = "The B sixteenth note in measure 1 beat 2 moves up two steps"

  failingTest should s"extract correctly from $t22" in {
    val mentions = extractMentions(t22)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("sixteenth")), Some(Pitch("B")), Some(Specifier("The")))
    val direction = Direction("up")
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), Some(Beat("beat 2")), None, None)
    val step = Step(cardinality = Some("two"), None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step),
    )

    testTransposeEvent(found, desired)

  }

  //  val t23 = "transpose everything to the key of G"

  val t24 = "The third whole note should be taken and moved up one step"

  passingTest should s"extract correctly from $t24" in {
    val mentions = extractMentions(t24)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("whole")), None, Some(Specifier("The third")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("one"), None)
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }


  val t25 = "The eighth note should be transposed down two steps"

  passingTest should s"extract correctly from $t25" in {
    val mentions = extractMentions(t25)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("The")))
    val direction = Direction("down")
    val step = Step(cardinality = Some("two"), None)
    val desired = Transpose(
      musicalEntity = Some(note),
      direction = Some(direction),
      step = Some(step)
    )

    testTransposeEvent(found, desired)

  }


  val t26 = "All the eighth notes should be moved up one half step"

  passingTest should s"extract correctly from $t26" in {
    val mentions = extractMentions(t26)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("All the")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("one"), Some("half"))
    val desired = Transpose(
      musicalEntity = Some(note),
      direction = Some(direction),
      step = Some(step)
    )

    testTransposeEvent(found, desired)

  }


  val t27 = "The quarter note should be moved up two steps in beat 1 of measure 1"

  passingTest should s"extract correctly from $t27" in {
    val mentions = extractMentions(t27)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("two"), None)
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), Some(Beat("beat 1")), None, None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step),
    )

    testTransposeEvent(found, desired)

  }

  // todo: needs 3 notes
  // todo: 'from an A' identified as location
  val t28 = "The first quarter note should be lowered from an A to a G"

  failingTest should s"extract correctly from $t28" in {
    val mentions = extractMentions(t28)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("The first")))
    val direction = Direction("lowered")
//    val final_note = Note(None, Some(Pitch("G")), Some(Specifier("a")))
    val desired = Transpose(
      musicalEntity = Some(note),
      direction = Some(direction)
    )

    testTransposeEvent(found, desired)

  }

  // todo: needs 3 notes and relative location
  val t29 = "The eighth note should be transposed from an A to the G above it"

  failingTest should s"extract correctly from $t29" in {
    val mentions = extractMentions(t29)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("The")))
    val direction = Direction("above")
//    val final_note = Note(None, Some(Pitch("G")), Some(Specifier("the")))
    val desired = Transpose(
      musicalEntity = Some(note),
      direction = Some(direction)
    )

    testTransposeEvent(found, desired)

  }

  // todo: add ability to have a second pitch
//  val t30 = "the eighth note should be transposed from A to G"
//
//  failingTest should s"extract correctly from $t30" in {
//    val mentions = extractMentions(t30)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(Some(Duration("eighth")), None, Some(Specifier("The")))
//    val pitch = Pitch("A")
//    val desired = Transpose(
//      musicalEntity = Some(note),
//      pitch = Some(pitch)
//    )
//
//    testTransposeEvent(found, desired)
//
//  }


  // todo: no longer captured by rules
  val t31 = "The D half note should move up 2 steps"

  failingTest should s"extract correctly from $t31" in {
    val mentions = extractMentions(t31)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("half")), Some(Pitch("D")), Some(Specifier("The")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("2"), None)
    val desired = Transpose(
      musicalEntity = Some(note),
      direction = Some(direction),
      step = Some(step)
    )

    testTransposeEvent(found, desired)

  }

//  // todo: Needs 3 notes
//  val t32 = "The eighth note should move up two tones from an A to a C"
//
//  failingTest should s"extract correctly from $t32" in {
//    val mentions = extractMentions(t32)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(Some(Duration("eighth")), None, Some(Specifier("The")))
//    val direction = Direction("up")
//    val step = Step(cardinality = Some("two"), None)
//    val final_note = Note(None, Some(Pitch("C")), Some(Specifier("a")))
//    val desired = Transpose(
//      note = Some(note),
//      direction = Some(direction),
//      step = Some(step)
//    )
//
//    testTransposeEvent(found, desired)
//
//  }

  val t33 = "The eighth note should move up two steps to a C"

  failingTest should s"extract correctly from $t33" in {
    val mentions = extractMentions(t33)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("The")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("two"), None)
//    val final_note = Note(None, Some(Pitch("C")), Some(Specifier("a")))
    val desired = Transpose(
      musicalEntity = Some(note),
      direction = Some(direction),
      step = Some(step)
//      final_note = Some(final_note)
    )

    testTransposeEvent(found, desired)

  }

  // todo: needs 3 notes
  val t34 = "The first whole note moves up from a D to an E"

  failingTest should s"extract correctly from $t34" in {
    val mentions = extractMentions(t34)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("whole")), None, Some(Specifier("The first")))
    val direction = Direction("up")
//    val final_note = Note(None, Some(Pitch("E")), Some(Specifier("an")))
    val desired = Transpose(
      musicalEntity = Some(note),
      direction = Some(direction),
//      final_note = Some(final_note)
    )

    testTransposeEvent(found, desired)

  }

  // todo: needs a way to deal with semitones
  val t35 = "All the eighth notes move up two semitones"

  failingTest should s"extract correctly from $t35" in {
    val mentions = extractMentions(t35)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("All the")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("two"), Some("semi"))
    val desired = Transpose(
      musicalEntity = Some(note),
      direction = Some(direction),
      step = Some(step)
    )

    testTransposeEvent(found, desired)

  }

  // todo: needs to deal with 'second measure'; 'sharp' in pitch
  val t36 = "in the second measure, the F sharp quarter note should be moved down two steps"

  failingTest should s"extract correctly from $t36" in {
    val mentions = extractMentions(t36)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), Some(Pitch("F sharp")), Some(Specifier("All the")))
    val direction = Direction("down")
    val step = Step(cardinality = Some("two"), None)
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("second measure")), None, None, None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t37 = "In measure 1 beat 2, move the major fourth down 2 steps"

  passingTest should s"extract correctly from $t37" in {
    val mentions = extractMentions(t37)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Chord(cardinality = Some("fourth"), Some(ChordType("major")), Some(Specifier("the")))
    val direction = Direction("down")
    val step = Step(cardinality = Some("2"), None)
    val location = Location(Some(LocationTerm("In")), None, Some(Measure("measure 1")), Some(Beat("beat 2")), None, None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  // todo: need multiple pitches
  val t38 = "Take the chord in measure 1 and change it from A and C to B and D"

  failingTest should s"extract correctly from $t38" in {
    val mentions = extractMentions(t38)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Chord(cardinality = Some("fourth"), Some(ChordType("major")), Some(Specifier("the")))
    val direction = Direction("down")
    val step = Step(cardinality = Some("2"), None)
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }


  val t39 = "Take the first whole note and move it down one step"

  passingTest should s"extract correctly from $t39" in {
    val mentions = extractMentions(t39)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("whole")), None, Some(Specifier("the first")))
    val direction = Direction("down")
    val step = Step(cardinality = Some("one"), None)
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t40 = "Take the A quarter note in measure 1 and lower it to a G"

  failingTest should s"extract correctly from $t40" in {
    val mentions = extractMentions(t40)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val direction = Direction("lower")
//    val final_note = Note(None, Some(Pitch("G")), Some(Specifier("a")))
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction)
//      final_note = Some(final_note)
    )

    testTransposeEvent(found, desired)

  }

  val t41 = "Move all the eighth notes up one half step"

  passingTest should s"extract correctly from $t41" in {
    val mentions = extractMentions(t41)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("all the")))
    val step = Step(cardinality = Some("one"), Some("half"))
    val desired = Transpose(
      musicalEntity = Some(note),
      step = Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t42 = "move the quarter note up two steps in beat 3 of measure 2"

  passingTest should s"extract correctly from $t42" in {
    val mentions = extractMentions(t42)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val step = Step(cardinality = Some("two"), None)
    val direction = Direction("up")
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 2")), Some(Beat("beat 3")), None, None)
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

//  // todo: needs 3 notes
//  val t43 = "transpose the eighth note from an A to a G"
//
//  failingTest should s"extract correctly from $t43" in {
//    val mentions = extractMentions(t43)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(Some(Duration("eighth")), None, Some(Specifier("the")))
//    val final_note = Note(None, Some(Pitch("G")), Some(Specifier("a")))
//    val desired = Transpose(
//      note = Some(note),
//      final_note = Some(final_note)
//    )
//
//    testTransposeEvent(found, desired)
//
//  }

//  // todo: needs 3 notes
//  val t44 = "transpose the eighth note from a G to the A above it"
//
//  failingTest should s"extract correctly from $t44" in {
//    val mentions = extractMentions(t44)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(Some(Duration("eighth")), None, Some(Specifier("the")))
//    val final_note = Note(None, Some(Pitch("A")), Some(Specifier("the")))
//    val loc_rel = LocationRel("above")
//    val desired = Transpose(
//      note = Some(note),
//      final_note = Some(final_note),
//      loc_rel = Some(loc_rel)
//    )
//
//    testTransposeEvent(found, desired)
//
//  }

//  // todo: needs 2 pitch values
//  val t45 = "transpose the second quarter note from F to B"
//
//  failingTest should s"extract correctly from $t45" in {
//    val mentions = extractMentions(t45)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the second")))
//    val pitch = Pitch("F")
//    val desired = Transpose(
//      note = Some(note),
//      pitch = Some(pitch)
//    )
//
//    testTransposeEvent(found, desired)
//
//  }


  val t47 = "Transpose the Eb in measure 5 down 3 half steps"

  passingTest should s"extract correctly from $t47" in {
    val mentions = extractMentions(t47)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("Eb")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 5")), None, None, None)
    val step = Step(cardinality = Some("3"), proportion = Some("half"))
    val direction = Direction("down")
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t48 = "Transpose the note on beat 3.5 of measure 5 down 1 half step"

  passingTest should s"extract correctly from $t48" in {
    val mentions = extractMentions(t48)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, None, Some(Specifier("the")))
    val location = Location(Some(LocationTerm("on")), None, Some(Measure("measure 5")), Some(Beat("beat 3.5")), None, None)
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val direction = Direction("down")
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t49 = "Transpose the C# on beat 1 up 1 half step"

  passingTest should s"extract correctly from $t49" in {
    val mentions = extractMentions(t49)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("C#")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("on")), None, None, Some(Beat("beat 1")), None, None)
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t51 = "Transpose the C up 1 half step"

  passingTest should s"extract correctly from $t51" in {
    val mentions = extractMentions(t51)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("C")), Some(Specifier("the")))
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }


  val t53 = "Transpose all the notes up 1 half step"

  passingTest should s"extract correctly from $t53" in {
    val mentions = extractMentions(t53)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, None, Some(Specifier("all the")))
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t54 = "Transpose all the notes in measure 2 down 5 half steps"

  passingTest should s"extract correctly from $t54" in {
    val mentions = extractMentions(t54)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, None, Some(Specifier("all the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 2")), None, None, None)
    val step = Step(cardinality = Some("5"), proportion = Some("half"))
    val direction = Direction("down")
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t55 =  "Transpose the quarter note up 1 half step"

  passingTest should s"extract correctly from $t55" in {
    val mentions = extractMentions(t55)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t56 = "Transpose all the quarter notes up 1 half step"

  passingTest should s"extract correctly from $t56" in {
    val mentions = extractMentions(t56)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("all the")))
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t57 = "Transpose the first C up 2 half steps"

  passingTest should s"extract correctly from $t57" in {
    val mentions = extractMentions(t57)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("C")), Some(Specifier("the first")))
    val step = Step(cardinality = Some("2"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t58 = "Transpose the second C up 2 half steps"

  passingTest should s"extract correctly from $t58" in {
    val mentions = extractMentions(t58)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("C")), Some(Specifier("the second")))
    val step = Step(cardinality = Some("2"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t59 = "Transpose everything up 5 half steps"

  passingTest should s"extract correctly from $t59" in {
    val mentions = extractMentions(t59)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Everything("everything")
    val step = Step(cardinality = Some("5"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t60 = "Transpose all the C up 5 half steps"

  passingTest should s"extract correctly from $t60" in {
    val mentions = extractMentions(t60)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("C")), Some(Specifier("all the")))
    val step = Step(cardinality = Some("5"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t61 = "Transpose all the C in measure 1 up 5 half steps"

  passingTest should s"extract correctly from $t61" in {
    val mentions = extractMentions(t61)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("C")), Some(Specifier("all the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val step = Step(cardinality = Some("5"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  val t62 = "Transpose all the Cs up 5 half steps in measure 1"

  passingTest should s"extract correctly from $t62" in {
    val mentions = extractMentions(t62)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(None, Some(Pitch("Cs")), Some(Specifier("all the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val step = Step(cardinality = Some("5"), proportion = Some("half"))
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      Some(location),
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }

  // todo: rules capture 'all the D#' and 'quarter notes' as two different notes
  val t63 = "Transpose all the D# quarter notes up one octave"

  failingTest should s"extract correctly from $t63" in {
    val mentions = extractMentions(t63)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), Some(Pitch("D#")), Some(Specifier("all the")))
    // todo: add stepsize, e.g. octave, semitone?
    val step = Step(cardinality = Some("one"), None)
    val direction = Direction("up")
    val desired = Transpose(
      Some(musicalEntity),
      None,
      Some(direction),
      Some(step)
    )

    testTransposeEvent(found, desired)

  }
}
