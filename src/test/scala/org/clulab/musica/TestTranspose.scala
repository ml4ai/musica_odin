package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.Transpose
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.TestUtils._

class TestTranspose extends ExtractionTest {

  val t1 = "Transpose the C4 quarter note on beat 1 of measure 1 up 5 half steps"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("C4")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), Some(Beat("1")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("5"), proportion = Some("half"))
    val desired = Transpose(
      note = Some(note),
      direction = Some(direction),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

}
