# ------------------------------------------------------------------------
# Corpora by action category
# ------------------------------------------------------------------------

# draft  := current "working" parse to PyECI -- needs to be vetted by Donya
# BROKEN := current issue, with description


# All drafts
CORPUS_INSERT = \
    ("Insert a C4 quarter note on beat 1 of measure 3.",
     "Insert a C4 quarter note on measure 1 beat 1",
     "Insert a C4 quarter note on measure 1, beat 1",
     "Insert a G4 half note on beat 3 of measure 2",
     "Insert a C4 quarter note at beat 1 of measure 1",
     "Insert a G4 half note on beat 1 of measure 1",
     "Insert an F4 whole note on beat 1 of measure 3"
     )


# All drafts
CORPUS_DELETE = \
    ("Delete the C in measure 1",
     "Delete the Cs in measure 1",
     "Delete all the notes in measure 2",
     "Delete the second G",
     "Delete all the notes",
     "Delete all the Fs"
     )


# All drafts
CORPUS_REVERSE = \
    ("Reverse all the notes",
     "Reverse all the G",
     "Reverse all the notes in measure 1"
     )


# In progress
CORPUS_TRANSPOSE = \
    ("Transpose the C4 quarter note on beat 1 of measure 1 up 5 half steps",  # draft
     "Transpose the Eb in measure 5 down 3 half steps",                       # draft
     "Transpose the note on beat 3.5 of measure 5 down 1 half step",          # draft
     "Transpose the C# on beat 1 up 1 half step",    # draft
     "Transpose the C in measure 1 up 1 half step",  # draft
     "Transpose the C up 1 half step",               # BROKEN: missing onset breaks Odin Transpose rule
     "Transpose the C quarter note up 1 half step",  # BROKEN: missing onset breaks Odin Transpose rule
     "Transpose all the notes up 1 half step",       # BROKEN: missing onset breaks Odin Transpose rule
     "Transpose all the notes in measure 2 down 5 half steps",  # draft
     "Transpose the quarter note up 1 half step",               # BROKEN: missing onset breaks Odin Transpose rule
     "Transpose all the quarter notes up 1 half step",          # BROKEN: missing onset breaks Odin Transpose rule
     "Transpose the first C up 2 half steps",   # BROKEN: missing onset breaks Odin Transpose rule; handle cardinality?
     "Transpose the second C up 2 half steps",  # BROKEN: missing onset breaks Odin Transpose rule; handle cardinality?
     "Transpose everything up 5 half steps",    # BROKEN: missing onset breaks Odin Transpose rule
     "Transpose all the C up 5 half steps",     # BROKEN: missing onset breaks Odin Transpose rule
     "Transpose all the C in measure 1 up 5 half steps",        # draft
     "Transpose all the Cs up 5 half steps in measure 1",       # BROKEN: missing onset breaks Odin Transpose rule
     "Transpose all the notes up 5 half steps in measure 1",    # BROKEN: Missing onset breaks Odin Transpose pattern; due to attachment of onset after step info
     "Transpose all the notes up 5 half steps between measures 1 and 3"   # BROKEN: missing onset breaks Odin Transpose rule; need to add/test between handling
     )


# TODO evaluate
CORPUS_INVERSION = \
    ("Invert all the notes",
     "Invert all the G",
     "Invert all the notes in measure 1",
     "Invert everything in measure 1",
     "Invert all the notes around G4"
     )


# TODO: first needs Odin rule dev for between & through patterns
CORPUS_BETWEEN_THROUGH = \
    ("Delete everything between C4 and E5",
     "Reverse all the notes in measures 1 through 4",
     "Transpose everything between the C in measure 1 and the E in measure 2 up 5 half steps",
     "Reverse the notes from beats 1 through 3 in measure 2"
     "Invert all the notes between measures 2 and 5"
     )


# TODO
CORPUS_GENERATION = \
    ("Generate",
     "Generate 4 measures",
     "Generate a phrase in the style of a Bach chorale",
     "Generate in the style of Bach",
     "Generate a melody"
     )


# TODO
CORPUS_MAKE = \
    ("Make the C4 in measure 1 into a D",
     "Move the first note to measure 5",
     "Put the first C on top of the G",
     "Put the C in measure 1 on top",
     "Move all the notes from measure 1 into measure 5",
     "Copy all the notes from measure 1 into measure 4",
     "Swap the first and third notes in measure 2",
     "Move everything 2 measures to the right",
     "Move all the Cs 1 beat earlier"
     )


# TODO
CORPUS_SETTING = \
    ("Work on measure 1",
     "Work on measures 1 through 5",
     "Work on measures 1 and 2",
     "Select all the quarter notes"
     )


# TODO
CORPUS_NAMING = \
    ("Mark the C4 in measure 1 through the G in measure 5 as 'Part A'",
     "Call measures 1 through 4 'Part A'",
     "Repeat 'Part A'",
     "Transpose 'Part A' up 5 half steps"
     )


# TODO
CORPUS_COREFERENCES = \
    ("Make the melody go back to the first refrain",
     "Go back to the first refrain (i.e. work on)",
     "Take the first C and put it a beat later",
     "Take the C and put it on top (i.e. we’re working on a chord)"
     )


# Everything
ALL_SENTENCES = CORPUS_INSERT + CORPUS_DELETE + CORPUS_REVERSE \
                + CORPUS_TRANSPOSE + CORPUS_INVERSION + CORPUS_BETWEEN_THROUGH \
                + CORPUS_GENERATION + CORPUS_MAKE + CORPUS_SETTING \
                + CORPUS_NAMING + CORPUS_COREFERENCES
