
// oracle

var Oracle = function() {
	this.state = 0;
}

Oracle.prototype.ask_oracle = function(question) {
	var that = this;
	if (question.length > 0) {

		$.ajax({
			url: "http://api.trimini.no/oracle.php",
			data: "q="+question.replace(/\?/g, '')+"&callback=?",
			crossDomain: true,
			dataType: "jsonp",
			timeout: 10000,
			beforeSend: function() {
				$("#orakel_answer").slideDown(200, function() {
					$("#orakel_answer").html("Vent mens jeg spør orakelet...");
					update_map_size();
				});
				console.log(that.state)
				that.state = 1;
			},
			success: function(data) {
				$("#orakel_answer").html(data[0].answer);
				update_map_size();
			},
			error: function(j, t, e) {
				if (t === "timeout") {
					$("#orakel_answer").html('<span style="color: red;">En feil har oppstått. Orakelet ser ut til å være utilgjengelig.</span>');
					console.log(t);
				}
				else {
					$("#orakel_answer").html('<span style="color: red;">En feil har oppstått. Orakelet ser ut til å være er utilgjengelig.</span>');
					console.log(t);
				}
			},
			complete: function() {
				that.state = 0;
			}
		});

	}

};

Oracle.prototype.is_active = function() {
	return parseInt(this.state);
};