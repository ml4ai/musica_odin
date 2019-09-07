package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Repeat}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestRepeat extends ExtractionTest {

  val t1 = "Repeat the G half note in measure 1"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val note = Note(Some(Duration("half")), Some(Pitch("G")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), None)
    val desired = Repeat(
      note = Some(note),
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

  val t2 = "In the second bar, the eighth notes repeat traveling up. instead of down."

  failingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("the")))
    val onset = Onset(Some(Measure("the second")), None)
    val desired = Repeat(
      note = Some(note),
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

  val t3 = "Repeat everything in bar 3"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val onset = Onset(Some(Measure("3")), None)
    val everything = Everything("everything")
    val desired = Repeat(
      onset = Some(onset),
      everything = Some(everything)
    )

    testRepeatEvent(found, desired)
  }

  // todo: needs a way to deal with number or repetitions
  val t4 = "Repeat everything three times"

  failingTest should s"extract correctly from $t4" in {
    val mentions = extractMentions(t4)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val everything = Everything("everything")
    val desired = Repeat(
      everything = Some(everything)
    )

    testRepeatEvent(found, desired)
  }

  // todo: the second bar, offset?
  val t5 = "Repeat all the half notes before the end of the second bar"

  failingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val note = Note(Some(Duration("half")), None, Some(Specifier("all the")))
    val onset = Onset(Some(Measure("the second")), None)
    val loc_rel = LocationRel("before")
    val loc_abs = LocationAbs("end")
    val desired = Repeat(
      note = Some(note),
      onset = Some(onset),
      loc_rel = Some(loc_rel),
      loc_abs = Some(loc_abs)
    )

    testRepeatEvent(found, desired)
  }

  val t6 = "Everything in the second measure should be repeated"

  failingTest should s"extract correctly from $t6" in {
    val mentions = extractMentions(t6)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val onset = Onset(Some(Measure("the second")), None)
    val everything = Everything("everything")
    val desired = Repeat(
      onset = Some(onset),
      everything = Some(everything)
    )

    testRepeatEvent(found, desired)
  }

  // todo: handle number of repetitions
  val t7 = "Everything should be repeated twice"

  failingTest should s"extract correctly from $t7" in {
    val mentions = extractMentions(t7)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val everything = Everything("everything")
    val desired = Repeat(
      everything = Some(everything)
    )

    testRepeatEvent(found, desired)
  }

  // todo: onset as duration of validity?
  val t8 = "All the quarter notes in the first two measures should be repeated"

  failingTest should s"extract correctly from $t8" in {
    val mentions = extractMentions(t8)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("All the")))
    val onset = Onset(Some(Measure("the first two")), None)
    val desired = Repeat(
      note = Some(note),
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

  // todo: deal with two notes in one
  val t9 = "Repeat the first and second quarter notes in the first bar"

  failingTest should s"extract correctly from $t9" in {
    val mentions = extractMentions(t9)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the first and second")))
    val onset = Onset(Some(Measure("the first")), None)
    val desired = Repeat(
      note = Some(note),
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

  // todo: needs 2 notes
  // this SHOULD fail but it passes! issue with test framework
  val t10 = "Repeat the first quarter note and the first eighth note in measure 2"

  passingTest should s"extract correctly from $t10" in {
    val mentions = extractMentions(t10)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the first")))
    val note_two = Note(Some(Duration("eigth")), None, Some(Specifier("the first")))
    val onset = Onset(Some(Measure("2")), None)
    val desired = Repeat(
      note = Some(note),
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

  // todo: add location of repetition (if replacement for another note?) as argument
  val t11 = "The last quarter note in the first measure should be repeated as the first note in the third measure"

  failingTest should s"extract correctly from $t11" in {
    val mentions = extractMentions(t11)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the last")))
    val note_two = Note(Some(Duration("eigth")), None, Some(Specifier("the first")))
    val onset = Onset(Some(Measure("the first")), None)
    val desired = Repeat(
      note = Some(note),
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

  // todo: add measure as arg; add location of repetition as arg
  val t12 = "Repeat the first measure as the second measure"

  failingTest should s"extract correctly from $t12" in {
    val mentions = extractMentions(t12)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val onset = Onset(Some(Measure("the first")), None)
    val desired = Repeat(
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

  // todo: add measure as arg; location of repetition
  val t13 = "Make the second measure a repetition of the first"

  failingTest should s"extract correctly from $t13" in {
    val mentions = extractMentions(t13)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val onset = Onset(Some(Measure("the second")), None)
    val desired = Repeat(
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

  // todo: add measure as arg
  val t14 = "Repeat the first three measures"

  failingTest should s"extract correctly from $t14" in {
    val mentions = extractMentions(t14)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val onset = Onset(Some(Measure("the first")), None)
    val desired = Repeat(
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }

}
