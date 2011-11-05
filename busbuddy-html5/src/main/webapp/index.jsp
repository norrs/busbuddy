<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html> 
<html> 
<head> 
	<meta charset=utf-8> 
	<title>BusBuddy</title>
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no"> 
	<meta name="author" content="Tri M. Nguyen <mail@trimn.net>"> 
 	<link rel="stylesheet" type="text/css" href="css/main.css">
	
	<script src="http://maps.google.com/maps/api/js?sensor=false"></script> 
	<script src="js/jquery-1.6.1.min.js"></script>
	<script src="js/timeAndDate.js"></script>
	<script src="js/infobox.js"></script>
	<script src="js/holdeplass.js"></script>
	<script src="js/busbuddy.js"></script> 
	<script src="js/oracle.js"></script> 
	<script src="js/geolocation.js"></script> 

	<script>
	var update_map_size = function() {
		$("#map_canvas").css("height", (window.innerHeight - document.getElementById("map_canvas").offsetTop) + "px");
	};

	$(document).ready(function() {
		update_map_size();

		$('form[name|="orakel_form"]').submit(function() {
			ask_oracle($('input[name|="orakel"]').val());
			return false;
		});
	});

	$(window).resize(function() {
		update_map_size();
	});

	</script>
</head> 
<body onload="initialize()">
	<header>
		<h1>Busbuddy</h1>
		<form method="post" action="" name="orakel_form"><input type="text" name="orakel" placeholder="Spør orakelet"></form>
	</header>
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