from typing import List
import re
import os
import sys
import pprint
import datetime
from collections import OrderedDict
import requests
import pygraphviz


# ------------------------------------------------------------------------
# Usage:
# The following script contains two "top-level" functions -- see docstrings
# (1) perform_single_dependency_parse()
# (2) batch_odin_parse()


# ------------------------------------------------------------------------
# TODO
# (1) Create top-level main


# ------------------------------------------------------------------------
# Optional global path to sentence corpus file


# NOTE: you'll need to update the root to your own location of MUSICA Google Drive contents
SENTENCE_CORPUS_ROOT = '/Users/claytonm/Google Drive/MUSICA/DataSets/Sentences/'
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
                 fn='/parseSentence') -> requests.Response:
    """
    Send an http request to Odin host, using function fn with argument
    :param sentence: [str] sentence
    :param host: URL for odin host
    :param fn: odin request function
    :return: either a request.Request or str
    """
    try:
        r = requests.get(host + fn, params={'sent': sentence})
        # print('RESPONSE: {0}'.format(r.status_code))
        return r
    except requests.exceptions.ConnectionError as ce:
        print('ERROR odin_request(): Could not connect to Odin: ', str(ce))
        sys.exit()
    except Exception as e:
        print('ERROR odin_request(): Something went wrong: ', str(e))
        sys.exit()


def graph_dependency_parse(relations: List, dest_path=None):
    """
    Generate a plot of the dependency graph, using pygraphviz, and save it to dest_path.
    :param relations: List of <dependency_relation> as output from extract_odin_dependency_parse
    :param dest_path: str representing destination file path; default = 'dep_graph_test.png'
    :return:
    """
    if dest_path is None:
        dest_path = "dep_graph_test.png"
    G = pygraphviz.AGraph(directed=True, strict=True, resolution="190")

    if relations:
        for i in range(len(relations)):
            if i == 0:
                G.add_edge('ROOT', relations[i][1][1], arrowType="inv")
            # G.edge_attr['label'] = 'root'
            G.add_edge(relations[i][1][1], relations[i][2][1], color='red', label=relations[i][0], )

    G.layout(prog="dot")
    G.draw(dest_path)


def extract_odin_dependency_parse(r: requests.Response) -> List:
    """
    Extracts the dependency relations from an Odin dependency parse and organize into
    easier to graph representation as a list of <dependency_relation>s, defined as follows:
        <dependency_relation> := ( <dependency_relation_type>, <entity>:head, <entity>:tail )
        <dependency_relation_type> := str
        <entity> := ( [ <entity_label>, <POS_tag>, <entity_span> ] , <entity_text> )
        <entity_label> := str  // internal ID, usually starting with 'T' followed by number
        <POS_tag> := str  // Part of speech tag
        <entity_span> := List of List of two Integers  // Ints represent start, end of character position in sentence
        <entity_text> := str  // "<word> [<POS_tag>]", where <word> is original text of entity
                              //                   from sentence (within <entity_span>)
    :param r: [requests.Response]
    :return: List of <dependency_relation>
    """
    text = re.compile(".*")

    odin_ = r.json()
    sentence = odin_['syntax']['text']

    se = OrderedDict()
    for i in range(len(odin_['syntax']['entities'])):
        se[odin_['syntax']['entities'][i][0]] = odin_['syntax']['entities'][i]
    words = OrderedDict([(r'T{}'.format(i + 1),
                          text.search(sentence, list(se.values())[i][2][0][0],
                                      list(se.values())[i][2][0][1]).group())
                         for i in range(len(se))])

    ses = OrderedDict()
    for i in range(len(se)):
        # word_label: "<word> [<POS>]"
        word_label = '{0} [{1}]'.format(words[list(se.values())[i][0]], list(se.values())[i][1])
        ses[list(se.values())[i][0]] = (list(se.values())[i], word_label)

    res = odin_['syntax']['relations']
    resx = [(res[i][2][0][1], res[i][2][1][1]) for i in range(len(res))]
    dparse = [(res[i][1], ses[resx[i][0]], ses[resx[i][1]]) for i in range(len(res))]

    return dparse


def perform_single_dependency_parse(sentence: str):
    """
    Given a sentence, request Odin process the sentence, extract the dependency parse
    and save the graph to file (currently defaults to 'dep_graph_test.png').
    :param sentence: str representing sentence to be parsed
    :return:
    """
    r = odin_request(sentence)
    pprint.pprint(r.json())
    dparse = extract_odin_dependency_parse(r)
    graph_dependency_parse(dparse)
    print('DONE')


# perform_single_dependency_parse('Change the first quarter note to an F')


# ------------------------------------------------------------------------
# Batch Odin processing request functionality


def html_table_template(rows: str) -> str:
    """

    :return:
    """
    return """<style type="text/css">
.tg  {border-collapse:collapse;border-spacing:0;}
.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;}
.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;}
.tg .tg-3nwm{font-family:"Palatino Linotype", "Book Antiqua", Palatino, serif !important;;color:#3531ff;vertical-align;text-align: left}
.tg .tg-us36{border-color:inherit;vertical-align:top}
.tg .tg-yw4l{vertical-align:top}
</style>
<table class="tg">""" + \
           '{0}'.format(rows) + \
           "</table>"


def html_row_template(label: str, cell1: str, cell2: str) -> str:
    return """  <tr>
    <th class="tg-us36" rowspan="2">{0}</th>
    <th class="tg-3nwm">{1}</th>
  </tr>
  <tr>
    <td class="tg-yw4l">{2}</td>
  </tr>
""".format(label, cell1, cell2)


def html_img(src_path):
    return '<img src="{0}" alt="{0}" height="300">'.format(src_path)


def batch_odin_parse(corpus_file_path: str, dest_root=None, generate_html_p=True, verbose_p=False):
    """
    Given a path <corpus_file_path> to a single text file containing a set of sentences,
    one on each line, essentially execute the perform_single_dependency_parse script
    (request dep parse, save graph dep parse), and optionally create index.html summary
    webpage displaying sentences and their dependency parses.
    :param corpus_file_path: path to single text file containing sentences (one on each row)
    :param dest_root: Optional root for output results directory
    :param generate_html_p: Flag for whether to generate index.html webpage
    :param verbose_p: Flag for whether to generate verbose output describing steps during processing
    :return:
    """
    start_time = datetime.datetime.now()
    if verbose_p:
        print('EXEC batch_odin_parse():')
    if not os.path.isfile(corpus_file_path):
        print('ERROR batch_odin_parse(): Could not find file:')
        print('    ', corpus_file_path)
        sys.exit()
    else:
        print('Corpus file path:', corpus_file_path)

        if dest_root is None:
            dest_root = os.path.join('../data/', get_timestamp())

        if os.path.exists(dest_root):
            print('ERROR batch_odin_parse(): dest_root already exists:')
            print('    ', dest_root)
            sys.exit()
        else:
            if verbose_p:
                print('Creating dest_root: {0}'.format(dest_root))
            os.mkdir(dest_root)

            html_rows = list()

            with open(corpus_file_path) as fin:
                for i, line in enumerate(fin.readlines()):
                    sentence = line.strip()
                    if verbose_p:
                        print('Processing {0}: "{1}"'.format(i, sentence))
                    r = odin_request(sentence)
                    dparse = extract_odin_dependency_parse(r)
                    dparse_graph_filename = 'dep_graph_{0}.png'.format(i)
                    dest_path = os.path.join(dest_root, dparse_graph_filename)
                    graph_dependency_parse(dparse, dest_path)

                    if generate_html_p:
                        html_rows += html_row_template('{0}'.format(i),
                                                       '{0}'.format(sentence),
                                                       '{0}'.format(html_img(dparse_graph_filename)))

            duration = datetime.datetime.now() - start_time
            duration_string = 'Batch job took: {0}'.format(pretty_timedelta(duration))
            if verbose_p:
                print(duration_string)

            if generate_html_p:
                if verbose_p:
                    print('Generating index.html')
                html_path = os.path.join(dest_root, 'index.html')
                with open(html_path, 'w') as hout:
                    hout.write(duration_string + '\n')
                    hout.write(html_table_template(''.join(html_rows)))

        if verbose_p:
            print('DONE batch_odin_parse().')


batch_odin_parse(SENTENCE_CORPUS_PATH, generate_html_p=True, verbose_p=True)

