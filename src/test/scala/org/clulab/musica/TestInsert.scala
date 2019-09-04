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
}
