import os.path
import json


# ------------------------------------------------------------------------
# Load JSON config
# ------------------------------------------------------------------------

CUR_FILE = os.path.dirname(os.path.realpath(__file__))
JSON_PATH = os.path.normpath(os.path.join(CUR_FILE, 'config.json'))

if not os.path.isfile(JSON_PATH):
    raise RuntimeError("config.json file is required. See paths.py")

config = json.load(open(JSON_PATH))


def get_config_param(param):
    if param in config:
        param_value = config[param]
        # print(f"Setting '{param}' to '{param_value}'")
        return param_value
    else:
        print(f"WARNING: parameter '{param}' not found in config. See paths.py")
        return None


# ------------------------------------------------------------------------
# Set path globals from config
# ------------------------------------------------------------------------

MTURK_SENTENCE_SOURCE_ROOT = get_config_param('mturk_sentence_source_root')
