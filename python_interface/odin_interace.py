import re
import sys
import json
import pprint
import requests


# ------------------------------------------------------------------------
# Current top-level entry point:
# (1) perform_single_dependency_parse()


# ------------------------------------------------------------------------
# TODO
# (1) Create top-level main
# (2) batch_odin_parse()  # currently in sentence_examples


# ------------------------------------------------------------------------
# Single Odin processing request functionality
# ------------------------------------------------------------------------

def odin_request(sentence: str,
                 host='http://localhost:9000',
                 fn='/getMentions') -> requests.Response:
    """
    Send an http request to Odin host, using function fn with argument
    The Odin request fn getMentions extracts all of the mentions
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


# ------------------------------------------------------------------------
# Filter Actions from mentions
# ------------------------------------------------------------------------

def filter_actions(mentions: dict):
    """
    Extract any mentions that are Actions
    Actions are intended to correspond to PyECI/MusECI operations
    :param mentions:
    :return:
    """
    actions = list()
    for m in mentions['mentions']:
        if 'labels' in m:
            if 'Action' in m['labels']:
                actions.append(m)
    return actions


# ------------------------------------------------------------------------
# Process Action mentions
# ------------------------------------------------------------------------

def process_action_mention(action_mention: dict):
    """
    Top-level dispatch for currently handled Action types
    :param action_mention:
    :return:
    """
    op = None
    args = None
    if 'Insert' in action_mention['labels']:
        op = 'Insert'
        args = handle_insert(action_mention)
    elif 'Delete' in action_mention['labels']:
        op = 'Delete'
        args = handle_delete(action_mention)
    elif 'Reverse' in action_mention['labels']:
        op = 'Reverse'
        args = handle_reverse(action_mention)
    elif 'Transpose' in action_mention['labels']:
        op = 'Transpose'
        args = handle_transpose(action_mention)

    return {'action': op, 'arguments': args}


def handle_transpose(mention: dict):
    onset = get_onset(mention)
    note = get_note(mention)
    direction = get_direction(mention)
    step = get_step(mention)

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
                                     'Duration': note['duration']}},
            'Direction': direction,
            'Step': step}


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


def handle_reverse(mention: dict):
    onset = get_onset(mention)
    note = get_note(mention)

    if note['onset'] is None and onset is not None:
        note['onset'] = onset
    else:
        if note['onset'] is None and onset is None:
            # Reverse appears to not require an onset
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


# ------------------------------------------------------------------------
# Parsers to extract various info from musica_odin mentions
# ------------------------------------------------------------------------

def get_property_value(arguments: dict, property_name: str, value_key='words'):
    """
    Helper for extracting named-key values from property in a dictionary of properties
        { '<property_name>': [ { '<key>': [ value ] ... } ... ] ... }
    :param arguments: property (arguments) dictionary
    :param property_name: property name string
    :param value_key: key name string
    :return:
    """
    property_value = None
    if property_name in arguments:
        property_args = arguments[property_name][0]
        if 'words' in property_args:
            property_value = property_args[value_key][0]
    return property_value


def get_onset(mention: dict):
    """
    Extract onset info from a musica_odin mention
    Includes (optional): beat, measure
    :param mention: musica_odin mention
    :return:
    """
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
        # print('NO Onset')
        return None


def get_note(mention: dict):
    """
    Extract note info from a musica_odin mention
    Includes (optional): pitch, onset, duration, specifier (associated with note)
    :param mention: musica_odin mention
    :return:
    """
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
        # If no note found, return an unspecified note
        # TODO: if no note is mentioned, then perhaps this should be None?
        #       But if return None, that breaks Action handlers that expect Notes to exist
        #       Really this depends on PyECI expectations.
        return {'pitch': None,
                'onset': None,
                'duration': None,
                'specifier': None}


def parse_duration(duration_info: str) -> dict:
    """
    Map common duration text terms to MusECI duration values
    :param duration_info:
    :return:
    """
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


def parse_pitch(raw_pitch_info: str) -> dict:
    """
    Parse pitch text to pitch_class and (optional) octave
    :param raw_pitch_info: raw text string
    :return:
    """
    pitch_info = raw_pitch_info.strip('s')  # strip 's' for plural
    pitch_info = re.split('(\d+)', pitch_info)

    pitch_class = pitch_info[0].strip('.')  # strip '.' in token for "initials": 'G.' short for 'George'

    octave = None
    if len(pitch_info) > 1:
        octave = pitch_info[1]

    return {'pitch_class': pitch_class, 'octave': octave}


def test_parse_pitch():
    assert parse_pitch('C') == {'pitch_class': 'C', 'octave': None}
    assert parse_pitch('C#') == {'pitch_class': 'C#', 'octave': None}
    assert parse_pitch('Cb') == {'pitch_class': 'Cb', 'octave': None}
    assert parse_pitch('C.') == {'pitch_class': 'C', 'octave': None}
    assert parse_pitch('Cs') == {'pitch_class': 'C', 'octave': None}
    assert parse_pitch('C#4') == {'pitch_class': 'C#', 'octave': '4'}
    assert parse_pitch('C#43') == {'pitch_class': 'C#', 'octave': '43'}
    assert parse_pitch('C#43s') == {'pitch_class': 'C#', 'octave': '43'}
    assert parse_pitch('Cb43') == {'pitch_class': 'Cb', 'octave': '43'}
    assert parse_pitch('C#.43') == {'pitch_class': 'C#', 'octave': '43'}


# test_parse_pitch()


def get_specifier(mention: dict) -> dict:
    """
    Extract Specifier info from a musica_odin mention
    Includes (optional): quantifier, cardinality, set_choice
    :param mention: musica_odin mention
    :return:
    """
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

    return {'quantifier': quantifier, 'cardinality': cardinality, 'set_choice': set_choice}


def get_direction(mention: dict):
    # NOTE: assumes direction is at top-level, as is currently the case with transpose
    return get_property_value(mention['arguments'], 'direction')


def get_step(mention: dict):
    if 'step' in mention['arguments']:
        sm = mention['arguments']['step'][0]['arguments']

        cardinality = None
        if 'cardinality' in sm:
            cardinality = get_property_value(sm, 'cardinality')

        proportion = None
        if 'proportion' in sm:
            proportion = get_property_value(sm, 'proportion')

        return {'proportion': proportion, 'cardinality': cardinality}
    else:
        return None


# ------------------------------------------------------------------------
# Perform single dependency parse
# ------------------------------------------------------------------------

def odin_sentence_to_pyeci_spec(sentence: str, return_sentence=False, verbose=False):
    """
    Given a sentence, request Odin process the sentence, extract the musica_odin mentions.
    :param sentence: str representing sentence to be parsed
    :param return_sentence: flag controlling whether function returns both the action_spec AND the original sentence
                            This is a helper for auto-generating target unit test targets
    :param verbose: flag controlling whether to print intermediate values (e.g., mentions)
    :return:
    """

    if verbose:
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

    if return_sentence:
        return actions, sentence
    else:
        return actions

