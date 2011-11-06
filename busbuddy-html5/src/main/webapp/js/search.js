var search_stops = function(stops) {
	$("#result_list ul").html("");

	var counter = 0;
	for (var i = 0; i < stops.length; i++)
	{
		if (stops[i].getName().toLowerCase().indexOf( $('input[name|="orakel"]').val().toLowerCase() ) >= 0 && $('input[name|="orakel"]').val().length > 1)
		{
			$("#result_list ul").append('<li data-direction="'+direction(stops[i].getId())+'" data-listid="'+i+'" data-lat="'+stops[i].getLat()+'" data-lng="'+stops[i].getLng()+'" data-id="'+ stops[i].getId() +'" data-name="'+stops[i].getName()+'">'+stops[i].getName() + ' <span style="color: #AAA; padding:0 0 0 8px;">'+direction(stops[i].getId())+'</span> ' + '</li>');
			counter = 1;
		}
	}

	
	if (counter == 0)
		$("#result_list ul").prepend('<li>Fant ingen holdeplasser. Trykk enter for å spørre orakelet.</li>');

	update_map_size();
};