<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html> 
<html> 
<head> 
	<meta charset=utf-8> 
	<title>BusBuddy</title>
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no"> 
	<meta name="author" content="Tri M. Nguyen <mail@trimn.net>"> 
 	<link rel="stylesheet" type="text/css" href="css/main.css">
	<script>
	var update_map_size = function() {
		$("#map_canvas").css("height", (window.innerHeight - document.getElementById("map_canvas").offsetTop) + "px");
	};
	</script>
	<script src="http://maps.google.com/maps/api/js?sensor=false"></script> 
	<script src="js/jquery-1.6.1.min.js"></script>
	<script src="js/timeAndDate.js"></script>
	<script src="js/infobox.js"></script>
	<script src="js/holdeplass.js"></script>
	<script src="js/busbuddy.js"></script> 
	<script src="js/oracle.js"></script> 
	<script src="js/geolocation.js"></script> 

	<script>
	

	$(document).ready(function() {
		update_map_size();

		// ========== 

		$('input[name|="orakel"]').keyup(function(event) {
			

			if (event.which == 13) {
				ask_oracle($('input[name|="orakel"]').val());
			}
			else if (event.which == 27) {
				$('input[name|="orakel"]').val("");
			}
			else {
				
				if (!($("#result_list").hasClass("visible"))) {
					render_result_list();
				}

				search_stops();
			}

			if ( $('input[name|="orakel"]').val().length <= 0 ) {
				hide_result_list();
			}

			update_map_size();
		});

		$("#result_list li").live('click', function() {

			if ($(this).attr('data-lat') && $(this).attr('data-lng')) {
				var latlng = new google.maps.LatLng($(this).attr('data-lat'), $(this).attr('data-lng'));
				map.setCenter(latlng);
				map.setZoom(16);
			}

			search_result_click($(this).attr('data-listid'));
			
		});
		// ========== 

	});

	var render_result_list = function() {
		$("#result_list").addClass("visible");
	};

	var hide_result_list = function() {
		$("#result_list").removeClass("visible");
	};

	$(window).resize(function() {
		update_map_size();
	});

	</script>
</head> 
<body onload="initialize()">
	<header>
		<h1>Busbuddy</h1>
		<input type="text" name="orakel" placeholder="Spør orakelet">
	</header>

	<div id="result_list">
	<h4>Søkeresultater (spør orakelet, eller velg holdeplass fra listen)</h4>
		<ul></ul>
	</div>

	<div id="orakel_answer"></div>
	<img src="images/bb_100x100.png" alt="" id="busbuddy"> 
	<div id="desc"> 
		<h2>Om BusBuddy for web</h2>
		<p>Busbuddy for web er en webapplikasjon laget av <a href="http://trimn.net/">Tri M. Nguyen</a> som benytter seg av sanntidsdata-API fra <a href="http://api.busbuddy.no:8080/">BusBuddy API</a>.</p>
		<p>Med BusBuddy får du informasjon om bussavgangene i Trondheim ved valgt holdeplass.</p>
	</div> 
	<div id="map_canvas"></div> 
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