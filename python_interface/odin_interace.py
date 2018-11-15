from typing import List
import re
import os
import sys
import json
import pprint
import datetime
# from collections import OrderedDict
import requests
# import pygraphviz


# ------------------------------------------------------------------------
# Current:
# (1) perform_single_dependency_parse()


# ------------------------------------------------------------------------
# TODO
# (1) Create top-level main
# (2) batch_odin_parse()


# ------------------------------------------------------------------------
# Optional global path to sentence corpus file


# NOTE: you'll need to update the root to your own location of MUSICA Google Drive contents
SENTENCE_CORPUS_ROOT = '/Users/claytonm/Google Drive/MUSICA/DataSets/Sentences/'
# SENTENCE_CORPUS_ROOT = '/home/elision/MUSICA/musica/sandbox/matsuura/sentences/'
SENTENCE_CORPUS_PATH = os.path.join(SENTENCE_CORPUS_ROOT, 'sentences.txt')


# ------------------------------------------------------------------------
# Utility: get_timestamp


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
# Single Odin processing request functionality

def odin_request(sentence: str,
                 host='http://localhost:9000',
                 fn='/getMentions') -> requests.Response:
    """
    Send an http request to Odin host, using function fn with argument
    :param sentence: [str] sentence
    :param host: URL for odin host
    :param fn: odin request function
    :return: either a request.Request or str
    """
    try:
        r = requests.get(host + fn, params={'text': sentence})
        # print('RESPONSE: {0}'.format(r.status_code))
        return r
    except requests.exceptions.ConnectionError as ce:
        print('ERROR odin_request(): Could not connect to Odin: ', str(ce))
        sys.exit()
    except Exception as e:
        print('ERROR odin_request(): Something went wrong: ', str(e))
        sys.exit()


def filter_actions(mentions: dict):
    actions = list()
    for m in mentions['mentions']:
        if 'labels' in m:
            if 'Action' in m['labels']:
                actions.append(m)
    return actions


def process_action_mention(action_mention: dict):
    if 'Insert' in action_mention['labels']:
        return 'Insert', handle_insert(action_mention)
    if 'Delete' in action_mention['labels']:
        return 'Delete', handle_delete(action_mention)
    if 'Reverse' in action_mention['labels']:
        return 'Reverse', handle_delete(action_mention)


def handle_reverse(mention: dict):
    onset = get_onset(mention)
    note = get_note(mention)

    if note['onset'] is None and onset is not None:
        note['onset'] = onset
    else:
        if note['onset'] is None and onset is None:
            pass
        else:
            print('UNHANDLED CASE: handle_reverse() onset:', mention)
            sys.exit()

    specifier = None
    if note['specifier'] is not None:
        specifier = note['specifier']

    return {'MusicEntity': {'Specifier': specifier,
                            'Note': {'Pitch': note['pitch'],
                                     'Onset': note['onset'],
                                     'Duration': note['duration']}}}


def handle_delete(mention: dict):
    onset = get_onset(mention)
    note = get_note(mention)

    if note['onset'] is None and onset is not None:
        note['onset'] = onset
    else:
        if note['onset'] is None and onset is None:
            pass
        else:
            print('UNHANDLED CASE: handle_delete() onset:', mention)
            sys.exit()

    specifier = None
    if note['specifier'] is not None:
        specifier = note['specifier']

    return {'MusicEntity': {'Specifier': specifier,
                            'Note': {'Pitch': note['pitch'],
                                     'Onset': note['onset'],
                                     'Duration': note['duration']}}}


def handle_insert(mention: dict):
    onset = get_onset(mention)
    note = get_note(mention)

    if note['onset'] is None and onset is not None:
        note['onset'] = onset
    else:
        print('UNHANDLED CASE: handle_insert() onset:', mention)
        sys.exit()


    specifier = None
    if note['specifier'] is not None:
        specifier = note['specifier']

    return {'MusicEntity': {'Specifier': specifier,
                            'Note': {'Pitch': note['pitch'],
                                     'Onset': note['onset'],
                                     'Duration': note['duration']}}}


# TODO: generalize - really just getting a property values down one level
def get_property_value(arguments: dict, property_name: str, value_key='words'):
    """
    assume pattern
        { '<property_name>': [ { '<key>': [ value ] ... } ... ] ... }
    :param arguments:
    :param property_name:
    :param value_key:
    :return:
    """
    property_value = None
    if property_name in arguments:
        property_args = arguments[property_name][0]
        if 'words' in property_args:
            property_value = property_args[value_key][0]
    return property_value


def get_onset(mention: dict):
    if 'onset' in mention['arguments']:
        om = mention['arguments']['onset'][0]['arguments']

        # find beat
        beat = None
        # TODO: add verbose failure conditions
        if 'beat' in om:
            om_beat_args = om['beat'][0]['arguments']
            beat = get_property_value(om_beat_args, 'cardinality')

        # find measure
        measure = None
        # TODO: add verbose failure conditions
        if 'measure' in om:
            om_measure_args = om['measure'][0]['arguments']
            measure = get_property_value(om_measure_args, 'cardinality')

        return {'beat': beat, 'measure': measure}
    else:
        print('NO Onset')
        return None


def get_note(mention: dict):
    if 'note' in mention['arguments']:
        nm = mention['arguments']['note'][0]['arguments']

        pitch_info = None
        # find pitch
        if 'pitch' in nm:
            pitch_info = get_property_value(nm, 'pitch')
            pitch_info = parse_pitch(pitch_info)

        onset_info = None
        # I don't think this will ever happen
        if 'onset' in nm:
            onset_info = get_property_value(nm, 'onset')

        duration_info = None
        if 'duration' in nm:
            duration_info = get_property_value(nm, 'duration')
            duration_info = parse_duration(duration_info)

        specifier_info = None
        if 'specifier' in nm:
            specifier_info = get_specifier(nm)

        return {'pitch': pitch_info,
                'onset': onset_info,
                'duration': duration_info,
                'specifier': specifier_info}

    else:
        return {'pitch': None,
                'onset': None,
                'duration': None,
                'specifier': None}


def parse_duration(duration_info):
    duration = {'measure': None, 'beat': None}
    if duration_info == 'eighth':
        duration['measure'] = 0
        duration['beat'] = 0.5
        return duration
    if duration_info == 'quarter':
        duration['measure'] = 0
        duration['beat'] = 1
        return duration
    if duration_info == 'half':
        duration['measure'] = 0
        duration['beat'] = 2
        return duration
    if duration_info == 'whole':
        duration['measure'] = 0
        duration['beat'] = 4
        return duration


def parse_pitch(raw_pitch_info):
    pitch_info = raw_pitch_info.strip('s')  # strip 's' for plural
    pitch_info = re.split('(\d+)', pitch_info)

    pitch_class = pitch_info[0].strip('.')  # strip '.' in token for "initials": 'G.' short for 'George'

    octave = None
    if len(pitch_info) > 1:
        octave = pitch_info[1]

    return {'pitch_class': pitch_class, 'octave': octave}


def test_parse_pitch():
    # TODO turn into proper unit test!!
    print(parse_pitch('C'))
    print(parse_pitch('C#'))
    print(parse_pitch('Cb'))
    print(parse_pitch('C.'))
    print(parse_pitch('Cs'))
    print(parse_pitch('C#4'))
    print(parse_pitch('C#43'))


# test_parse_pitch()


def get_specifier(mention: dict):
    specifier_args = mention['specifier'][0]['arguments']

    quantifier = None
    if 'quantifier' in specifier_args:
        quantifier = get_property_value(specifier_args, 'quantifier')

    cardinality = None
    if 'cardinality' in specifier_args:
        cardinality = get_property_value(specifier_args, 'cardinality')

    set_choice = None
    if 'set_choice' in specifier_args:
        set_choice = get_property_value(specifier_args, 'set_choice')

    if quantifier == 'a' or quantifier == 'an':
        quantifier = 'a'
        cardinality = 1

    return quantifier, cardinality, set_choice


def perform_single_dependency_parse(sentence: str, verbose=False):
    """
    Given a sentence, request Odin process the sentence, extract the dependency parse
    and save the graph to file (currently defaults to 'dep_graph_test.png').
    :param sentence: str representing sentence to be parsed
    :return:
    """

    print(sentence)

    r = odin_request(sentence)
    if verbose:
        print(r)
        pprint.pprint(r.json())

    filtered_actions = filter_actions(json.loads(r.text))

    actions = list()
    for filtered_action in filtered_actions:
        if verbose:
            print('\n================== Filtered Action')
            pprint.pprint(filtered_action)
            print('\n================== Actions')
        action = process_action_mention(filtered_action)
        actions.append(action)
        if verbose:
            pprint.pprint(action)

    if verbose:
        print('\n==================')
        print('DONE')

    return actions


'''
pprint.pprint(perform_single_dependency_parse(sentence="Insert a C4 quarter note on beat 1 of measure 3."))
pprint.pprint(perform_single_dependency_parse(sentence="Insert a C4 quarter note on measure 1 beat 1"))
pprint.pprint(perform_single_dependency_parse(sentence="Insert a C4 quarter note on measure 1, beat 1"))
pprint.pprint(perform_single_dependency_parse(sentence="Insert a G4 half note on beat 3 of measure 2"))
pprint.pprint(perform_single_dependency_parse(sentence="Insert a C4 quarter note at beat 1 of measure 1"))
pprint.pprint(perform_single_dependency_parse(sentence="Insert a G4 half note on beat 1 of measure 1"))
pprint.pprint(perform_single_dependency_parse(sentence="Insert an F4 whole note on beat 1 of measure 3"))
'''

'''
pprint.pprint(perform_single_dependency_parse(sentence="Delete the C in measure 1"))
pprint.pprint(perform_single_dependency_parse(sentence="Delete the Cs in measure 1"))
pprint.pprint(perform_single_dependency_parse(sentence="Delete all the notes in measure 2"))
pprint.pprint(perform_single_dependency_parse(sentence="Delete the second G"))
pprint.pprint(perform_single_dependency_parse(sentence="Delete all the notes"))
pprint.pprint(perform_single_dependency_parse(sentence="Delete all the Fs"))
'''

'''
pprint.pprint(perform_single_dependency_parse(sentence="Reverse all the notes"))
pprint.pprint(perform_single_dependency_parse(sentence="Reverse all the G"))
'''
pprint.pprint(perform_single_dependency_parse(sentence="Reverse all the notes in measure 1", verbose=True))

