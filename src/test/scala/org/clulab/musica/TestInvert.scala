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
}
