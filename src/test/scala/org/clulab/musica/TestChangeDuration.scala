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

  failingTest should s"extract correctly from $t2" in {
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

  failingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
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
}
