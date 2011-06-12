
     //rendered from jsp.
    var daysStat = new Array();
    var daysStatEachApp = new Array();
    var appDetails = new Array();



    function AppEntry(name, data) {
        this.name = name;
        this.data = data;
    }



    function init() {

            loadLast30DaysStats(last30DaysStats);
        	loadLast30DaysStatsForEachApp(last30DaysStatsForEachApp);
    		loadLast30DaysStatsForApp(last30DaysStatsForAppDetails);
	}

    function busbuddyFetch(url) {
		var script = document.createElement("script");
		script.setAttribute("src", url);
		script.setAttribute("type", "text/javascript");
		document.body.appendChild(script);
	}

    function loadLast30DaysStats(data) {
        $.each(data, function(itemNo, hit) {
            daysStat.push(new Array(hit.timeStamp,hit.hitsData))
        })
        drawLast30DaysStats();
    }

    function loadLast30DaysStatsForEachApp(data) {
        $.each(data, function(appName, value) {
            var dataSet = new Array()
            $.each(value, function(key, data) { dataSet.push(new Array(data.timeStamp, data.hitsData)); })


            objectSet = {}
            objectSet["name"] = appName
            objectSet["data"] = dataSet


            daysStatEachApp.push(objectSet);



        });
        drawLast30DaysStatsForEachApp();
    }

    function loadLast30DaysStatsForApp(data) {


        $.each(data, function(resultCode, value) {
            var dataSet = new Array();
            $.each(value, function(key, data) { dataSet.push(new Array(data.timeStamp, data.hitsData)); })


            objectSet = {}
            objectSet["name"] = resultCode
            objectSet["data"] = dataSet

            appDetails.push(objectSet);

        });
        console.debug(appDetails)

        drawAppDetails();
    }

    function drawLast30DaysStats() {
        chart = new Highcharts.Chart({
            credits: { enabled : true},
            chart: {
                renderTo: 'container',
                defaultSeriesType: 'area',
            },
            title: {
                text: 'Totalt requests of all applications the last 30 days',
                x: -20 //center
            },
            subtitle: {
                text: 'stats api.busbuddy.norrs.no',
                x: -20
            },
            xAxis:{
                type: 'datetime',
            },
            yAxis: {
                title: {
                    text: 'Requests'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b> - '+ this.y + ' requests<br/>'+
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x)
                }
            },

            series: [{
                name: "All requests",
                data: daysStat
            }]

        });
    }

    function drawLast30DaysStatsForEachApp() {
        chart = new Highcharts.Chart({
            credits: { enabled : true},
            chart: {
                renderTo: 'applicationsLast30Days',
                defaultSeriesType: 'line',
            },
            title: {
                text: 'Requests the last 30 days by applications',
                x: -20 //center
            },
            subtitle: {
                text: 'stats api.busbuddy.norrs.no',
                x: -20
            },
            xAxis:{
                type: 'datetime',
            },
            yAxis: {
                title: {
                    text: 'Requests'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b> - '+ this.y + ' requests<br/>'+
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x)
                }
            },

            series: daysStatEachApp


        });
    }

      function drawAppDetails() {
        chart = new Highcharts.Chart({
            credits: { enabled : true},
            chart: {
                renderTo: 'applicationDetailsLast30Days',
                defaultSeriesType: 'line',
            },
            title: {
                text: 'Requests the last 30 days for '+appDetailsName,
                x: -20 //center
            },
            subtitle: {
                text: 'stats api.busbuddy.norrs.no',
                x: -20
            },
            xAxis:{
                type: 'datetime',
            },
            yAxis: {
                title: {
                    text: 'Requests'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>Response code: '+ this.series.name +'</b> - '+ this.y + ' requests<br/>'+
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x)
                }
            },

            series: appDetails


        });
    }
