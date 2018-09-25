package org.clulab.musica

import com.typesafe.scalalogging.LazyLogging
import org.clulab.odin._
import org.clulab.odin.impl.Taxonomy
import org.clulab.utils.FileUtils
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor


case class PitchInfo(pitch: String, octave: Option[Int], accidental: Option[String]) extends Attachment


class MusicaActions(val taxonomy: Taxonomy) extends Actions with LazyLogging {

//  // every action muct have a specific format:
//  // mentions are the mentions extracted by THIS rule THIS time through the odin cascade
//  def processPitch(mentions: Seq[Mention], state: State): Seq[Mention] = {
//
//    // todo: smarter splitring (with a regex prob)
//    def splitOctave(m: Mention): (String, Option[Int], Option[String]) = {
//
//      val (accidentals, other) = m.text.partition(char => MusicaActions.ACCIDENTALS.contains(char))
//      val (octave, pitchClass) = other.partition(char => MusicaActions.NUMBERS.contains(char))
//
//      val octaveOption = if (octave != "") Some(octave.toInt) else None
//      val accidentalOption = if (accidentals != "") Some(accidentals) else None
//
//      (pitchClass, octaveOption, accidentalOption)
//    }
//
//    for {
//      m <- mentions
//      (pitch, octave, accidentals) = splitOctave(m)
//      pitchAttachment = PitchInfo(pitch, octave, accidentals)
//    } yield m.withAttachment(pitchAttachment)
//  }








//      val args = scala.collection.mutable.Map[String, Seq[Mention]]()
//      args.put("pitchclass", Seq(m))
//      if (octave.nonEmpty) {
//        args.put("octave", Seq(m))
//      }
//      val compFoundBy = m.foundBy + "++applyMetaData" // cmpositional foundBy for debugging
//      // return as mention with args
//      new RelationMention(labels = m.labels, tokenInterval = m.tokenInterval, arguments = args.toMap,
//        paths = m.paths, sentence = m.sentence, document = m.document, keep = m.keep, foundBy = compFoundBy,
//        attachments = m.attachments)
//    }
//
//    // Split the pitch class from the octave
//    for {
//      m <- mentions
//      text = m.text // "C4"
//      (pitch, octave) = splitOctave(text)
//    } yield applyMetaData(m, pitch, octave)
//
////    mentions
//  }

}

object MusicaActions {
  val ACCIDENTALS = Set('#', 'b') // todo etc
  val NUMBERS = Set('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

  def apply(taxonomyPath: String) =
    new MusicaActions(readTaxonomy(taxonomyPath))

  private def readTaxonomy(path: String): Taxonomy = {
    val input = FileUtils.getTextFromResource(path)
    val yaml = new Yaml(new Constructor(classOf[java.util.Collection[Any]]))
    val data = yaml.load(input).asInstanceOf[java.util.Collection[Any]]
    Taxonomy(data)
  }
}
