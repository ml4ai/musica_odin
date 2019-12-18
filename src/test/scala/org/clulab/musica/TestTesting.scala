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

    for (mention <- mentions) {
      println(mention.text + " is a " + mention.label + " found by " + mention.foundBy)
    }

    val convertEvents = mentions.filter(_ matches "Convert")

    println(convertEvents)
    convertEvents should have length(1)
    val found = convertEvents.head

    val note = Note(Some(Duration("half")), None, Some(Specifier("all the")))
    val musicalEntity = MusicalEntity(Some(note), None, None)
    val sourceEntity = SourceEntity(Some(musicalEntity))

    //val desired = Convert(Some(SourceEntity(Some(MusicalEntity(Some(Note(Some(Duration("half")), None, Some(Specifier("all the")))), None, None)))))

    val desired = Convert(
      Some(sourceEntity)
    )

    testConvertEvent(found, desired)

  }

}
