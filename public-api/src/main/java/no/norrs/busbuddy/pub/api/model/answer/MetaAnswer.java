package no.norrs.busbuddy.pub.api.model.answer;

import java.util.List;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswer {

    private String start;
    private String destination;
    private int busRoute;
    private List<Integer> times;
    private int duration;

    public MetaAnswer(String start, String destination, int busRoute, List<Integer> times, int duration) {
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

    public int getBusRoute() {
        return busRoute;
    }

    public List<Integer> getTimes() {
        return times;
    }

    public int getDuration() {
        return duration;
    }

    public boolean compareTo(MetaAnswer ma) {
        boolean result = true;

        if (    ma.getBusRoute() == this.busRoute &&
                ma.getStart().equals(this.start) &&
                ma.getDestination().equals(this.destination) &&
                ma.getDuration() == this.duration &&
                ma.getTimes().size() == this.times.size()
                ) {
            for (int time : ma.getTimes()) {
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
