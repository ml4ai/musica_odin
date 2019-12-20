//package org.clulab.musica
//
//import org.clulab.musica.MusicaTestObjects.AtomicObjects._
//import org.clulab.musica.MusicaTestObjects.ComplexEvents.Convert
//import org.clulab.musica.MusicaTestObjects.SimpleEvents._
//import org.clulab.musica.MusicaTestObjects.ConversionUtils
//import org.clulab.musica.TestUtils._
//
//class TestConvert extends ExtractionTest {
//
//  // CONVERT NO SUBTYPE
//
//  val t46 = "Convert the quarter note in measure 1 into a quarter rest"
//
//  passingTest should s"extract correctly from $t46" in {
//    val mentions = extractMentions(t46)
//    val convertEvents = mentions.filter(_ matches "Convert")
////    println(ConversionUtils.mentionToString(convertEvents.head))
//
//    convertEvents should have length(1)
//    val found = convertEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
//    val onset = Onset(Some(Measure("1")), None)
//    val rest = Rest(Some(Specifier("a")), Some(Duration("quarter")))
//    val desired = Convert(
//      note = Some(note),
//      onset = Some(onset),
//      rest = Some(rest)
//    )
//
//    testConvertEvent(found, desired)
//  }
//
//  // only dealing with the conversion event
//  // this one will be very hard to do
//  // todo: relative location needed
//  val t47 = "In the first measure, after the C quarter note, take the D eighth note out and change it to an eighth note rest."
//
//  failingTest should s"extract correctly from $t47" in {
//    val mentions = extractMentions(t47)
//    val convertEvents = mentions.filter(_ matches "Convert")
//
//    convertEvents should have length(1)
//    val found = convertEvents.head
//
//    val note = Note(Some(Duration("eighth")), Some(Pitch("D")), Some(Specifier("the")))
//    val onset = Onset(Some(Measure("the first")), None)
//    val rest = Rest(Some(Specifier("an")), Some(Duration("eighth")))
//    val desired = Convert(
//      note = Some(note),
//      onset = Some(onset),
//      rest = Some(rest)
//    )
//
//    testConvertEvent(found, desired)
//  }
//
//  val t48 = "Change the D quarter note into a minor fifth"
//
//  passingTest should s"extract correctly from $t48" in {
//    val mentions = extractMentions(t48)
//    val convertEvents = mentions.filter(_ matches "Convert")
//
//    convertEvents should have length(1)
//    val found = convertEvents.head
//
//    val note = Note(Some(Duration("quarter")), Some(Pitch("D")), Some(Specifier("the")))
//    val chord = Chord(cardinality = Some("fifth"), Some(ChordType("minor")), Some(Specifier("a")))
//    val desired = Convert(
//      note = Some(note),
//      chord = Some(chord)
//    )
//
//  }
//
//  // CONVERT SUBTYPE CHANGE DURATION
//
//  val t49 = "Shorten the quarter note on beat 1 of measure 2"
//
//  passingTest should s"extract correctly from $t49" in {
//    val mentions = extractMentions(t49)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
//    val onset = Onset(Some(Measure("2")), Some(Beat("1")))
//    val desired = ChangeDuration(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  val t50 = "Shorten the half note on the first beat of the second measure"
//
//  failingTest should s"extract correctly from $t50" in {
//    val mentions = extractMentions(t50)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("half")), None, Some(Specifier("the")))
//    val onset = Onset(Some(Measure("second")), Some(Beat("first")))
//    val desired = ChangeDuration(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  val t51 = "The quarter note on beat 1 of measure 2 is shortened"
//
//  passingTest should s"extract correctly from $t51" in {
//    val mentions = extractMentions(t51)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
//    val onset = Onset(Some(Measure("2")), Some(Beat("1")))
//    val desired = ChangeDuration(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  val t52 = "The quarter note is shortened in beat 1 of measure 2"
//
//  passingTest should s"extract correctly from $t52" in {
//    val mentions = extractMentions(t52)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
//    val onset = Onset(Some(Measure("2")), Some(Beat("1")))
//    val desired = ChangeDuration(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  //  Cannot yet handle 2 notes OR 'cardinals' in note
//  val t53 = "The first quarter note should be shortened to an eighth note"
//
//  passingTest should s"extract correctly from $t53" in {
//    val mentions = extractMentions(t53)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("The first")))
//    val onset = Onset(None, None)
//    val final_note = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
//    val desired = ChangeDuration(
//      note = Some(note),
//      final_note = Some(final_note)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  //  Cannot yet handle 2 notes OR 'cardinals' in note
//  val t54 = "Shorten the quarter note in measure 1 to an eighth note"
//
//  passingTest should s"extract correctly from $t54" in {
//    val mentions = extractMentions(t54)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
//    val onset = Onset(Some(Measure("1")), None)
//    val final_note = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
//    val desired = ChangeDuration(
//      note = Some(note),
//      onset = Some(onset),
//      final_note = Some(final_note)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  //  Shortened version of t6 that's known to pass
//  val t55 = "Shorten the quarter note in measure 1"
//
//  passingTest should s"extract correctly from $t55" in {
//    val mentions = extractMentions(t55)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
//    val onset = Onset(Some(Measure("1")), None)
//    val desired = ChangeDuration(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//
//  val t56 = "Shorten all the half notes"
//
//  passingTest should s"extract correctly from $t56" in {
//    val mentions = extractMentions(t56)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("half")), None, Some(Specifier("all the")))
//    val onset = Onset(None, None)
//    val desired = ChangeDuration(
//      note = Some(note)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  // todo: needs to include specifier everything
//  val t57 = "Everything should be shortened from half notes to quarter notes"
//
//  failingTest should s"extract correctly from $t57" in {
//    val mentions = extractMentions(t57)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    // val spec = Specifier("everything")
//    val note = Note(Some(Duration("half")), None, Some(Specifier("all the")))
//    val onset = Onset(None, None)
//    val desired = ChangeDuration(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  // only testing duration change here; todo: needs 3 notes
//  val t58 = "The first note switched from a half note to a quarter note and moved up two notes."
//
//  failingTest should s"extract correctly from $t58" in {
//    val mentions = extractMentions(t58)
//    val changeDurationEvents = mentions.filter(_ matches "ChangeDuration")
//
//    changeDurationEvents should have length(1)
//    val found = changeDurationEvents.head
//
//    // val spec = Specifier("everything")
//    val note = Note(None, None, Some(Specifier("The first")))
//    val final_note = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
//    val desired = ChangeDuration(
//      note = Some(note),
//      final_note = Some(final_note)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//  val t59 = "Change the half note in measure 1"
//
//  failingTest should s"extract correctly from $t59" in {
//    val mentions = extractMentions(t59)
//    val changeDurationEvents = mentions.filter(_ matches "Convert")
//
//    changeDurationEvents should have length (1)
//    val found = changeDurationEvents.head
//
//    val note = Note(Some(Duration("half")), None, Some(Specifier("the")))
//    val onset = Onset(Some(Measure("1")), None)
//    val desired = ChangeDuration(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testChangeDurationEvent(found, desired)
//  }
//
//
//}
