alert("test");
var test = "stest";

$.ajax({
	url: "http://search.twitter.com/search.json?q=blue%20angels&rpp=5&include_entities=true&with_twitter_user_id=true&result_type=mixed&callback=",

	success: function(data) {
		alert("test");
	}
});