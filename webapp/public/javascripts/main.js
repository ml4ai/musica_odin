var bratLocation = 'assets/brat';

// Color names used
var baseNounPhraseColor = '#CCD1D1';
var increaseNounPhraseColor = '#BBDC90';
var decreaseNounPhraseColor = '#FC5C38';
var quantifierColor = '#AED6F1';
var quantifiedNounPhraseColor = '#85C1E9';
// fixme: whatever events we have in this project...
var insertEventColor = '#fdff36';
var correlationEventColor = '#F7DC6F';
var stepColor = "#a6dfeb";
var onsetColor = "#6baac4";
var beatColor = "#16c6f4";
var directionColor = "#b7bd1c";
var transposeColor = "#fd9040";



head.js(
    // External libraries
    bratLocation + '/client/lib/jquery.min.js',
    bratLocation + '/client/lib/jquery.svg.min.js',
    bratLocation + '/client/lib/jquery.svgdom.min.js',

    // brat helper modules
    bratLocation + '/client/src/configuration.js',
    bratLocation + '/client/src/util.js',
    bratLocation + '/client/src/annotation_log.js',
    bratLocation + '/client/lib/webfont.js',

    // brat modules
    bratLocation + '/client/src/dispatcher.js',
    bratLocation + '/client/src/url_monitor.js',
    bratLocation + '/client/src/visualizer.js'
);

var webFontURLs = [
    bratLocation + '/static/fonts/Astloch-Bold.ttf',
    bratLocation + '/static/fonts/PT_Sans-Caption-Web-Regular.ttf',
    bratLocation + '/static/fonts/Liberation_Sans-Regular.ttf'
];

var collData = {
    entity_types: [ {
        "type"   : "Quantifier",
        "labels" : ["Quantifier", "Quant"],
        // Blue is a nice colour for a person?
        "bgColor": quantifierColor,
        // Use a slightly darker version of the bgColor for the border
        "borderColor": "darken"
    },
    {
            "type"   : "NounPhrase",
            "labels" : ["NounPhrase", "NP"],
            // Blue is a nice colour for a person?
            //"bgColor": "thistle",
            "bgColor": baseNounPhraseColor,
            // Use a slightly darker version of the bgColor for the border
            "borderColor": "darken"
        },
        {
            "type"   : "NounPhrase-Inc",
            "labels" : ["NounPhrase", "NP"],
            // Blue is a nice colour for a person?
            //"bgColor": "thistle",
            "bgColor": increaseNounPhraseColor,
            // Use a slightly darker version of the bgColor for the border
            "borderColor": "darken"
        },
        {
            "type"   : "NounPhrase-Dec",
            "labels" : ["NounPhrase", "NP"],
            // Blue is a nice colour for a person?
            //"bgColor": "thistle",
            "bgColor": decreaseNounPhraseColor,
            // Use a slightly darker version of the bgColor for the border
            "borderColor": "darken"
        },
        {
            "type"   : "NounPhrase-Quant",
            "labels" : ["NounPhrase", "NP"],
            // Blue is a nice colour for a person?
            //"bgColor": "thistle",
            "bgColor": quantifiedNounPhraseColor,
            // Use a slightly darker version of the bgColor for the border
            "borderColor": "darken"
        },
     {
       "type": "Measure",
       "labels":  ["Measure", "MEAS"],
       "bgColor": "#efab77",
       "borderColor": "darken"
     },
     {
        "type": "Specifer",
        "labels":  ["spec"],
        "bgColor": "#efab77",
        "borderColor": "darken"
      },
     {
       "type": "Duration",
       "labels":  ["Duration"],
       "bgColor": "green",
       "borderColor": "darken"
     },
     {
       "type": "Note",
       "labels":  ["Note"],
       "bgColor": "#ceb1db",
       "borderColor": "darken"
     },
       {
            "type": "Step",
            "labels":  ["Step"],
            "bgColor": stepColor,
            "borderColor": "darken"
          },
        {
             "type": "Onset",
             "labels":  ["Onset"],
             "bgColor": onsetColor,
             "borderColor": "darken"
           },
 {
             "type": "Direction",
             "labels":  ["Dir"],
             "bgColor": directionColor,
             "borderColor": "darken"
           },
            {
            "type": "Beat",
            "labels":  ["Beat"],
            "bgColor": beatColor,
            "borderColor": "darken"
          },
           {
              "type": "Cardinality",
              "labels":  ["CD"],
              "bgColor": "#ffffff",
              "borderColor": "darken"
            },
            {
              "type": "Specifier",
              "labels":  ["Spec"],
              "bgColor": "#ffffff",
              "borderColor": "darken"
            },
    ],
//    relation_types: [{
//                         type     : 'Note',
//                         labels   : ['Note', 'NOTE'],
//                         // dashArray allows you to adjust the style of the relation arc
//                         //dashArray: '3,3',
//                         color    : '#ceb1db',
//                         /* A relation takes two arguments, both are named and can be constrained
//                             as to which types they may apply to */
//                         args     : [
//                             //
//                             {role: 'Specifier', targets: ['Spec'] },
//                             {role: 'Entity',  targets: ['Person'] }
//                         ]
//                     }],

    event_types: [
      {
        "type": "Add",
        "labels": ["ADD"],
        "bgColor": "lightgreen",
        "borderColor": "darken",
        "arcs": [
            {"type": "node", "labels": ["note"], "borderColor": "darken", "bgColor":"violet"},
            {"type": "onset", "labels": ["onset"], "borderColor": "darken", "bgColor":"violet"}
        ]
      },

      {
        "type": "Delete",
        "labels": ["DEL"],
        "bgColor": "red",
        "borderColor": "darken",
        "arcs": [
            {"type": "note", "labels": ["note"], "borderColor": "darken", "bgColor":"violet"},
            {"type": "onset", "labels": ["onset"], "borderColor": "darken", "bgColor":"violet"}
        ]
      },

      {
        "type": "Insert",
        "labels": ["INS"],
        "bgColor": insertEventColor,
        "borderColor": "darken",
        "arcs": [
          {"type": "note", "labels": ["note"], "borderColor": "darken", "bgColor":"pink"},
          {"type": "onset", "labels": ["onset"], "borderColor": "darken", "bgColor":"pink"}
         ]
      },

      {
        "type": "Transpose",
        "labels": ["TRANSPOSE"],
        "bgColor": transposeColor,
        "borderColor": "darken",
        "arcs": [
          {"type": "note", "labels": ["note"], "borderColor": "darken", "bgColor":"pink"},
          {"type": "onset", "labels": ["onset"], "borderColor": "darken", "bgColor":"pink"},
          {"type": "direction", "labels": ["direction"], "borderColor": "darken", "bgColor":"pink"},
          {"type": "step", "labels": ["step"], "borderColor": "darken", "bgColor":"pink"}
         ]
      }
    ]
};

// docData is initially empty.
var docData = {};

head.ready(function() {

    var syntaxLiveDispatcher = Util.embed('syntax',
        $.extend({'collection': null}, collData),
        $.extend({}, docData),
        webFontURLs
    );
    var mentionsLiveDispatcher = Util.embed('mentions',
        $.extend({'collection': null}, collData),
        $.extend({}, docData),
        webFontURLs
    );

    $('form').submit(function (event) {

        // stop the form from submitting the normal way and refreshing the page
        event.preventDefault();

        // collect form data
        var formData = {
            'sent': $('textarea[name=text]').val(),
            'showEverything': $('input[name=showEverything]').is(':checked')
        }

        if (!formData.sent.trim()) {
            alert("Please write something.");
            return;
        }

        // show spinner
        document.getElementById("overlay").style.display = "block";

        // process the form
        $.ajax({
            type: 'GET',
            url: 'parseSentence',
            data: formData,
            dataType: 'json',
            encode: true
        })
        .fail(function () {
            // hide spinner
            document.getElementById("overlay").style.display = "none";
            alert("error");
        })
        .done(function (data) {
            console.log(data);
            syntaxLiveDispatcher.post('requestRenderData', [$.extend({}, data.syntax)]);
            mentionsLiveDispatcher.post('requestRenderData', [$.extend({}, data.mentions)]);
            document.getElementById("mentionsDisplayString").innerHTML = data.mentionsDisplayString;
            document.getElementById("parse").innerHTML = data.parse;
            // hide spinner
            document.getElementById("overlay").style.display = "none";
        });

    });
});
