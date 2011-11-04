// var apikey = "82NV49lmavKaljw2";
var apikey = "L420Jvg7mrXx507T";

var apiHost = "http://api.busbuddy.no:8080";
var holdeplasser;
var markers;
var map;
var lastClickMarker;
var markerClicked = new Array();
// var data;

function initialize() {
	var latlng = new google.maps.LatLng(63.4186338, 10.3920824);
	var infoOptions = {
		zoom: 13,
		center: latlng,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById('map_canvas'), infoOptions);

	holdeplasser = new Array();
	markers = new Array();

	busbuddyFetch(apiHost+"/api/1.3/busstops?apiKey="+apikey+"&callback");
}

function getHoldeplasser(data) {
	for (var i = 0; i < data.stops.length; i++) {
		holdeplasser[i] = new Holdeplass(data.stops[i].id, data.stops[i].name, data.stops[i].latitude, data.stops[i].longitude);
		addMarker(holdeplasser[i]);
	}
}

function busbuddyFetch(url) {
	var script = document.createElement("script");
	script.setAttribute("src", url);
	script.setAttribute("type", "text/javascript");
	document.body.appendChild(script);
}

function addMarker(holdeplass) {
	var marker = new google.maps.Marker({
		position: holdeplass.getLatLng(),
		map: map,
		title: holdeplass.getName()
	});
	marker.setIcon('images/15x15_4.png');

	markers.push(marker);

	google.maps.event.addListener(marker, 'click', function() {
		markerClicked[0] = marker;
		markerClicked[1] = holdeplass;

		var infoBox = document.createElement("div");
		infoBox.style.cssText = "border-radius: 5px; border: 1px solid black; margin-top: 8px; background: #020c1c; padding: 0 15px 15px 15px;";
		infoBox.innerHTML = '<h2>' + markerClicked[1].getName() + '</h2><img src="images/loader2.gif" alt="loader"><p style="color: #ffffff;">laster inn data...</p>';

		var infoOptions = {
			content: infoBox,
			disableAutoPan: false,
			maxWidth: 0,
			pixelOffset: new google.maps.Size(-140, 0),
			zIndex: null,
			boxStyle: { 
		  		background: "url('images/arrow.gif') no-repeat",
		  		opacity: 0.9,
		  		width: "280px"
		  	},
			closeBoxMargin: "10px 2px 2px 2px",
			closeBoxURL: "images/close2.gif",
			infoBoxClearance: new google.maps.Size(1, 1),
			isHidden: false,
			pane: "floatPane",
			enableEventPropagation: false
		};

		markerClicked[2] = new InfoBox(infoOptions);

		if (lastClickMarker) lastClickMarker.close();
		lastClickMarker = markerClicked[2];

		markerClicked[2].open(map, marker);
		busbuddyFetch(apiHost+"/api/1.3/departures/" + holdeplass.getId() + "?apiKey="+apikey+"&callback");
	});
}

function busbuddyResponse(data) {
	if (data.busStops) {
		for (var i = 0; i < data.busStops.length; i++) {
			holdeplasser[i] = new Holdeplass(data.busStops[i].locationId, data.busStops[i].name, data.busStops[i].latitude, data.busStops[i].longitude);
			addMarker(holdeplasser[i]);
		}

	} else if (data.departures){
		addMessage(data, markerClicked);
	}
}

function addMessage(data) {
	var content = '<div style="border-radius: 5px; border: 1px solid black; margin-top: 8px; background: #020c1c; padding: 0 15px 15px 15px;"><h2>' + markerClicked[1].getName() + '</h2>';

	content += '<table><tr style="line-height: 15px;"><td width=30><span class="dynInfoNor">Linje</span><br><span class="dynInfoEng">Line</span></td><td width=100><span class="dynInfoNor">Destinasjon</span><br><span class="dynInfoEng">Destination</span></td><td><span class="dynInfoNor">Avgangstid</span><br><span class="dynInfoEng">Departure Time</span></td></tr>';

	for (var i in data.departures) {
		content += '<tr class="dynInfo"><td>' + data.departures[i].line + '</td><td>' + data.departures[i].destination + '</td><td>';
		if (data.departures[i].isRealtimeData) {
			content += timeDiff(data.departures[i].registeredDepartureTime);
		} else {
			content += 'Ca. ' + timeDiff(data.departures[i].scheduledDepartureTime);
		}
		content += '</td></tr>';
	}
	content += '</div>';
	markerClicked[2].setContent(content);
}

function showOverlays() {
	if (markers) for (var i in markers) markers[i].setMap(map);
	}