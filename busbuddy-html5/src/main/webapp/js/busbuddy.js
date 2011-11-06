// var apikey = "82NV49lmavKaljw2";
var apikey = "L420Jvg7mrXx507T";

var apiHost = "http://api.busbuddy.no:8080";
var holdeplasser = new Array();
var markers = new Array();
var tmpMarker = null;
var map;
var lastClickMarker;
var markerClicked = new Array();


/**
 * TODO: optimalisere søk
 */

var initialize = function()
{
	// center the map over Gløshaugen by default
	var latlng = new google.maps.LatLng(63.4186338, 10.3920824);
	var infoOptions = {
		zoom: 13,
		center: latlng,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById('map_canvas'), infoOptions);
	
	// geolocation.js
	find_my_location(map);

	// get stop list from API
	request_data("busstops");
};

var request_data = function(type, data)
{
	data = (data == undefined) ? "" : "/"+data;

	$.ajax({
		url: "http://api.busbuddy.no:8080/api/1.3/"+type+data,
		data: "apiKey="+apikey+"&callback",
		crossDomain: true,
		dataType: "jsonp",
		jsonp: false,
		jsonpCallback: "busbuddyResponse",
		timeout: 10000,
		success: function (data)
		{
			if (data.departures)
			{
				addMessage(data, markerClicked);
			}
			else if (data.busStops)
			{
				for (var i = 0; i < data.busStops.length; i++)
				{
					holdeplasser[i] = new Holdeplass(data.busStops[i].locationId, data.busStops[i].name, data.busStops[i].latitude, data.busStops[i].longitude);
					addMarker(holdeplasser[i]);
				}
			}
		},
		error: function(j, t, e)
		{
			alert("An error has occured. If you see this message. Tri has probably derped.");
		}
	});
};


var addMarker = function(holdeplass)
{
	var marker = new google.maps.Marker({
		position: holdeplass.getLatLng(),
		map: map,
		title: holdeplass.getName(),
		clickable: true,
		icon: 'images/15x15_4.png',
		title: holdeplass.getName()
	});

	markers.push(marker);

	google.maps.event.addListener(marker, 'click', function() {
		markerClicked[0] = marker;
		markerClicked[1] = holdeplass;
		markerClicked[2] = new InfoBox(render_infobox());

		if (lastClickMarker)
			lastClickMarker.close();
		lastClickMarker = markerClicked[2];

		markerClicked[2].open(map, marker);

		request_data("departures", holdeplass.getId());
	});
};


var search_result_click = function(index)
{
	var marker = new google.maps.Marker({
		position: holdeplasser[index].getLatLng(),
		map: map,
		clickable: false,
		visible: false
	});

	markerClicked[0] = marker
	markerClicked[1] = holdeplasser[index];
	markerClicked[2] = new InfoBox(render_infobox());

	if (tmpMarker != null)
		tmpMarker.setMap(null);
	
	tmpMarker = marker;
	

	if (lastClickMarker)
		lastClickMarker.close();

	lastClickMarker = markerClicked[2];

	markerClicked[2].open(map, tmpMarker);

	request_data("departures", markerClicked[1].getId());
};

var render_infobox = function() {
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

	return infoOptions;
};

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



// quickfix
function direction(direction){
	if(direction == 1 || parseInt(direction/1000) % 2 == 1) 
		return 'Til byen';
	else if(direction == 0 || parseInt(direction/1000) % 2 == 0) 
		return 'Fra byen';
	else
		return 'Ukjent retning';
}


// extracted
$(document).ready(function()
{
	// ========= Initiate
	$("#result_list").hide();
	$("#orakel_answer").hide();
	update_map_size();

	var oracle = new Oracle();

	// keyup commands
	$(document).keyup(function()
	{
		// reset search and dropdown when ESC is triggered
		if (event.which == 27)
		{
			$('input[name|="orakel"]').val("");
			hide_result_list();
			hide_oracle_answer();
			update_map_size();
		}
	});

	$('input[name|="orakel"]').keyup(function(event)
	{
		if (event.which == 13)
		{			
			console.log("test: " + oracle.is_active())
			if (oracle.is_active() == 0)
			{
				oracle.ask_oracle($('input[name|="orakel"]').val());
			}
		}
		else {
			if (!($("#result_list").hasClass("visible")))
				render_result_list();

			search_stops(holdeplasser);
		}

		if ( $('input[name|="orakel"]').val().length <= 0 )
			hide_result_list();

		update_map_size();
	});

	$("#result_list li").live('click', function()
	{
		if ($(this).attr('data-lat') && $(this).attr('data-lng'))
		{
			var latlng = new google.maps.LatLng($(this).attr('data-lat'), $(this).attr('data-lng'));
			map.setCenter(latlng);
			map.setZoom(16);
		}

		hide_result_list();
		hide_oracle_answer();
		update_map_size();

		search_result_click($(this).attr('data-listid'));
	});
	// ========== 

});

var render_result_list = function()
{
	$("#result_list").slideDown(300, function() {
		update_map_size();
	});
};
var hide_result_list = function()
{
	$("#result_list").slideUp(300, function() {
		update_map_size();
	});
};



var hide_oracle_answer = function()
{
	$("#orakel_answer").slideUp(200, function() {
		update_map_size();
	});
};



var show_desc = function()
{
	$("#desc").addClass("visible");
};
var hide_desc = function()
{
	$("#desc").removeClass("visible");
};


// ========== cool default stuff

$(window).resize(function()
{
	update_map_size();
});