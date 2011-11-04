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
	<script src="js/busbuddy.js"></script> 
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