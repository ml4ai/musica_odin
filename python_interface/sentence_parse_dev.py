from odin_interace import odin_sentence_to_pyeci_spec
import pprint


# ------------------------------------------------------------------------
# Corpora by action category
# ------------------------------------------------------------------------

CORPUS_INSERT = \
    ("Insert a C4 quarter note on beat 1 of measure 3.",
     "Insert a C4 quarter note on measure 1 beat 1",
     "Insert a C4 quarter note on measure 1, beat 1",
     "Insert a G4 half note on beat 3 of measure 2",
     "Insert a C4 quarter note at beat 1 of measure 1",
     "Insert a G4 half note on beat 1 of measure 1",
     "Insert an F4 whole note on beat 1 of measure 3"
    )

CORPUS_DELETE = \
    ("Delete the C in measure 1",
     "Delete the Cs in measure 1",
     "Delete all the notes in measure 2",
     "Delete the second G",
     "Delete all the notes",
     "Delete all the Fs"
    )

CORPUS_REVERSE = \
    ("Reverse all the notes",
     "Reverse all the G",
     "Reverse all the notes in measure 1"  # currently broken
    )


# ------------------------------------------------------------------------
# Parse Corpus
# ------------------------------------------------------------------------

def batch_odin_parse(corpus, return_sentence=False):
    for sentence in corpus:
        pprint.pprint(odin_sentence_to_pyeci_spec(sentence=sentence, return_sentence=return_sentence))


# ------------------------------------------------------------------------
# Scripts
# ------------------------------------------------------------------------

batch_odin_parse(CORPUS_REVERSE, return_sentence=True)
