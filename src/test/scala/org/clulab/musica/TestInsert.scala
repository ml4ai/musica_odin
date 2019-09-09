package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Insert}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._


class TestInsert extends ExtractionTest {

  // needs chord + 2 notes?
  // needs absolute location
  // todo: finish this
  val t1 = "This is simply adding a simple chord at the end by adding an e at the same time with the c."

  failingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), Some(Pitch("B")), Some(Specifier("the")))
    val desired = Insert(
      note = Some(note)
    )

    testInsertEvent(found, desired)
  }

  val t2 = "Insert an A quarter note after the D in measure 3"

  failingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("an")))
    val loc_rel = LocationRel("after")
    val note_prec = Note(None, Some(Pitch("D")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("3")), None)
    val desired = Insert(
      note = Some(note),
      onset = Some(onset),
      loc_rel = Some(loc_rel),
      note_prec = Some(note_prec)
    )

    testInsertEvent(found, desired)
  }

  val t3 = "Insert a major fourth before the minor seventh"

  val t4 = "Add another eighth note to the end of the run in measure 5"

  // todo: needs 3 notes
  val t5 = "Add a C quarter note between the G half note and the A half note"

  // todo: needs 3-4 notes + rest
  val t6 = "Go to the end of the score and add a measure with three quarter notes: A, B, and A. Then add a quarter rest."

  // todo: add 'measure' as option
  val t7 = "Add a new bar to the start of the score."

  // todo: how to deal with this? should this actually just be a 'replace' event?
  val t8 = "Add an eighth rest at the start of the space where you just removed a quarter note."

  val t9 = "Insert a quarter note and a quarter rest after the last eighth note"

  val t10 = "Insert two eighth notes at the end of the second measure"
}
