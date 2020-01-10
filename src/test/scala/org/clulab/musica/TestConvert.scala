package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.Convert
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.TestUtils._

class TestConvert extends ExtractionTest {

  // CONVERT NO SUBTYPE

  // todo: not picking up on "in measure 1" as a location --> but works in webapp
  val t1 = "Convert the quarter note in measure 1 into a quarter rest"

  failingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val sourceLocation = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val destEntity = Rest(Some(Specifier("a")), Some(Duration("quarter")))
    val desired = Convert(
      Some(sourceEntity),
      Some(sourceLocation),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

//  // only dealing with the conversion event
//  // this one will be very hard to do
//  // todo: relative location needed
//  val t47 = "In the first measure, after the C quarter note, take the D eighth note out and change it to an eighth note rest."
//
//  failingTest should s"extract correctly from $t47" in {
//    val mentions = extractMentions(t47)
//    val convertEvents = mentions.filter(_ matches "Convert")
//
//    convertEvents should have length(1)
//    val found = convertEvents.head
//
//    val note = Note(Some(Duration("eighth")), Some(Pitch("D")), Some(Specifier("the")))
//    val onset = Onset(Some(Measure("the first")), None)
//    val rest = Rest(Some(Specifier("an")), Some(Duration("eighth")))
//    val desired = Convert(
//      note = Some(note),
//      onset = Some(onset),
//      rest = Some(rest)
//    )
//
//    testConvertEvent(found, desired)
//  }
//
  // todo: 'change X into' --> into marked as location
  val t3 = "Change the D quarter note into a minor fifth"

  failingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), Some(Pitch("D")), Some(Specifier("the")))
    val destEntity = Chord(cardinality = Some("fifth"), Some(ChordType("minor")), Some(Specifier("a")))
    val desired = Convert(
      Some(sourceEntity),
      None,
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

  val t4 = "Turn all the whole notes into whole rests"

  passingTest should s"extract correctly from $t4" in {
    val mentions = extractMentions(t4)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("whole")), None, Some(Specifier("all the")))
    val destEntity = Rest(None, Some(Duration("whole")))
    val desired = Convert(
      Some(sourceEntity),
      None,
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

  // todo: measure 1 issue again
  val t5 = "The G should be converted to an augmented second in measure 1."

  failingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(None, Some(Pitch("G")), Some(Specifier("the")))
    val sourceLocation = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val destEntity = Chord(cardinality = Some("second"), Some(ChordType("augmented")), Some(Specifier("an")))
    val desired = Convert(
      Some(sourceEntity),
      Some(sourceLocation),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

  // todo: measure 1 problem? found in event mention but not as a location mention
  val t6 = "In measure 1, the G should be converted to an augmented second."

  failingTest should s"extract correctly from $t6" in {
    val mentions = extractMentions(t6)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(None, Some(Pitch("G")), Some(Specifier("the")))
    val sourceLocation = Location(Some(LocationTerm("In")), None, Some(Measure("measure 1")), None, None, None)
    val destEntity = Chord(cardinality = Some("second"), Some(ChordType("augmented")), Some(Specifier("an")))
    val desired = Convert(
      Some(sourceEntity),
      Some(sourceLocation),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }


  //todo: needs extra note handling
  val t7 = "Change the first and last notes into two chords"


  // todo: entities and location mentions found as part of event mention, but not appearing as separate?
  val t8 = "After the C quarter note in measure 2, convert the G into a quarter rest"

  failingTest should s"extract correctly from $t8" in {
    val mentions = extractMentions(t8)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(None, Some(Pitch("G")), Some(Specifier("the")))
    val sourceLocation = Location(Some(LocationTerm("after")),
      Some(Note(Some(Duration("quarter")), Some(Pitch("C")), Some(Specifier("the")))),
      Some(Measure("measure 1")), None, None, None)
    val destEntity = Rest(Some(Specifier("a")), Some(Duration("quarter")))
    val desired = Convert(
      Some(sourceEntity),
      Some(sourceLocation),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

  // CONVERT SUBTYPE CHANGE DURATION

  val t9 = "Shorten the C in measure 1 to a quarter note"

  passingTest should s"extract correctly from $t9" in {
    val mentions = extractMentions(t9)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(None, Some(Pitch("C")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val destEntity = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val desired = Convert(
      Some(sourceEntity),
      Some(location),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

  val t10 = "Shorten the C after the half note in measure 1 to a quarter note"

  passingTest should s"extract correctly from $t10" in {
    val mentions = extractMentions(t10)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(None, Some(Pitch("C")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("after")), Some(Note(Some(Duration("half")), None, Some(Specifier("the")))),
      Some(Measure("measure 1")), None, None, None)
    val destEntity = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val desired = Convert(
      Some(sourceEntity),
      Some(location),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }


  val t11 = "Shorten the quarter note on beat 1 of measure 2"

  passingTest should s"extract correctly from $t11" in {
    val mentions = extractMentions(t11)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val location = Location(Some(LocationTerm("on")), None, Some(Measure("measure 2")), Some(Beat("beat 1")),
      None, None)
    val desired = Convert(
      Some(sourceEntity),
      Some(location)
    )

    testConvertEvent(found, desired)
  }

  val t12 = "Shorten the half note on the first beat of the second measure"

  passingTest should s"extract correctly from $t12" in {
    val mentions = extractMentions(t12)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("half")), None, Some(Specifier("the")))
    val location = Location(Some(LocationTerm("on")), None, Some(Measure("the second measure")), Some(Beat("the first beat")),
      None, None)
    val desired = Convert(
      Some(sourceEntity),
      Some(location)
    )

    testConvertEvent(found, desired)
  }

  val t13 = "The quarter note on beat 1 of measure 2 is shortened"

  passingTest should s"extract correctly from $t13" in {
    val mentions = extractMentions(t13)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
    val location = Location(Some(LocationTerm("on")), None, Some(Measure("measure 2")), Some(Beat("beat 1")),
      None, None)
    val desired = Convert(
      Some(sourceEntity),
      Some(location)
    )

    testConvertEvent(found, desired)
  }

  val t14 = "The quarter note is shortened in beat 1 of measure 2"

  passingTest should s"extract correctly from $t14" in {
    val mentions = extractMentions(t14)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 2")), Some(Beat("beat 1")),
      None, None)

    val desired = Convert(
      Some(sourceEntity),
      Some(location)
    )

    testConvertEvent(found, desired)
  }

//  //  Cannot yet handle 2 notes OR 'cardinals' in note
  val t15 = "The first quarter note should be shortened to an eighth note"

  passingTest should s"extract correctly from $t15" in {
    val mentions = extractMentions(t15)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("The first")))
    val destEntity = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
    val desired = Convert(
      Some(sourceEntity),
      None,
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

  //  Cannot yet handle 2 notes OR 'cardinals' in note
  val t16 = "Shorten the quarter note in measure 1 to an eighth note"

  passingTest should s"extract correctly from $t16" in {
    val mentions = extractMentions(t16)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val destEntity = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
    val desired = Convert(
      Some(sourceEntity),
      Some(location),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

  //  Shortened version of t6 that's known to pass
  val t17 = "Shorten the quarter note in measure 1"

  passingTest should s"extract correctly from $t17" in {
    val mentions = extractMentions(t17)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val desired = Convert(
      Some(sourceEntity),
      Some(location)
    )

    testConvertEvent(found, desired)
  }


  val t18 = "Shorten all the half notes"

  passingTest should s"extract correctly from $t18" in {
    val mentions = extractMentions(t18)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("half")), None, Some(Specifier("all the")))
    val desired = Convert(
      Some(sourceEntity)
    )

    testConvertEvent(found, desired)
  }

//  // todo: needs to include specifier everything
//  val t57 = "Everything should be shortened from half notes to quarter notes"

  val t19 = "The half notes should be shortened to quarter notes"

  passingTest should s"extract correctly from $t19" in {
    val mentions = extractMentions(t19)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("half")), None, Some(Specifier("The")))
    val destEntity = Note(Some(Duration("quarter")), None, None)

    val desired = Convert(
      Some(sourceEntity),
      None,
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

//  // only testing duration change here; todo: needs 3 notes
//  val t58 = "The first note switched from a half note to a quarter note and moved up two notes."
//
//  failingTest should s"extract correctly from $t58" in {
//    val mentions = extractMentions(t58)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    // val spec = Specifier("everything")
//    val note = Note(None, None, Some(Specifier("The first")))
//    val final_note = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
//    val desired = ChangeDuration(
//      note = Some(note),
//      final_note = Some(final_note)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
  val t21 = "Change the half note in measure 1"

  passingTest should s"extract correctly from $t21" in {
    val mentions = extractMentions(t21)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length (1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("half")), None, Some(Specifier("the")))
    val sourceLocation = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val desired = Convert(
      Some(sourceEntity),
      Some(sourceLocation)
    )

    testConvertEvent(found, desired)
  }


  val t22 = "Change the half rest in measure 1"

  passingTest should s"extract correctly from $t22" in {
    val mentions = extractMentions(t22)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length (1)
    val found = convertEvents.head

    val sourceEntity = Rest(Some(Specifier("the")), Some(Duration("half")))
    val sourceLocation = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val desired = Convert(
      Some(sourceEntity),
      Some(sourceLocation)
    )

    testConvertEvent(found, desired)
  }

  val t23 = "Change the G quarter note to an F"

  passingTest should s"extract correctly from $t23" in {
    val mentions = extractMentions(t23)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), Some(Pitch("G")), Some(Specifier("the")))
    val destEntity = Note(None, Some(Pitch("F")), Some(Specifier("an")))
    val desired = Convert(
      Some(sourceEntity),
      None,
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

    val t24 = "The three notes in the third bar changed."

    failingTest should s"extract correctly from $t24" in {
      val mentions = extractMentions(t24)
      val transposeEvents = mentions.filter(_ matches "Transpose")

      transposeEvents should have length(1)
      val found = transposeEvents.head

      val sourceEntity = Note(None, None, Some(Specifier("The three")))
      val sourceLocation = Location(Some(LocationTerm("in")), None, Some(Measure("the third bar")), None, None, None)
      val desired = Convert(
        Some(sourceEntity),
        Some(sourceLocation)
      )

      testConvertEvent(found, desired)
    }

  val t25 = "The A quarter note in measure 1 should be taken and changed to a G"

  failingTest should s"extract correctly from $t25" in {
    val mentions = extractMentions(t25)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val sourceEntity = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("The")))
    val sourceLocation = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val destEntity = Note(None, Some(Pitch("G")), Some(Specifier("a")))
    val desired = Convert(
      Some(sourceEntity),
      Some(sourceLocation),
      Some(destEntity)
    )

    testConvertEvent(found, desired)

  }


  //  // todo: requires 3 notes, and recognizing lowercase pitch
//  val t9 = "In the 3rd bar you want to change the 3rd note from a d to an a"
//
//  failingTest should s"extract correctly from $t9" in {
//    val mentions = extractMentions(t9)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val musicalEntity = Note(None, None, Some(Specifier("the 3rd")))
//    val location = Location(Some(LocationTerm("In")), None, Some(Measure("3rd bar")), None, None, None)
//    val step = Step(cardinality = Some("two"), None)
//    val desired = Transpose(
//      Some(musicalEntity),
//      Some(location),
//      None,
//      Some(step)
//    )
//
//    testTransposeEvent(found, desired)
//  }
//
//  // todo: Needs 3 notes
//  val t18 = "the half note in measure 1 should be taken and changed from an A to a D"
//
//  failingTest should s"extract correctly from $t18" in {
//    val mentions = extractMentions(t18)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(Some(Duration("half")), None, Some(Specifier("the")))
//    val onset = Onset(Some(Measure("1")), None)
//    val final_note = Note(None, Some(Pitch("D")), Some(Specifier("a")))
//    val desired = Transpose(
//      note = Some(note),
//      onset = Some(onset),
//      final_note = Some(final_note)
//    )
//
//    testTransposeEvent(found, desired)
//
//  }
}
