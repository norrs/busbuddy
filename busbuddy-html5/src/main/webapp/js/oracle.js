var ask_oracle = function(question) {

	if (question.length > 0) {

		$.ajax({
			url: "http://api.trimini.no/oracle.php",
			data: "q=" + question + "&callback=?",
			crossDomain: true,
			dataType: "jsonp",
			timeout: 10000,
			beforeSend: function() {
				$("#orakel_answer").addClass('expanded');
				setTimeout("pre_oracle()", 200);
				update_map_size();
			},
			success: function(data) {
				$("#orakel_answer").html(data[0].answer);
				update_map_size();
			},
			error: function(j, t, e) {
				if (t === "timeout") {
					$("#orakel_answer").html('<span style="color: red;">En feil har oppstått. Orakelet ser ut til å være utilgjengelig.</span>');
				}
				else {
					$("#orakel_answer").html('<span style="color: red;">En feil har oppstått. Orakelet ser ut til å være er utilgjengelig.</span>');
				}
			}
		});

	}

};

var pre_oracle = function() {
	$("#orakel_answer").html("Vent mens jeg spør orakelet...");
};