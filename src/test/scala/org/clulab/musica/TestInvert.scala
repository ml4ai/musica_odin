package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Invert}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._


class TestInvert extends ExtractionTest {

  val t1 = "Invert all the half notes around middle C"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)

    val invertEvents = mentions.filter(_ matches "Invert")

    invertEvents should have length(1)
    val found = invertEvents.head

    val sourceEntity = Note(Some(Duration("half")), None, Some(Specifier("all the")))

    val axis = Note(None, Some(Pitch("middle C")), None)

    val desired = Invert(
      Some(sourceEntity),
      None,
      Some(axis)
    )

    testInvertEvent(found, desired)

  }

  // todo: grammar currently identifies "the first two measures around B4" as a NOTE
  val t2 = "Invert the notes in the first two measures around B4"

  failingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)

    val invertEvents = mentions.filter(_ matches "Invert")

    invertEvents should have length(1)
    val found = invertEvents.head

    val sourceEntity = Note(None, None, Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("the first two measures")), None, None, None)
    val axis = Note(None, Some(Pitch("B4")), None)

    val desired = Invert(
      Some(sourceEntity),
      Some(location),
      Some(axis)
    )

    testInvertEvent(found, desired)
  }

  val t3 = "All the notes should be inverted around A"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)

    val invertEvents = mentions.filter(_ matches "Invert")

    invertEvents should have length(1)
    val found = invertEvents.head

    val sourceEntity = Note(None, None, Some(Specifier("All the")))
    val axis = Note(None, Some(Pitch("A")), None)

    val desired = Invert(
      Some(sourceEntity),
      None,
      Some(axis)
    )

    testInvertEvent(found, desired)
  }
}
