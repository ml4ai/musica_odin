package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.ChangeDuration
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.TestUtils._

class TestChangeDuration extends ExtractionTest {

  val t1 = "Shorten the quarter note on beat 1 of measure 2"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val onset = Onset(Some(Measure("2")), Some(Beat("1")))
    val desired = ChangeDuration(
      note = Some(note),
      onset = Some(onset)
    )

    testChangeDurationEvent(found, desired)
  }

  val t2 = "Shorten the half note on the first beat of the second measure"

  passingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    val note = Note(Some(Duration("half")), None, Some(Specifier("the")))
    val onset = Onset(Some(Measure("second")), Some(Beat("first")))
    val desired = ChangeDuration(
      note = Some(note),
      onset = Some(onset)
    )

    testChangeDurationEvent(found, desired)
  }

  val t3 = "The quarter note on beat 1 of measure 2 is shortened"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
    val onset = Onset(Some(Measure("2")), Some(Beat("1")))
    val desired = ChangeDuration(
      note = Some(note),
      onset = Some(onset)
    )

    testChangeDurationEvent(found, desired)
  }

  val t4 = "The quarter note is shortened in beat 1 of measure 2"

  passingTest should s"extract correctly from $t4" in {
    val mentions = extractMentions(t4)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
    val onset = Onset(Some(Measure("2")), Some(Beat("1")))
    val desired = ChangeDuration(
      note = Some(note),
      onset = Some(onset)
    )

    testChangeDurationEvent(found, desired)
  }

//  Cannot yet handle 2 notes OR 'cardinals' in note
  val t5 = "The first quarter note should be shortened to an eighth note"

  passingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("The first")))
    val onset = Onset(None, None)
    val note2 = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
    val desired = ChangeDuration(
      note = Some(note),
      onset = Some(onset),
      note2 = Some(note2)
    )

    testChangeDurationEvent(found, desired)
  }

  //  Cannot yet handle 2 notes OR 'cardinals' in note
  val t6 = "Shorten the quarter note in measure 1 to an eighth note"

  passingTest should s"extract correctly from $t6" in {
    val mentions = extractMentions(t6)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), None)
    val note2 = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
    val desired = ChangeDuration(
      note = Some(note),
      onset = Some(onset),
      note2 = Some(note2)
    )

    testChangeDurationEvent(found, desired)
  }

  //  Shortened version of t6 that's known to pass
  val t7 = "Shorten the quarter note in measure 1"

  passingTest should s"extract correctly from $t7" in {
    val mentions = extractMentions(t7)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), None)
    val desired = ChangeDuration(
      note = Some(note),
      onset = Some(onset)
    )

    testChangeDurationEvent(found, desired)
  }


  val t8 = "Shorten all the half notes"

  passingTest should s"extract correctly from $t8" in {
    val mentions = extractMentions(t8)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    val note = Note(Some(Duration("half")), None, Some(Specifier("all the")))
    val onset = Onset(None, None)
    val desired = ChangeDuration(
      note = Some(note)
    )

    testChangeDurationEvent(found, desired)
  }

  // needs to include specifier everything
  val t9 = "Everything should be shortened from half notes to quarter notes"

  failingTest should s"extract correctly from $t9" in {
    val mentions = extractMentions(t9)
    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")

    changeDurationEvents should have length(1)
    val found = changeDurationEvents.head

    // val spec = Specifier("everything")
    val note = Note(Some(Duration("half")), None, Some(Specifier("all the")))
    val onset = Onset(None, None)
    val desired = ChangeDuration(
      note = Some(note),
      onset = Some(onset)
    )

    testChangeDurationEvent(found, desired)
  }
}
