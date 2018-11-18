from odin_interace import odin_sentence_to_pyeci_spec
from sentence_corpora import *
import datetime
import pprint
import json
import os


# ------------------------------------------------------------------------
# NOTE: see Scripts section at bottom for example usage


# ------------------------------------------------------------------------
# Globals
# ------------------------------------------------------------------------

# root directory home for odin_parse_state files
SNAPSHOT_DST_ROOT = 'odin_parse_state_snapshots'
CSTATE_FILENAME_BASE = 'odin_parse_state'
FILEEXT = '.json'


# ------------------------------------------------------------------------
# Utility: get_timestamp
# ------------------------------------------------------------------------

def pretty_time(the_time: datetime.datetime) -> str:
    """
    Returns a string in the following format:
        <year><month><day><24hour><minute><second>-<milliseconds>
    :param the_time: a datetime.datetime object
    :return: str
    """
    return '{y}{mon:02d}{day:02d}{hr:02d}{min:02d}{sec:02}_{msec:07d}' \
        .format(y=the_time.year, mon=the_time.month, day=the_time.day,
                hr=the_time.hour, min=the_time.minute, sec=the_time.second,
                msec=the_time.microsecond)


def pretty_timedelta(delta: datetime.timedelta):
    return 'days: {days}, seconds: {sec}, microseconds: {msec}' \
        .format(days=delta.days, sec=delta.seconds, msec=delta.microseconds)


def get_timestamp() -> str:
    """
    Get pretty_time of current time.
    :return: str
    """
    return pretty_time(datetime.datetime.now())


# ------------------------------------------------------------------------
# Parse Corpus
# ------------------------------------------------------------------------

def batch_odin_parse(corpus,
                     return_sentence=False, return_mentions=False,
                     pprint_without_return=False,
                     verbose=False, verbose_odin=False):
    """
    Top-level helper for use when developing or testing sequence of sentences.
    Also used for generating snapshot
    :param corpus: sequence of sentences
    :param return_sentence: when True: include sentence in return value
    :param return_mentions: when True: include Odin mentions in return value
    :param pprint_without_return: when True: just pprint results and don't have fn return at end
                                  Use this when using only for sentence testing/dev
    :param verbose: when True: print current sentence being processed
    :param verbose_odin: when True: run odin_sentence_to_pyeci_spec with verbose True
    :return:
    """
    results = list()
    for i, sentence in enumerate(corpus):
        if verbose:
            print('{0}: {1}'.format(i, sentence))
        result = odin_sentence_to_pyeci_spec(sentence=sentence,
                                             return_sentence=return_sentence,
                                             return_mentions=return_mentions,
                                             verbose=verbose_odin)
        results.append(result)
        if pprint_without_return:
            pprint.pprint(result)

    if not pprint_without_return:
        return results


# ------------------------------------------------------------------------
# Corpus Regression Testing of
#   (1) Odin parse behavior
#   (2) Odin mention to PyECI_spec mapping
# ------------------------------------------------------------------------

def create_corpus_odin_parse_state_snapshot(corpus, filename=None, root=SNAPSHOT_DST_ROOT):
    """
    Generate a snapshot text file for all sentences included in corpus
    Each entry is represented as a dict (flat-file format for humnan consumption)
        { '0_sentence': <sentence>,
          '1_pyeci_spec': <pyeci_spec>
          '2_mensions': <odin_mensions> }
    <pyeci_spec> is a recursive dict representation of the information needed to produce PyECI
    <odin_mensions> is a dict representation of the original json representation of *all* Odin mention information
    :param corpus: sequence of sentences
    :param filename: optional filename; default is CSTATE_FILENAME_BASE_<timestamp>.txt
    :return: filename
    """
    if not os.path.exists(root):
        print('Snapshot DST_ROOT does not exist, creating:')
        os.makedirs(root)
        print('    ... created:', root)
    if filename is None:
        filename = CSTATE_FILENAME_BASE + '_' + get_timestamp() + FILEEXT
    snapshot_dst = os.path.join(root, filename)
    print('Saving corpus parse state:', snapshot_dst)
    with open(snapshot_dst, 'w') as fout:
        parse_results = batch_odin_parse(corpus, return_sentence=True, return_mentions=True, verbose=True)
        # snapshot_format: dict of <sentence>, <pyeci_spec>, <mentions>
        snapshot_list = dict()
        for i, (pyeci_spec, sentence, mentions) in enumerate(parse_results):
            # shapshot_format keys start with numbers so they are forced to pprint in given order
            sdict = {'0_sentence': sentence,
                     '1_pyeci_spec': pyeci_spec,
                     '2_mentions': mentions}
            snapshot_list[i] = sdict
        json.dump(snapshot_list, fout, indent=4, sort_keys=True)
    return filename


# TODO: save diff report
def regression_test(snapshot_src_filename, root=SNAPSHOT_DST_ROOT, summary_p=False, save_p=False):
    """
    Cheap-n-cheerful regression testing.
    (1) Takes as input a pointer to an odin_parse_state .JSON file and reads it
    (2) Iterate through each sentence in file snapshot and
        (a) gathers *current* Odin parse with PyECI_spec and mentions (if any)
        (a) for each found action, reports whether PyECI_spec *snapshot* matches *current*
        (b) reports whether Odin mentions *snapshot* matches *current*
    All matching performed by dictionary == operator.
    TODO Implement saving diff file
    Diff file will report all matches and when there are diffs, what the two versions are.
    :param snapshot_src_filename: snapshot file name
    :param root: Root directory of snapshot file
    :param summary_p: When True: does NOT display diff detail, only whether match
    :param save_p: TODO
    :return:
    """
    src = os.path.join(root, snapshot_src_filename)
    with open(src, 'r') as json_file:
        data = json.load(json_file)

    timestamp = get_timestamp()  # TODO use to indicate current Odin used at runtime

    for key, value in data.items():
        sentence, pyeci_spec_snapshot, mentions_snapshot \
            = value['0_sentence'], value['1_pyeci_spec'], value['2_mentions']

        print('\n=====================================')
        print('({0})'.format(key), '\"{0}\"'.format(sentence))

        (pyeci_spec_current, s, mentions_current)\
            = odin_sentence_to_pyeci_spec(sentence, return_sentence=True, return_mentions=True)

        print('-------------------------------------')
        for i, (pyeci_spec_s, pyeci_spec_c) \
                in enumerate(zip(pyeci_spec_snapshot, pyeci_spec_current)):
            if pyeci_spec_s == pyeci_spec_c:
                print('#{0} pyeci_spec MATCH'.format(i))
            else:
                print('#{0} pyeci_spec DOES NOT MATCH'.format(i))
                if not summary_p:
                    print('From ORIGINAL pyeci_spec:', snapshot_src_filename)
                    pprint.pprint(pyeci_spec_s)
                    print('From CURRENT pyeci_spec:')
                    pprint.pprint(pyeci_spec_c)

        print('-------------------------------------')
        if mentions_snapshot == mentions_current:
            print('mentions MATCH')
        else:
            print('mentions DO NOT MATCH')
            if not summary_p:
                print('From ORIGINAL pyeci_spec:', snapshot_src_filename)
                pprint.pprint(mentions_snapshot)
                print('From CURRENT pyeci_spec:')
                pprint.pprint(mentions_current)


# ------------------------------------------------------------------------
# Scripts
# ------------------------------------------------------------------------

# ------------------------------------------------------------------------
# Examples of running batch_odin_parse in various configurations

# runs on a whole corpus
# batch_odin_parse(CORPUS_REVERSE, return_sentence=True)

# just run first sentence of corpus with verbose output
# batch_odin_parse([CORPUS_INVERSION[0]], return_sentence=True, verbose_odin=True)

# send in a specific single sentence
# batch_odin_parse(["Reverse all the notes in measure 1"], return_sentence=True, verbose_odin=True)


batch_odin_parse(["Transpose the C4 quarter note on beat 1 of measure 1 up 5 half steps"],
                 return_sentence=True, verbose_odin=True)

# ------------------------------------------------------------------------
# Examples of running create_corpus_odin_parse_state_snapshot

# Create corpus Odin parse state snapshot for just CORPUS_REVERSE
# create_corpus_odin_parse_state_snapshot(CORPUS_REVERSE)

# Create corpus Odin parse state snapshot for ALL_SENTENCES
# create_corpus_odin_parse_state_snapshot(ALL_SENTENCES)


# ------------------------------------------------------------------------
# Examples of running ...

# regression_test('odin_parse_state_20181117160413_0401164.json')

# regression_test('odin_parse_state_20181117164323_0569318.json')
