package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Reverse}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestReverse extends ExtractionTest {

  val t1 = "Reverse all the notes in measure 1"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val reverseEvents = mentions.filter(_ matches "Reverse")

    reverseEvents should have length(1)
    val found = reverseEvents.head

    val musicalEntity = Note(None, None, Some(Specifier("all the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val desired = Reverse(
      Some(musicalEntity),
      Some(location)
    )

    testReverseEvent(found, desired)
  }
}
