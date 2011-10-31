<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html> 
<html> 
<head> 
	<meta charset=utf-8> 
	<title>BusBuddy</title>
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no"> 
	<meta name="author" content="Tri M. Nguyen <mail@trimn.net>"> 
	<style> 
	html { height: 100%; }
	body { height: 100%; margin: 0; padding: 0; font: 11px/18px arial, sans-serif; }
	
	#map_canvas { height: 100%; }
	
	.dynInfo { color: #fee164; }
	.dynInfoNor { color: #ffffff; }
	.dynInfoEng { color: #f88a2f; }
	
	h2 { color: #FFFFFF; margin: 15px 0 4px 0; padding: 0; }
	table td { border-collapse: collapse; padding: 0; }
	p { margin: 0; padding: 0 0 0 7px; }
	
	#busbuddy { position: absolute; bottom: 25px; right: 25px; z-index: 100; }
	#map_canvas { position: absolute; z-index: 0; }
	#desc {
		position: absolute;
		bottom: 25px;
		right: 150px;
		width: 300px;
		height: 130px;
		padding: 15px;
		background: rgba(0,0,0,0.9);
		color: #fff;
		z-index: 20;
		border-radius: 5px;
		margin: 0 -20px -20px 0;
		
		-o-transform: scale(0.2, 0.2) translate(550px,100px);
		-o-transition-delay: 0s;
		-o-transition-duration: 0.35s;
		
		-webkit-transform: scale(0.2, 0.2) translate(550px,100px);
		-webkit-transition-delay: 0s;
		-webkit-transition-duration: 0.35s;
		
		-moz-transform: scale(0.2, 0.2) translate(550px,100px);
		-moz-transition-delay: 0s;
		-moz-transition-duration: 0.35s;
		
		transform: scale(0.2, 0.2) translate(550px,100px);
		transition-delay: 0s;
		transition-duration: 0.35s;
		
		
	}
	#desc:hover {
		-o-transform: scale(1, 1) translate(0,0);
		-webkit-transform: scale(1, 1) translate(0,0);
		-moz-transform: scale(1, 1) translate(0,0);
		-webkit-transform: scale(1, 1) translate(0,0);
	}
	#desc h2 { margin: 0; }
	#desc a { color: #AAA; }
	#desc a:hover { text-decoration: none; }
	#desc p { margin: 0 0 10px 0; padding: 0; }
	</style> 
 
	
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script> 
	<script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
	<script type="text/javascript" src="js/timeAndDate.js"></script>
	<script type="text/javascript" src="js/infobox.js"></script>
	<script type="text/javascript" src="js/holdeplass.js"></script>
	<script type="text/javascript">
	var apikey = "82NV49lmavKaljw2";
	//var apiHost = "http://localhost:8080/busbuddy";
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
			busbuddyFetch(apiHost+"/api/1.2/departures/" + holdeplass.getId() + "?apiKey="+apikey+"&callback");
		});
	}
	
	function busbuddyResponse(data) {
		if (data.busStops) {
			for (var i = 0; i < data.busStops.length; i++) {
				holdeplasser[i] = new Holdeplass(data.busStops[i].busStopId, data.busStops[i].name, data.busStops[i].latitude, data.busStops[i].longitude);
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
	</script> 
</head> 
<body onload="initialize()"> 
	<img src="images/bb_100x100.png" alt="" id="busbuddy"> 
	<div id="desc"> 
		<h2>Om BusBuddy for web</h2>
		<p>Busbuddy for web er en webapplikasjon laget av <a href="http://trimn.net/">Tri M. Nguyen</a> som benytter seg av sanntidsdata-API fra <a href="http://api.busbuddy.no:8080/">BusBuddy API</a>.</p>
		<p>Med BusBuddy får du informasjon om bussavgangene i Trondheim ved valgt holdeplass.</p>
	</div> 
	<div id="map_canvas" style="width: 100%; height: 100%;"></div> 
</body>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-23611102-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
</html>