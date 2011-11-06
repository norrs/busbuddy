
// geolocation

var find_my_location = function(map) {

	var error = function(msg) {
		// TODO: display error message
	};

	var success = function(position) {
		var latlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
		map.setCenter(latlng);
		map.setZoom(16);

		var marker = new google.maps.Marker({
			position: latlng, 
			map: map, 
			title:"You are here!"
		});
	}

	if (navigator.geolocation) {
	  navigator.geolocation.getCurrentPosition(success, error);
	}
	else {
	  error('geolocation not supported');
	}

};