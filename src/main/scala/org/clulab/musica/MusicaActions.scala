package org.clulab.musica

import com.typesafe.scalalogging.LazyLogging
import org.clulab.odin._
import org.clulab.odin.impl.Taxonomy
import org.clulab.utils.FileUtils
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor


case class PitchInfo(pitch: String, octave: Option[Int], accidental: Option[String]) extends Attachment


class MusicaActions(val taxonomy: Taxonomy) extends Actions with LazyLogging {

  /** Keeps the longest mention for each group of overlapping mentions **/
  def keepLongest(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    val mns: Iterable[Mention] = for {
      // find mentions of the same label and sentence overlap
      (k, v) <- mentions.groupBy(m => (m.sentence, m.label))
      m <- v
      // for overlapping mentions starting at the same token, keep only the longest
      longest = v.filter(_.tokenInterval.overlaps(m.tokenInterval)).maxBy(m => ((m.end - m.start) + 0.1 * m.arguments.size))
    } yield longest
    mns.toVector.distinct
  }

  def addArgument(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    for {
      m <- mentions
      argsToAdd = m.arguments - "original" // remove the original
      origMention = m.arguments("original").head // assumes one only
      combinedArgs = origMention.arguments ++ argsToAdd
    } yield copyWithArgs(origMention, combinedArgs)
  }

  def copyWithArgs(orig: Mention, newArgs: Map[String, Seq[Mention]]): Mention = {
    orig match {
      case tb: TextBoundMention => ???
      case rm: RelationMention => rm.copy(arguments = newArgs)
      case em: EventMention => em.copy(arguments = newArgs)
      case _ => ???
    }
  }

  def transpose2Convert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {

    /*
    take a Transpose event and change it to a Convert event
    Transpose events should contain (MusEnt, Location, Direction, Step)
    Convert events should contain (MusEnt, StartingLoc, MusEnt, EndingLoc)
    */

//    def pitch2int(s: String): Int = {
//      // this is unnecessarily abstracted, but doing this for now to see more easily
//
//      // mini-LUT built for semitones difference in treble clef; starts at middle C, goes up to G
//      // octave numbering starts at C
//      // true for key of C -- if we are in other keys this may not be the same
//      // e.g. in key of G, if 'F' is written, it could be F#
//      // does not include sharps or flats
//      val chart = Map("C4" -> 0, "D4" -> 2,"E4" -> 4,"F4" -> 5, "G4" -> 7, "A4" -> 9, "B4" -> 11,
//        "C5" -> 12, "D5" -> 14, "E5" -> 16, "F5" -> 17, "G5" -> 19)
//
//      // return the value
//      chart(s)
//
//    }

    def dirStepEnt2Ent(m: Mention): (Option[String], Option[String], Option[String], Option[String]) = {
      // take direction and step plus starting MusEnt and calculate ending MusEnt
      // needs LUT?

      // todo: how to extract the arguments properly
      val step = m.arguments("step")
      val direction = m.arguments("direction")
      val musEnt = m.arguments("note")
      val location = m.arguments("location")

      // use LUT to find the ending note
      // will need to have OCTAVE information in addition to pitch?
      var endEntInt = 0
      if (direction == "up") {
        endEntInt = MusicaActions.PITCH2SEMITONEDIFF(musEnt) + step
      } else {
        endEntInt = MusicaActions.PITCH2SEMITONEDIFF(musEnt) - step
      }

      val endEnt = MusicaActions.SEMITONEDIFF2PITCH(endEntInt)
      // return
      (musEnt, location, endEnt, location)

    }

    //iterate through the mentions in the sequence
    for (m <- mentions) {

      // if mention is of type Transpose
      if (m.label == "Transpose") {

        // call dirStepEnt2Ent
        val (musEnt, startLocation, endEnt, endLocation) = dirStepEnt2Ent(m)
        // todo: put this into the format for a Convert event
        val convertAttachment = Convert(musEnt, startLocation, endEnt, endLocation)

        // return updated Convert Event with StartingLoc and EndingLoc the same
        // tried yield syntax as in the below example, but it didn't work properly
        m.withAttachment(convertAttachment)
      }
    }


  }

  // todo: look at this one first! this should be an easy one to start with
  def move2Convert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {

    /*
    take a Move event and change it to a Convert event
    Move events should contain (MusEnt, StartingLoc, EndingLoc)
    Convert events should contain (MusEnt, StartingLoc, MusEnt, EndingLoc)
    */

    for (m <- mentions) {

      if (m.label == "Move") {
        // todo: how to extract the arguments properly
        val musEnt = m.arguments("note") //or other musEnt
        val startingLoc = m.arguments("location")
        val endingLoc = m.arguments("location")

        // todo: put this into the format for a Convert event
        val convertAttachment = Convert(musEnt, startingLoc, musEnt, endingLoc)

        m.withAttachment(convertAttachment)
      }
    }

  }

  def changeDur2Convert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {

    /*
    take a Move event and change it to a Convert event
    Move events should contain (MusEnt, StartingLoc, EndingLoc)
    Convert events should contain (MusEnt, StartingLoc, MusEnt, EndingLoc)
    */

    for (m <- mentions) {

      if (m.label == "Change_duration") {

        // todo: how to extract the arguments properly
        val musEnt = m.arguments("note") //or other musEnt
        val location = m.arguments("location")
        val endEnt = m.arguments("note")

        // todo: put this into the format for a Convert event
        val convertAttachment = Convert(musEnt, location, endEnt, location)

        m.withAttachment(convertAttachment)
      }
    }

  }

  def repeat2Insert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {

    /*
    take a Repeat event and change it to an Insert event
    Repeat events should contain (MusEnt, InitialLocation, LocationOfRepetition, Frequency)
    Insert events should contain (MusEnt, LocationOfInsertion, Frequency)
     */

    // if musEnt is underspecified, save

    for (m <- mentions) {

      if (m.label == "Repeat") {

        // it seems like this might only be important if we have something like:
        // "repeat the notes in measure 4 twice"
        // where we have to refer to the score in order to see what values those notes have
        // then use that to complete the Insert event. submethod?

        // todo: how to extract the arguments properly
        val musEnt = m.arguments("note") //or other musEnt
        val location = m.arguments("location")
        val endEnt = m.arguments("note")

        // todo: put this into the format for a Convert event
        val convertAttachment = Insert(musEnt, location, endEnt, location)

        m.withAttachment(convertAttachment)
      }
    }

  }

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
  val PITCH2SEMITONEDIFF: Map[String, Int] = Map("C4" -> 0, "D4" -> 2,"E4" -> 4,"F4" -> 5,
    "G4" -> 7, "A4" -> 9, "B4" -> 11, "C5" -> 12, "D5" -> 14, "E5" -> 16, "F5" -> 17, "G5" -> 19)
  val SEMITONEDIFF2PITCH: Map[Int, String] = Map(0 -> "C4", 2 -> "D4",4 -> "E4", 5 -> "F4",
    7 -> "G4", 9 -> "A4", 11 -> "B4", 12 -> "C5", 14 -> "D5", 16 -> "E5", 17 -> "F5", 19 -> "G5")

  def apply(taxonomyPath: String) =
    new MusicaActions(readTaxonomy(taxonomyPath))

  private def readTaxonomy(path: String): Taxonomy = {
    val input = FileUtils.getTextFromResource(path)
    val yaml = new Yaml(new Constructor(classOf[java.util.Collection[Any]]))
    val data = yaml.load(input).asInstanceOf[java.util.Collection[Any]]
    Taxonomy(data)
  }
}
