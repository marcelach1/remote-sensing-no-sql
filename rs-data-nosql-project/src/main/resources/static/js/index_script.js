/*
Id's and classes for HTML template dynamically accessed w.r.t. application flow.
 */
const labelWrapperId = 'label_fields_wrapper';
const labelFieldsClass = 'label-field';
const addLabelButtonClass = 'add_label_field';
const removeLabelButtonClass = 'remove_label_field';
const displayQueryInformationBtnId = 'display-query-information';
const sendAnalogueQueryButtonClass = 'get-output-btn';
const outputPanelId = 'output';
const outputHeadingId = 'output-heading';
const modalTooManyLabelsId = 'too-many-labels-modal';
const modalQueryInformationId = 'query-information';
const allLabelsSelectedBadgeId = 'all-labels-selected-badge';


/*
List of possible labels to fill the dropdown menus with - initially empty.
 */
let possible_labels = [];

/*
Number of currently active labels in the drop-down section.
 */
let n_dropdown_active = 0;

/*
Initially filling the array of possible labels with all 44 available ones to be able to fill the dropdown fields later.
 */
$.get('http://localhost:8080/labels', function(data, status){
    possible_labels = data;
});

/*
If the document is loaded, the map component and buttons are initialized.
 */
$(document).ready(function() {
    initializeMap();
    initializeButtons();
});

/*
Function initializing and rendering the Leaflet map component.
 */
function initializeMap() {
    // initialize overall map component
    const map = L.map('mapid', {
        maxBounds: [[-90, -180],[90, 180]], // restricts coordinate range to real world
        minZoom: 2, // prevent showing empty tiles to the user
        iconSize: [80, 80], // properly sized icons
    }).setView([52.5260, 15.2551], 5);

    // initialize visible tile layer
    const tileLayer = 'https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=A0FqhYRF6r84fDDvpWye';
    L.tileLayer(tileLayer, {
        // attribution (also in bottom right-hand corner of map component) needed for full Leaflet functionality
        attribution: '<a href="https://www.maptiler.com/copyright/" target="_blank">&copy; MapTiler</a> ' +
            '<a href="https://www.openstreetmap.org/copyright" target="_blank">&copy; OpenStreetMap contributors</a>',
        noWrap: true, // do not render raster layers outside of world
        tileSize: 512, // in default map size
        zoomOffset: -1, // disables tile layer zoom as tile size already adjusted
    }).addTo(map);

    // configuration part: user should be able to draw rectangles and non-overlapping polygons for visual queries
    // Leaflet code example: https://jsfiddle.net/user2314737/324h2d9q/
    // last access: 02 July 2020, around 1PM UTC+1
    const editableLayers = new L.FeatureGroup();
    const drawPluginOptions = {
        position: 'topright',
        draw: {
            polygon: {
                allowIntersection: false,
                drawError: {
                    color: '#e1e100',
                    message: '<strong>Intersecting polygons not supported!<strong>'
                },
                shapeOptions: {
                    color: '#97009c'
                }
            },
            polyline: false,
            circle: false,
            marker: false,
        }
    };

    // adding drawing elements to map
    map.addLayer(editableLayers);
    map.addControl(new L.Control.Draw(drawPluginOptions));

    // if user has drawn object in map: erase old drawings, show new one and trigger visual query including
    // coordinates of drawing
    map.on('draw:created', function(e) {
        editableLayers.clearLayers();
        editableLayers.addLayer(e.layer);
        sendVisualQuery(e.layer.toGeoJSON().geometry);
    });

    // erase drawings if user clicks on map
    map.on('click', function(e) {
        editableLayers.clearLayers();
    });
}

/*
Initializes template buttons.
 */
function initializeButtons() {
    const wrapper = $('#' + labelWrapperId);

    // delete dropdown field if user clicks on according X symbol
    $(wrapper).on('click','.' + removeLabelButtonClass, function(e) {
        e.preventDefault();
        $(this).parent('div').remove();
        $('.' + addLabelButtonClass).prop('disabled', false);
        n_dropdown_active -= 1;

        // if no dropdown field for labels is active: show "All labels selected" badge
        if (n_dropdown_active < 1) {
            $('#' + allLabelsSelectedBadgeId).show();
        }
    });

    // hide "All labels selected" badge if dropdown menus (= label restrictions) are active
    $('.' + addLabelButtonClass).click(function(e){
        addLabelField();
        $('#' + allLabelsSelectedBadgeId).hide();
    });

    // display query information modal when corresponding button clicked
    $('#' + displayQueryInformationBtnId).click(function(e) {
        $('#' + modalQueryInformationId).modal('show');
    });

    // initialize "send analogue query" button
    $('#' + sendAnalogueQueryButtonClass).on('click', function(e) {
        // retrieve and cast four coordinates (two pairs: upper left and lower right corner) from HTML number input fields
        const ulx = parseFloat($('#ulx').val());
        const uly = parseFloat($('#uly').val());
        const lrx = parseFloat($('#lrx').val());
        const lry = parseFloat($('#lry').val());
        const labels = getSelectedLabels();
        const isExclusiveLabelQuery = (!ulx && !uly && !lrx && !lry);
        const payload = JSON.stringify(getPayloadForAnalogueParameters(ulx, uly, lrx, lry, labels, isExclusiveLabelQuery));

        // if query includes coordinates, but specified coordinates invalid: show modal
        if (!isExclusiveLabelQuery && !coordinatesValid(ulx, uly, lrx, lry)) {
            $('#' + modalQueryInformationId).modal('show');
        } else { // valid analogue query
            console.log('POSTing as analogue query: ', payload);
            const startMillis = Date.now()

            // send AJAX request (POST) to server to fetch and display matching image patch names
            $.ajax({
                url:'http://localhost:8080/query-analogue',
                type:'POST',
                data: payload,
                contentType:"application/json",
                dataType:"json",
                success: function(result){
                    // prepare output section
                    const heading =  $('#' + outputHeadingId);
                    const panel = $('#' + outputPanelId);
                    panel.empty();
                    heading.empty();
                    if (!result || !result.patchNames) {
                        heading.append('Names of matching image patches (0): ');
                    } else {
                        heading.append('Names of matching image patches (' + result.patchNames.length + '): ');
                    }

                    heading.attr( "style", "visibility: visible !important;");

                    // render each result image patch name in output section
                    $.each(result.patchNames, function(index, value){
                        if(value !== null) {
                            const div = '<div>' + value + '</div>';
                            $('#' + outputPanelId).append($(div));
                        }
                    });

                    // if no matching image patches: show "None" instead of empty output section
                    if (!result || !result.patchNames || result.patchNames.length === 0) {
                        $('#' + outputPanelId).append('<div>None</div>');
                    }

                    console.log('Total milliseconds elapsed since request: ', Date.now() - startMillis);
                }
            });
        }
    });
}

/*
Triggers a visual query on the server containing the passed coordinates and selected labels.
 */
function sendVisualQuery(geoJsonGeometry) {
    const selectedLabels = getSelectedLabels();
    const payload = JSON.stringify({
        'geometry': geoJsonGeometry,
        'labels': selectedLabels.length > 0 ? selectedLabels : possible_labels,
        'queryType': $('input[name="query-option-radio"]:checked').val(),
        'geospatialQueryOperator': $('input[name="geospatial-option-radio"]:checked').val(),
    });
    console.log('POSTing as visual query: ', payload);
    const startMillis = Date.now()

    // send AJAX request (POST) to server to fetch and display matching image patch names
    $.ajax({
        url: 'http://localhost:8080/query-visual',
        type:'POST',
        data: payload,
        contentType:"application/json",
        dataType:"json",
        success: function(result){
            // prepare output section
            const heading =  $('#' + outputHeadingId);
            const panel = $('#' + outputPanelId);
            panel.empty();
            heading.empty();
            if (!result || !result.patchNames) {
                heading.append('Names of matching image patches (0): ');
            } else {
                heading.append('Names of matching image patches (' + result.patchNames.length + '): ');
            }
            heading.attr( "style", "visibility: visible !important;");

            // render each result image patch name in output section
            $.each(result.patchNames, function(index, value){
                if(value !== null) {
                    const div = '<div>' + value + '</div>';
                    $('#' + outputPanelId).append($(div));
                }
            });

            // if no matching image patches: show "None" instead of empty output section
            if (!result || !result.patchNames || result.patchNames.length === 0) {
                $('#' + outputPanelId).append('<div>None</div>');
            }

            console.log('Total milliseconds elapsed since request: ', Date.now() - startMillis);
        }
    });
}

/*
Returns the payload needed to send an analogue query to the server.
 */
function getPayloadForAnalogueParameters(ulx, uly, lrx, lry, selectedLabels, isExclusiveLabelQuery) {
    const resultType =  {
        'geometry': {
            'type': 'Polygon',
            'coordinates': [
                [
                    [ulx, uly], [lrx, uly], [lrx, lry], [ulx, lry], [ulx, uly],
                ]
            ]
        },
        'labels': selectedLabels.length > 0 ? selectedLabels : possible_labels,
        'queryType': $('input[name="query-option-radio"]:checked').val(),
        'geospatialQueryOperator': $('input[name="geospatial-option-radio"]:checked').val(),
    };

    if (isExclusiveLabelQuery) {
        resultType.geometry.coordinates = [];
    }

    return resultType;
}

/*
Checks whether the passed two pairs of coordinates (upper left and lower right corner) are valid.
 */
function coordinatesValid(ulx, uly, lrx, lry) {
    const areNumeric = $.isNumeric(ulx) && $.isNumeric(uly) && $.isNumeric(lrx) && $.isNumeric(lry);
    let isValidX = false;
    let isValidY = false;

    // Spring's MongoDB API does not yet support bigger latitude ranges for geospatial queries (only natively)
    const isSmallerThanHemisphere = (lrx - ulx) < 180.0;

    if (ulx <= lrx) {
        if (ulx >= -180.0 && lrx <= 180.0 && ulx !== lrx && isSmallerThanHemisphere) {
            isValidX = true;
        }
    }

    if (lry <= uly) {
        if (lry >= -90.0 && uly <= 90.0 && uly !== lry) {
            isValidY = true;
        }
    }

    return areNumeric && isValidX && isValidY;
}

/*
Returns the currently actively selected labels from dropdown fields without duplicates.
 */
function getSelectedLabels() {

    const selected = $('.' + labelFieldsClass).map(function() {
        return $(this).val();
    }).get();

    return [...new Set(selected)];
}

/*
Adds a label dropdown field to the user interface.
 */
function addLabelField() {
    // number of active dropdown fields for labels should equal the number of available labels in the worst case
    // to avoid unnecessary HTML elements
    if (n_dropdown_active === possible_labels.length) {
        $('.' + addLabelButtonClass).prop('disabled', true);
        $('#' + modalTooManyLabelsId).modal('show');
    } else {
        // fully add and render a new dropdown field for label selection
        n_dropdown_active += 1;

        let option = '';
        for (let i=0; i < possible_labels.length; i++){
            option += '<option value="'+ possible_labels[i] + '">' + possible_labels[i] + '</option>';
        }

        const remove_button = '<div>' +
            '<select class="' + labelFieldsClass + '" >' + option + '</select>' +
            '<label class="' + removeLabelButtonClass + ' fa fa-times"></label>' +
            '</div>';
        const wrapper = $('#' + labelWrapperId);
        $(wrapper).append(remove_button);
    }
}
