import paths
import os
import math
import pandas


# ------------------------------------------------------------------------
# Paths
# ------------------------------------------------------------------------

MTURK_ALL_TRIALS_RESULTS = \
    os.path.join(paths.MTURK_SENTENCE_SOURCE_ROOT,
                 "all_trials_results.xlsx")
MTURK_ALL_TRIALS_TARGET_DESCRIPTIONS = \
    os.path.join(paths.MTURK_SENTENCE_SOURCE_ROOT,
                 "all_trials_target_descriptions.xlsx")


# ------------------------------------------------------------------------
#
# ------------------------------------------------------------------------

def info(df_map):
    print(f'====== pandas version: {pandas.__version__}')
    print(len(df_map))
    print(df_map.keys())
    print('Sheet1 type:', type(df_map['Sheet1']))
    print('columns type:', type(df_map['Sheet1'].columns))
    print('columns:', df_map['Sheet1'].columns)
    print('Sheet dimensions:')
    print([(key, sheet.shape) for key, sheet in df_map.items()])


def process_raw_sentence(sentence):
    for char in sentence:
        print(ord(char), char)


# process_raw_sentence("""In the third measure change it to a quarter note A followed by a quarter note F followed by a half note D"
# Instead of playing d, f, a, you should play a, f, d.  D is still 2 beats, and f a and are 1 beat.""")


def get_correct_sentences():
    sheets_df_map = pandas.read_excel(MTURK_ALL_TRIALS_RESULTS, sheet_name=None)
    num = 1
    for i, sheet in enumerate(sheets_df_map.keys()):
        for index, row in sheets_df_map[sheet].iterrows():
            sheet_num = i+1
            if not math.isnan(row['ID']) and row['Correct'] == 1:
                participant_id = int(row['ID'])
                sentence = row.iloc[1]
                print(f"{num}, {sheet_num}, {participant_id}, \"{sentence}\"")
                num += 1


get_correct_sentences()


def read_all_trials_results():
    print(MTURK_ALL_TRIALS_RESULTS)
    print(MTURK_ALL_TRIALS_TARGET_DESCRIPTIONS)

    sheets_df_map = pandas.read_excel(MTURK_ALL_TRIALS_RESULTS, sheet_name=None)
    sheets_descriptions_df_map = \
        pandas.read_excel(MTURK_ALL_TRIALS_TARGET_DESCRIPTIONS, sheet_name=None)

    info(sheets_df_map)
    info(sheets_descriptions_df_map)

    print('==========')

    s1 = sheets_df_map['Sheet1']
    print('s1.shape:    ', s1.shape)
    print('s1.columns   ', s1.columns)
    print('type(s1[1]): ', type(s1[1]))
    print('s1[1].name:  ', s1[1].name)
    # print(s1[1])

    print('---')
    s2 = sheets_descriptions_df_map['Sheet1']
    print('s2.shape:    ', s2.shape)
    print('s2.columns   ', s2.columns)
    print('type(s2.iloc[2]): ', type(s2.iloc[:1]))
    # print('s2.iloc[2].name:  ', s2.iloc[:1].name)
    # print('s2.iloc[2].shape: ', s2.iloc[:1].shape)
    # print('type(s2["order measure"]): ', type(s2['order measure']))
    print('s2["pitch"].name:  ', s2['pitch'].name)
    print('s2["pitch"].shape: ', s2['pitch'].shape)

    print('---')
    # add column from s2 to s1
    s1['pitch'] = s2['pitch']
    print('s1.shape:    ', s1.shape)
    print('s1.columns   ', s1.columns)
    print('s1[1].name:  ', s1[1].name)

    print(s1[['ID', 'Correct', 'pitch']])


# read_all_trials_results()
