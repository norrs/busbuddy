function timeDiff(stTime) {
	var time = stTime;
	time = time.replace("T", " ");
	time = time.replace(".000", "");
	time = time.replace(/-/g, "/");
	time = new Date(time);
	var today = new Date();

	if (today.getDay() == time.getDay()) {
		var minutes = (time.getHours() + (time.getMinutes()/60)) - (today.getHours() + (today.getMinutes()/60));
		if (minutes <= 1) return Math.floor(minutes*60) + " min";
	}
	return time.getHours() + ":" + (time.getMinutes() < 10 ? 0 : '') + time.getMinutes();
}