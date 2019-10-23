package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.Convert
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.SomewhatComplexEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestTesting extends ExtractionTest {

  val t1 = "Shorten all the half notes"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Some()

    val desired = Convert(Some(SourceEntity(Some(MusicalEntity(Some(Note(Some(Duration("half")), None, Some(Specifier("all the")))), None, None)))))

    testConvertEvent(found, desired)

  }

}
