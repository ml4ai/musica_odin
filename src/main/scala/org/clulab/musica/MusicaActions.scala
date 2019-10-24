package org.clulab.musica

import com.typesafe.scalalogging.LazyLogging
import org.clulab.odin._
import org.clulab.odin.impl.Taxonomy
import org.clulab.utils.FileUtils
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.clulab.musica.MusicaEngine._

import scala.collection.mutable.ArrayBuffer


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

    //  todo: try writing so that if you get current pitch, direction, and number of steps, it will give you what you need
    //  you can code in the function as below; any time you need something else, do it like that
    //    def pitch2Note(pitch: String, dir: String, amount: Double): String = ???

    /*
    take a Transpose event and change it to a Convert event
    Transpose events should contain (MusEnt, Location, Direction, Step)
    Convert events should contain (MusEnt, StartingLoc, MusEnt, EndingLoc)
    */

    // get only the info on pitch from the mention
    def extractPitch(m: Seq[Mention]): String = ???

    // todo: the note has to be converted back to Seq[Mention] in order to work, right?
    def pitch2Note(pitch: String, dir: String, amount: Double): Seq[Mention] = ???

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

//    def dirStepEnt2Ent(m: Mention): (Option[String], Option[String], Option[String], Option[String]) = {
//      // take direction and step plus starting MusEnt and calculate ending MusEnt
//      // needs LUT?
//
//      // todo: how to extract the arguments properly
//      val step = m.arguments("step")
//      val direction = m.arguments("direction")
//      val musEnt = m.arguments("note")
//      val location = m.arguments("location")
//
//      // use LUT to find the ending note
//      // will need to have OCTAVE information in addition to pitch?
//      var endEntInt = 0
//      if (direction == "up") {
//        endEntInt = MusicaActions.PITCH2SEMITONEDIFF(musEnt) + step
//      } else {
//        endEntInt = MusicaActions.PITCH2SEMITONEDIFF(musEnt) - step
//      }
//
//      val endEnt = MusicaActions.SEMITONEDIFF2PITCH(endEntInt)
//      // return
//      (musEnt, location, endEnt, location)
//
//    }

    val out = new ArrayBuffer[Mention]

    //iterate through the mentions in the sequence
    for (m <- mentions) {

      // if mention is of type Transpose
      if (m.label == "Transpose") {

        val musEnt = m.arguments(MUS_ENT)
        val location = m.arguments.getOrElse(LOC, Seq())
        val direction = m.arguments.getOrElse("direction", Seq())
        val step = m.arguments.getOrElse("step", Seq())

        val pitch = extractPitch(musEnt)

        // todo: mention.head.text might not be the right (or best) way to do this...
        val destEnt = pitch2Note(pitch, direction.head.text, step.head.text.toDouble)

        // map to a new set of args
        val newArgs = Map(SRC_ENT -> musEnt, SRC_LOC -> location, DEST_ENT -> destEnt, DEST_LOC -> location)
        val compositionalFoundBy = m.foundBy + "++transpose2Convert"

        // change mention to a CONVERT mention
        val convertMention = m match {
          case em: EventMention => {
            val newLabels = taxonomy.hypernymsFor("Convert")
            val newTrigger = em.trigger.copy(labels = newLabels)
            em.copy(
              labels = newLabels,
              trigger = newTrigger,
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          }
          case rm: RelationMention =>
            rm.copy(
              labels = taxonomy.hypernymsFor("Convert"),
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          case _ => ???
        }

        out.append(convertMention)
      }
    }

    out
  }

  // todo: look at this one first! this should be an easy one to start with
  def move2Convert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {

    /*
    take a Move event and change it to a Convert event
    Move events should contain (MusEnt, StartingLoc, EndingLoc)
    Convert events should contain (MusEnt, StartingLoc, MusEnt, EndingLoc)
    */

    // For reference -- other syntax
//    for {
//      m <- mentions
//      if m.label == "Move"
//      musEnt = m.arguments("note") //or other musEnt
//      startingLoc = m.arguments("locationFrom")
//      endingLoc = m.arguments("locationTo")
//
//      newArgs = Map(SRC_ENT -> musEnt, SRC_LOC -> startingLoc, DEST_ENT -> musEnt, DEST_LOC -> endingLoc)
//      compositionalFoundBy = m.foundBy + "++move2Convert"
//      convertMention = m.asInstanceOf[EventMention].copy(
//        labels = taxonomy.hypernymsFor("Convert"),
//        arguments = newArgs,
//        foundBy = compositionalFoundBy)
//    } yield convertMention


    val out = new ArrayBuffer[Mention]

    for (m <- mentions) {

      if (m.label == "Move") {

        // get argument values
        val musEnt = m.arguments(MUS_ENT)
        val startingLoc = m.arguments.getOrElse(SRC_LOC, Seq())
        val endingLoc = m.arguments.getOrElse(DEST_LOC, Seq())

        // map to a new set of args
        val newArgs = Map(SRC_ENT -> musEnt, SRC_LOC -> startingLoc, DEST_ENT -> musEnt, DEST_LOC -> endingLoc)
        val compositionalFoundBy = m.foundBy + "++move2Convert"

        // change mention to a CONVERT mention
        val convertMention = m match {
          case em: EventMention => {
            val newLabels = taxonomy.hypernymsFor("Convert")
            val newTrigger = em.trigger.copy(labels = newLabels)
            em.copy(
              labels = newLabels,
              trigger = newTrigger,
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          }
          case rm: RelationMention =>
            rm.copy(
              labels = taxonomy.hypernymsFor("Convert"),
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          case _ => ???
        }

        out.append(convertMention)
      }
    }

    out
  }

  def changeDur2Convert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {

    /*
    take a Move event and change it to a Convert event
    Change Duration events should contain (SourceEnt, Location, DestEnt)
    Convert events should contain (SourceEnt, SourceLoc, DestEnt, DestLoc)
    */

    val out = new ArrayBuffer[Mention]

    for (m <- mentions) {

      if (m.label == "Change_duration") {

        // get arg values
        val srcEnt = m.arguments(SRC_ENT)
        val location = m.arguments.getOrElse(LOC, Seq())
        val destEnt = m.arguments.getOrElse(DEST_ENT, Seq())

        // map to a new set of args
        val newArgs = Map(SRC_ENT -> srcEnt, SRC_LOC -> location, DEST_ENT -> destEnt, DEST_LOC -> location)
        val compositionalFoundBy = m.foundBy + "++changeDur2Convert"

        // change mention to a CONVERT mention
        val convertMention = m match {
          case em: EventMention => {
            val newLabels = taxonomy.hypernymsFor("Convert")
            val newTrigger = em.trigger.copy(labels = newLabels)
            em.copy(
              labels = newLabels,
              trigger = newTrigger,
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          }
          case rm: RelationMention =>
            rm.copy(
              labels = taxonomy.hypernymsFor("Convert"),
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          case _ => ???
        }

        out.append(convertMention)
      }
    }

    out
  }

  def replace2Convert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {

    /*
    take a Replace event and change it to a Convert event
    Replace events should contain (SourceEnt, Location, DestEnt)
    Convert events should contain (SourceEnt, SourceLoc, DestEnt, DestLoc)
    */

    val out = new ArrayBuffer[Mention]

    for (m <- mentions) {

      if (m.label == "Replace") {

        // get arg values
        val srcEnt = m.arguments(SRC_ENT)
        val location = m.arguments.getOrElse(LOC, Seq())
        val destEnt = m.arguments.getOrElse(DEST_ENT, Seq())

        // map to a new set of args
        val newArgs = Map(SRC_ENT -> srcEnt, SRC_LOC -> location, DEST_ENT -> destEnt, DEST_LOC -> location)
        val compositionalFoundBy = m.foundBy + "++replace2Convert"

        // change mention to a CONVERT mention
        val convertMention = m match {
          case em: EventMention => {
            val newLabels = taxonomy.hypernymsFor("Convert")
            val newTrigger = em.trigger.copy(labels = newLabels)
            em.copy(
              labels = newLabels,
              trigger = newTrigger,
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          }
          case rm: RelationMention =>
            rm.copy(
              labels = taxonomy.hypernymsFor("Convert"),
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          case _ => ???
        }

        out.append(convertMention)
      }
    }

    out
  }

  def change2Insert(mentions: Seq[Mention], itemtype: String, state: State = new State()): Seq[Mention] = {

    /*
    take a Copy or Repeat event and change it to an Insert event
    Copy/Repeat events should contain (MusEnt, SourceLoc, DestLoc, Frequency)
    Insert events should contain (MusEnt, Location, Frequency)
     */

    val out = new ArrayBuffer[Mention]

    for (m <- mentions) {

      if (m.label == itemtype) {

        // get arg values
        val musEnt = m.arguments(MUS_ENT)
        val location = m.arguments.getOrElse(DEST_LOC, Seq())
        val freq = m.arguments.getOrElse(FREQ, Seq())

        // map to a new set of args
        val newArgs = Map(MUS_ENT -> musEnt, LOC -> location, FREQ -> freq)
        val compositionalFoundBy = m.foundBy + s"++${itemtype.toLowerCase()}2Insert"

        // change mention to an INSERT mention
        val insertMention = m match {
          case em: EventMention => {
            val newLabels = taxonomy.hypernymsFor("Insert")
            val newTrigger = em.trigger.copy(labels = newLabels)
            em.copy(
              labels = newLabels,
              trigger = newTrigger,
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          }
          case rm: RelationMention =>
            rm.copy(
              labels = taxonomy.hypernymsFor("Insert"),
              arguments = newArgs,
              foundBy = compositionalFoundBy)
          case _ => ???
        }

        out.append(insertMention)
      }
    }

    out
  }

  def copy2Insert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    val out = change2Insert(mentions, "Copy", state)

    out
  }

  def repeat2Insert(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    val out = change2Insert(mentions, "Repeat", state)

    out
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
