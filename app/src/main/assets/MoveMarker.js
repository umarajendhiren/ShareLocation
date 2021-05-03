// Replace with your own API key
var API_KEY = 'YOUR_API_KEY';

// Icons for markers
var RED_MARKER = 'https://maps.google.com/mapfiles/ms/icons/red-dot.png';
var GREEN_MARKER = 'https://maps.google.com/mapfiles/ms/icons/green-dot.png';
var BLUE_MARKER = 'https://maps.google.com/mapfiles/ms/icons/blue-dot.png';
var YELLOW_MARKER = 'https://maps.google.com/mapfiles/ms/icons/yellow-dot.png';

// URL for places requests
var PLACES_URL = 'https://maps.googleapis.com/maps/api/place/details/json?' +
                'key=' + API_KEY + '&placeid=';

// URL for Speed limits
var SPEED_LIMIT_URL = 'https://roads.googleapis.com/v1/speedLimits';

var coords;

/**
 * Current Roads API threshold (subject to change without notice)
 * @const {number}
 */
var DISTANCE_THRESHOLD_HIGH = 300;
var DISTANCE_THRESHOLD_LOW = 200;

/**
 * @type Array<ExtendedLatLng>
 */
var originals = [];     // the original input points, a list of ExtendedLatLng

var interpolate = true;
var map;
var placesService;
var originalCoordsLength;

// Settingup Arrays
var infoWindows = [];
var markers = [];
var placeIds = [];
var polylines = [];
var snappedCoordinates = [];
var distPolylines = [];

// Symbol that gets animated along the polyline
var lineSymbol = {
  path: google.maps.SymbolPath.CIRCLE,
  scale: 8,
  strokeColor: '#005db5',
  strokeWidth: '#005db5'
};

// Example 1 - Frolick around Sydney
var eg1 = '-33.870315,151.196532|-33.869979,151.197355|' +
    '-33.870044,151.197712|-33.870358,151.198206|' +
    '-33.870595,151.198376|-33.870640,151.198398|' +
    '-33.870620,151.198449|-33.870951,151.198525|' +
    '-33.871040,151.198528|-33.872031,151.198413';

// Example 2 - Lap around Canberra
var eg2 = '-35.274346,149.130168|-35.278012,149.129583|' +
    '-35.280329,149.129073|-35.280999,149.129293|' +
    '-35.281441,149.129846|-35.281945,149.130034|' +
    '-35.282825,149.129567|-35.283022,149.128811|' +
    '-35.284734,149.128366';

// Example 3 - Path with unsnappable point
var eg3 = '-35.274346,149.094000|-35.278012,149.129583|' +
    '-35.280329,149.129073|-35.280999,149.129293|' +
    '-35.281441,149.129846';

// Example 4 - Drive erratically in Elkin
var eg4 = '36.28881,-80.8525|36.287038,-80.85313|36.286161,-80.85369|' +
    '36.28654,-80.85418|36.2846,-80.84766|36.28355,-80.84669';

// Initialize
function initialize() {
  $('#eg1').click(function(e) {
    $('#coords').val(eg1);
    $('#plot').trigger('click');
  });

  $('#eg2').click(function(e) {
    $('#coords').val(eg2);
    $('#plot').trigger('click');
  });

  $('#eg3').click(function(e) {
    $('#coords').val(eg3);
    $('#plot').trigger('click');
  });

  $('#eg4').click(function(e) {
    $('#coords').val(eg4);
    $('#plot').trigger('click');
  });

  $('#toggle').click(function(e) {
     if ($('#panel').css("display") != 'none') {
        $('#toggle').html("+");
        $('#panel').hide();
     } else {
        $('#toggle').html("&mdash;");
        $('#panel').show();
     }
  });

  // Centre the map on Sydney
  var mapOptions = {
    center: {'lat': -33.870315, 'lng': 151.196532},
    zoom: 14
  };

  // Map object
  map = new google.maps.Map(document.getElementById('map'), mapOptions);

  // Places object
  placesService = new google.maps.places.PlacesService(map);

  // Reset the map to a clean state and reset all variables
  // used for displaying each request
  function clearMap() {
    // Clear the polyline
    for (var i = 0; i < polylines.length; i++) {
      polylines[i].setMap(null);
    }
    // Clear all markers
    for (var i = 0; i < markers.length; i++) {
      markers[i].setMap(null);
    }
    // Clear all the distance polylines
    for (var i = 0; i < distPolylines.length; i++) {
      distPolylines[i].setMap(null);
    }
    // Clear all info windows
    for (var i = 0; i < infoWindows.length; i++) {
      infoWindows[i].close();
    }

    // Empty everything
    polylines = [];
    markers = [];
    distPolylines = [];
    snappedCoordinates = [];
    placeIds = [];
    infoWindows = [];
    $('#unsnappedPoints').empty();
    $('#warningMessage').empty();
  }

  // Parse the value in the input element
  // to get all coordinates
  function parseCoordsFromQuery(input) {
    var coords;
    input = decodeURIComponent(input);
    if (input.split('path=').length > 1) {
      input = decodeURIComponent(input);
      // Split on the ampersand to get all params
      var parts = input.split('&');
      // Check each part to see if it starts with 'path='
      // grabbing out the coordinates if it does
      for (var i = 0; i < parts.length; i++) {
        if (parts[i].split('path=').length > 1) {
          coords = parts[i].split('path=')[1];
          break;
        }
      }
    } else {
      coords = decodeURIComponent(input);
    }

    // Parse the "Lat,Lng|..." coordinates into an array of ExtendedLatLng
    originals = [];
    var points = coords.split('|');
    for (var i = 0; i < points.length; i++) {
      var point = points[i].split(',');
      originals.push({lat: Number(point[0]), lng: Number(point[1]), index:i});
    }

    return coords;
  }

  // Clear the map of any old data and plot the request
  $('#plot').click(function(e) {
    clearMap();
    bendAndSnap();
    drawDistance();
    e.preventDefault();
  });

  // Make AJAX request to the snapToRoadsAPI
  // with coordinates parsed from text input element.
  function bendAndSnap() {
    coords = parseCoordsFromQuery($('#coords').val());
    location.hash = coords;
    $.ajax({
      type: 'GET',
      url: 'https://roads.googleapis.com/v1/snapToRoads',
      data: {
        interpolate: $('#interpolate').is(':checked'),
        key: API_KEY,
        path: coords
      },
      success: function(data) {
        $('#requestURL').html('<a target="blank" href="' +
            this.url + '">Request URL</a>');
        processSnapToRoadResponse(data);
        drawSnappedPolyline(snappedCoordinates);
        drawOriginals(originals);
        fitBounds(markers);
      },
      error: function() {
        $('#requestURL').html('<strong>That query didn\'t work :(</strong>' +
            '<p>Try looking at the <a href="' + this.url +
            '">Request URL</a></p>');
        clearMap();
      }
    });
  }

  // Toggle the distance polylines of the original points to show on the maps
  $('#distance').click(function(e) {
    for (var i = 0; i < distPolylines.length; i++) {
        distPolylines[i].setVisible(!distPolylines[i].getVisible());
    }
    // Clear all infoWindows associated with distance polygons on toggle
    for (var i = 0; i < infoWindows.length; i++) {
      if (infoWindows[i].dist) {
        infoWindows[i].close();
      }
    }
    e.preventDefault();
  });

  /**
   * Compute the distance between each original point and create a polyline
   * for each pair. Polylines are initially hidden on creation
   */
  function drawDistance() {
    for (var i = 0; i < originals.length - 1; i++) {
      var origin = new google.maps.LatLng(originals[i]);
      var destination = new google.maps.LatLng(originals[i+1]);
      var distance =
    google.maps.geometry.spherical.computeDistanceBetween(origin, destination);

      // Round the distance value to two decimal places
      distance = Math.round(distance * 100) / 100;

      var color;
      var weight;
      if (distance > DISTANCE_THRESHOLD_HIGH) {
        color = '#CC0022';
        weight = 7;
      } else if (distance < DISTANCE_THRESHOLD_HIGH &&
                 distance > DISTANCE_THRESHOLD_LOW) {
        color = '#FF6600';
        weight = 6;
      } else {
        color = '#22CC00';
        weight = 5;
      }
      var polyline = new google.maps.Polyline({
        strokeColor: color,
        strokeOpacity: 0.4,
        strokeWeight: weight,
        geodesic: true,
        visible: false,
        map: map
      });
      polyline.setPath([origin, destination]);

      distPolylines.push(polyline);
      infoWindows.push(addPolyWindow(polyline, distance, i));
    }
  }

  /**
   * Add an info window to the polyline displaying the original
   * points and the distance
   */
  function addPolyWindow(polyline, distance, index) {
    var infoWindow = new google.maps.InfoWindow();
    var content = '<div style="width:100%"><p>' +
        '<strong>Original Index: </strong>' + index + '<br>' +
        '<strong>Coords:</strong> (' +
        originals[index].lat + ',' + originals[index].lng + ')' +
        '<br>to<br>' +
        '<strong>Original Index: </strong>' + (index+1) + '<br>' +
        '<strong>Coords:</strong> (' +
        originals[index+1].lat + ',' + originals[index+1].lng + ')<br><br>' +
        '<strong>Distance:  </strong>' +  distance + ' m<br>';
    if (distance > DISTANCE_THRESHOLD_HIGH) {
      content += '<span style="color:#CC0022;font-style:italic">' +
          '*Large distance (>300m) may affect snapping</span><br>' +
          'Please see <a href="https://developers.google.com/maps/' +
          'documentation/roads/snap#parameter_usage" ' +
          'target="_blank">Roads API documentation</a>';
    }
    content += '</p></div>';

    infoWindow.setContent(content);
    infoWindow.dist = true;

    polyline.addListener('click', function(e) {
        infoWindow.setPosition(e.latLng);
        infoWindow.open(map);
    });

    polyline.addListener('mouseover', function(e) {
        polyline.setOptions({strokeOpacity: 1.0});
    });

    polyline.addListener('mouseout', function(e) {
        polyline.setOptions({strokeOpacity: 0.4});
    });

    return infoWindow;
  }

  // Parse the value in the input element
  // to get all coordinates
  function getMissingPoints(originalIndexes, originalCoordsLength) {
    var unsnappedPoints = [];
    var coordsArray = coords.split('|');
    var hasMissingCoords = false;
    for (var i = 0; i < originalCoordsLength; i++) {
      if (originalIndexes.indexOf(i) < 0) {
        hasMissingCoords = true;
        var latlng = {
          'lat': parseFloat(coordsArray[i].split(',')[0]),
          'lng': parseFloat(coordsArray[i].split(',')[1])
        };

        unsnappedPoints.push(latlng);
        latlng.unsnapped = true;
      }
    }
    return unsnappedPoints;
  }

  // Parse response from snapToRoads API request
  // Store all coordinates in response
  // Calls functions to add markers to map for unsnapped coordinates
  function processSnapToRoadResponse(data) {
    var originalIndexes = [];
    var unsnappedMessage = '';

    for (var i = 0; i < data.snappedPoints.length; i++) {
      var latlng = {
        'lat': data.snappedPoints[i].location.latitude,
        'lng': data.snappedPoints[i].location.longitude
      };
      var interpolated = true;

      if (data.snappedPoints[i].originalIndex != undefined) {
        interpolated = false;
        originalIndexes.push(data.snappedPoints[i].originalIndex);
        latlng.originalIndex = data.snappedPoints[i].originalIndex;
      }

      latlng.interpolated = interpolated;
      snappedCoordinates.push(latlng);
      placeIds.push(data.snappedPoints[i].placeId);

      // Cross-reference the original point and this snapped point.
      latlng.related = originals[latlng.originalIndex];
      originals[latlng.originalIndex].related = latlng;
    }

    var unsnappedPoints = getMissingPoints(
        originalIndexes,
        coords.split('|').length
    );

    for (var i = 0; i < unsnappedPoints.length; i++) {
      var marker = addMarker(unsnappedPoints[i]);
      var infowindow = addBasicInfoWindow(marker, unsnappedPoints[i], i);
      infoWindows.push(infowindow);

      unsnappedMessage += unsnappedPoints[i].lat + ',' +
          unsnappedPoints[i].lng + '<br>';
    }

    if (unsnappedPoints.length) {
      unsnappedMessage = '<strong>' +
         'These points weren\'t snapped: ' +
         '</strong><br>' + unsnappedMessage;
      $('#unsnappedPoints').html(unsnappedMessage);
    }

    if (data.warningMessage) {
      $('#warningMessage').html('<span style="color:#CC0022;' +
          'font-style:italic;font-size:12px">' + data.warningMessage + '<br/>' +
          '<a target="_blank" href="https://developers.google.com/maps/' +
          'documentation/roads/snap">https://developers.google.com/maps/' +
          'documentation/roads/snap</a>');
      $('#distance').trigger('click');
    }
  }

  // Draw the polyline for the snapToRoads API response
  // Call functions to add markers and infowindows for each snapped
  // point along the polyline.
  function drawSnappedPolyline(snappedCoords) {
    var snappedPolyline = new google.maps.Polyline({
      path: snappedCoords,
      strokeColor: '#005db5',
      strokeWeight: 6,
      icons: [{
        icon: lineSymbol,
        offset: '100%'
      }]
    });

    snappedPolyline.setMap(map);
    animateCircle(snappedPolyline);

    polylines.push(snappedPolyline);

    for (var i = 0; i < snappedCoords.length; i++) {
      var marker = addMarker(snappedCoords[i]);
      var infoWindow = addDetailedInfoWindow(marker,
          snappedCoords[i],
          placeIds[i]);
      infoWindows.push(infoWindow);
    }
  }

  // Draw the original input.
  // Call functions to add markers and infowindows for each point.
  function drawOriginals(originalCoords) {
    for (var i = 0; i < originalCoords.length; i++) {
      var marker = addMarker(originalCoords[i]);
      var infoWindow = addBasicInfoWindow(marker, originalCoords[i], i);
      infoWindows.push(infoWindow);
    }
  }

  // Infowindow used for unsnappable coordinates
  function addBasicInfoWindow(marker, coords, index) {
    var infowindow = new google.maps.InfoWindow();
    var content = '<div style="width:99%"><p>' +
        '<strong>Lat/Lng:</strong><br>' +
        '(' + coords.lat + ',' + coords.lng + ')<br>' +
        (index != undefined ? '<strong>Index: </strong>' + index : '') +
        '</p></div>';

    infowindow.setContent(content);

    google.maps.event.addListener(marker, 'click', function() {
      openInfoWindow(infowindow, marker);
    });

    return infowindow;
  }

  // Infowindow used for snapped points
  // Makes request to Places Details API to get data about each
  // Place ID.
  // Requests speed limit of each location using Roads SpeedLimit API
  function addDetailedInfoWindow(marker, coords, placeId) {
    var infowindow = new google.maps.InfoWindow();
    var placesRequestUrl = PLACES_URL + placeId;
    var detailsUrl = '<a target="_blank" href="' +
        placesRequestUrl + '">' +
        placeId + '</a></li>';

    // On click we make a request to the Places API
    // This is to avoid OVER_QUERY_LIMIT if we just requested everything
    // at the same time
    google.maps.event.addListener(marker, 'click', function() {
      content = '<div style="width:99%"><p>';

      function finishInfoWindow(placeDetails) {
        content += '<strong>Place Details: </strong>' + placeDetails + '<br>' +
            '<strong>' +
            (coords.interpolated ? 'Coords' : 'Snapped coords') +
            ': </strong>' +
            '(' + coords.lat.toFixed(5) + ',' +
            coords.lng.toFixed(5) + ')<br>';

        if (!(coords.interpolated)) {
          var original = originals[coords.originalIndex];
          content += '<strong>Original coords: </strong>' +
              '(' + original.lat + ',' + original.lng + ')<br>' +
              '<strong>Original Index: </strong>' +
              coords.originalIndex;
        }
        content += '</p></div>';
        infowindow.setContent(content);
        openInfoWindow(infowindow, marker);
      };

      getPlaceDetails(placeId, function(place) {
        if (place.name) {
          content += '<strong>' + place.name + '</strong><br>';
        }
        getSpeedLimit(placeId, function(data) {
          if (data.speedLimits) {
            content += '<strong>Speed Limit: </strong>' +
                data.speedLimits[0].speedLimit + ' km/h <br>';
          }
          finishInfoWindow(detailsUrl);
        });
      }, function() { finishInfoWindow("<em>None available</em>"); });
    });
    return infowindow;
  }

  // Avoid infoWindows staying open if the pano changes
  listenForPanoChange();

  // If the user came to the page with a particular path or URL,
  // immediately plot it.
  if (location.hash.length > 1) {
    coords = parseCoordsFromQuery(location.hash.slice(1));
    $('#coords').val(coords);
    $('#plot').click();
  }
} // End init function

// Call the initialize function once everything has loaded
google.maps.event.addDomListener(window, 'load', initialize);

// Load the control panel in a floating div if it is not loaded in an iframe
// after the textarea has been rendered
$("#coords").ready(function() {
    if (!window.frameElement) {
       $('#panel').addClass("floating panel");
       $('#button-div').addClass("button-div");
       $('#coords').removeClass("coords-large").addClass("coords-small");
       $('#toggle').show();
       $('#map').height('100%');
    }
});

/**
*  latlng literal with extra properties to use with the RoadsAPI
*  @typedef {Object} ExtendedLatLng
*   lat:string|float
*   lng:string|float
*   interpolated:boolean
*   unsnapped:boolean
*/

/**
 * Add a line to the map for highlighting the connection between two
 * markers while the mouse is over it.
 * @param {ExtendedLatLng} from - The origin of the line
 * @param {ExtendedLatLng} to - The destination of the line
 * @return {!Object} line - the polyline object created
 */
function addOverline(from, to) {
  return addLine("overline", from, to, '#ff77ff', 4, 1.0, 2.0, false);
}

/**
 * Add a line to the map for highlighting the connection between two
 * markers while the mouse is NOT over it.
 * @param {ExtendedLatLng} from - The origin of the line
 * @param {ExtendedLatLng} to - The destination of the line
 * @return {!Object} line - the polyline object created
 */
function addOutline(from, to) {
  return addLine("outline", from, to, '#bb33bb', 2, 0.5, 1.35, true);
}

/**
 * Add a line to the map for highlighting the connection between two
 * markers.
 * @param {string}         attrib  - The attribute to use for managing the line
 * @param {ExtendedLatLng} from    - The origin of the line
 * @param {ExtendedLatLng} to      - The destination of the line
 * @param {string}         color   - The color of the line
 * @param {number}         weight  - The weight of the line
 * @param {number}         opacity - The opacity of the line (0..1)
 * @param {number}         scale   - The scale of the arrow-head (pt)
 * @param {boolean}        visible - The visibility of the line
 * @return {!Object}       line    - the polyline object created
 */
function addLine(attrib, from, to, color, weight, opacity, scale, visible) {
  from[attrib] = new google.maps.Polyline({
    path:         [from, to],
    strokeColor:  color,
    strokeWeight:  weight,
    strokeOpacity: opacity,
    icons:[{
      offset: "0%",
      icon: {
        scale: scale/*pt*/,
        path:  google.maps.SymbolPath.BACKWARD_CLOSED_ARROW
      }
    }]
  });
  from[attrib].setVisible(visible);
  from[attrib].setMap(map);
  to[attrib] = from[attrib];
  polylines.push(from[attrib]);
  return from[attrib];
}

/**
 * Add a pair of lines to the map for highlighting the connection between two
 * markers; one visible while the mouse is over the marker (the "overline"),
 * the other while it is not (the "outline").
 * @param {ExtendedLatLng} from - The origin of the line (the original input)
 * @param {ExtendedLatLng} to - The destination of the line (the snapped point)
 * @return {!Object} line - the polyline object created
 */
function addCorrespondence(coords, marker) {
  if (!coords.overline) { addOverline(coords, coords.related); }
  if (!coords.outline)  { addOutline(coords, coords.related); }

  marker.addListener('mouseover', function(mevt) {
    coords.outline.setVisible(false);
    coords.overline.setVisible(true);
    coords.related.marker.setOpacity(1.0);
  });
  marker.addListener('mouseout', function(mevt) {
    coords.overline.setVisible(false);
    coords.outline.setVisible(true);
    coords.related.marker.setOpacity(0.5);
  });
}

/**
 * Add a marker to the map and check for special 'interpolated'
 * and 'unsnapped' properties to control which colour marker is used
 * @param {ExtendedLatLng} coords - Coords of where to add the marker
 * @return {!Object} marker - the marker object created
 */
function addMarker(coords) {
  var marker = new google.maps.Marker({
    position: coords,
    title: coords.lat + ',' + coords.lng,
    map: map,
    opacity: 0.5,
    icon: RED_MARKER
  });

  // Coord should NEVER be interpolated AND unsnapped
  if (coords.interpolated) {
    marker.setIcon(BLUE_MARKER);
  } else if (!coords.related) {
    marker.setIcon(YELLOW_MARKER);
  } else if (coords.originalIndex != undefined) {
    marker.setIcon(RED_MARKER);
    addCorrespondence(coords, marker);
  } else {
    marker.setIcon({url: GREEN_MARKER,
                    scaledSize: {width: 20, height: 20}});
    addCorrespondence(coords, marker);
  }

  // Make markers change opacity when the mouse scrubs across them
  marker.addListener('mouseover', function(mevt) {
    marker.setOpacity(1.0);
  });
  marker.addListener('mouseout', function(mevt) {
    marker.setOpacity(0.5);
  });

  coords.marker = marker;  // Save a reference for easy access later
  markers.push(marker);

  return marker;
}

/**
 * Animate an icon along a polyline
 * @param {Object} polyline The line to animate the icon along
 */
function animateCircle(polyline) {
  var count = 0;
  // fallback icon if the poly has no icon to animate
  var defaultIcon = [
    {
      icon: lineSymbol,
      offset: '100%'
    }
  ];
  window.setInterval(function() {
    count = (count + 1) % 200;
    var icons = polyline.get('icons') || defaultIcon;
    icons[0].offset = (count / 2) + '%';
    polyline.set('icons', icons);
  }, 20);
}

/**
 * Fit the map bounds to the current set of markers
 * @param {Array<Object>} markers Array of all map markers
 */
function fitBounds(markers) {
  var bounds = new google.maps.LatLngBounds;
  for (var i = 0; i < markers.length; i++) {
    bounds.extend(markers[i].getPosition());
  }
  map.fitBounds(bounds);
}

/**
 * Uses Places library to get Place Details for a Place ID
 * @param {string}   placeId         The Place ID to look up
 * @param {Function} foundCallback   Called if the place is found
 * @param {Function} missingCallback Called if nothing is found
 * @param {Function} errorCallback   Called if request fails
 */
function getPlaceDetails(placeId,
                         foundCallback, missingCallback, errorCallback) {
  var request = {
    placeId: placeId
  };

  placesService.getDetails(request, function(place, status) {
    if (status == google.maps.places.PlacesServiceStatus.OK) {
      foundCallback(place);
    } else if (status == google.maps.places.PlacesServiceStatus.NOT_FOUND) {
      missingCallback();
    } else if (errorCallback) {
      errorCallback();
    }
  });
}

/**
 * AJAX request to the Roads Speed Limit API.
 * Request the speed limit for the Place ID
 * @param {string}   placeId         Place ID to request the speed limit for
 * @param {Function} successCallback Called if request is successful
 * @param {Function} errorCallback   Called if request fails
 */
function getSpeedLimit(placeId, successCallback, errorCallback) {
  $.ajax({
    type: 'GET',
    url: SPEED_LIMIT_URL,
    data: {
      placeId: placeId,
      key: API_KEY
    },
    success: successCallback,
    error: errorCallback
  });
}

/**
 * Open an infowindow on either the map or the active streetview pano
 * @param {Object} infowindow Infowindow to be opened
 * @param {Object} marker Marker the infowindow is anchored to
 */
function openInfoWindow(infowindow, marker) {
  // If streetView is visible display the infoWindow over the pano
  // and anchor to the marker
  if (map.getStreetView().getVisible()) {
    infowindow.open(map.getStreetView(), marker);
  }
  // Otherwise open it on the map and anchor to the marker
  else {
    infowindow.open(map, marker);
  }
}

/**
 * Add event listener to for when the active pano changes
 */
function listenForPanoChange() {
  var pano = map.getStreetView();

  // Close all open markers when the pano changes
  google.maps.event.addListener(pano, 'position_changed', function() {
    closeAllInfoWindows(infoWindows);
  });
}

/**
 * Close all open infoWindows
 * @param {Array<Object>} infoWindows - all infowindow objects
 */
function closeAllInfoWindows(infoWindows) {
  for (var i = 0; i < infoWindows.length; i++) {
    infoWindows[i].close();
  }
}

