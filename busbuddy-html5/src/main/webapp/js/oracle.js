var ask_oracle = function(question) {

	if (question.length > 0) {

		$.ajax({
			url: "http://api.trimini.no/oracle.php",
			data: "q=" + question + "&callback=?",
			crossDomain: true,
			dataType: "jsonp",
			beforeSend: function() {
				$("#orakel_answer").addClass('expanded');
				setTimeout("test()", 200);
				update_map_size();
			},
			success: function(data) {
				$("#orakel_answer").html(data[0].answer);
				update_map_size();
			}
		});

	}

};

var test = function() {
	$("#orakel_answer").html("loading");
};