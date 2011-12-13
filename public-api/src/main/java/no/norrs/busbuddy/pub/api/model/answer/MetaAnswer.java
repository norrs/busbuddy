package no.norrs.busbuddy.pub.api.model.answer;

import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswer {

    private String start;
    private String destination;
    private String busRoute;
    private List<DateTime> times;
    private int duration;

    public MetaAnswer(String start, String destination, String busRoute, List<DateTime> times, int duration) {
        this.start = start;
        this.destination = destination;
        this.busRoute = busRoute;
        this.times = times;
        this.duration = duration;
    }

    public String getStart() {
        return start;
    }

    public String getDestination() {
        return destination;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public List<DateTime> getTimes() {
        return times;
    }

    public int getDuration() {
        return duration;
    }

    public boolean compareTo(MetaAnswer ma) {
        boolean result = true;

        if (    ma.getBusRoute().equals(this.busRoute) &&
                ma.getStart().equals(this.start) &&
                ma.getDestination().equals(this.destination) &&
                ma.getDuration() == this.duration &&
                ma.getTimes().size() == this.times.size()
                ) {
            for (DateTime time : ma.getTimes()) {
                if (!times.contains(time)) {
                    result = false;
                }
            }

        }
        else {
            result = false;
        }

        return result;
    }

    @Override
    public String toString() {
        return start+", "+destination+", "+busRoute+", "+times+", "+duration;
    }

}
