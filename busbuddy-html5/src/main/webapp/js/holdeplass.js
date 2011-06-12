function Holdeplass(id, name, lat, lng) {
	this.id = id;
    this.name = name;
	this.lat = lat;
	this.lng = lng;
}
Holdeplass.prototype.getLat = function() {
	return this.lat;
}
Holdeplass.prototype.getLng = function() {
	return this.lng;
}
Holdeplass.prototype.getName = function() {
	return this.name;
}
Holdeplass.prototype.getId = function() {
	return this.id;
}
Holdeplass.prototype.getLatLng = function() {
	return new google.maps.LatLng(this.getLat(), this.getLng());
}