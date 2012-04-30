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
    
    private boolean isInaccurate = false;
    private String detailedDuration = "";

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
    
    public void setInaccurate(String details) {
        this.isInaccurate = true;
        this.detailedDuration = details;
        
    }

    /**
     * Whether the duration from the oracle answer was not a whole number of minutes, but an interval.
     *
     * @return boolean for accuracy of duration
     */
    public boolean isInaccurate() {
        return isInaccurate;
    }

    /**
     * Returns the details duration from the oracle answer.
     * getDuration() will return the average, this will return the actual text from the answer.
     *
     * @return String containing duration in "x-y" format, where x and y are minutes
     */
    public String getDetailedDuration() {
        return detailedDuration;
    }

    public boolean compareTo(MetaAnswer ma) {
        boolean result = true;

        if (    ma.getBusRoute().equals(this.busRoute) &&
                ma.getStart().equals(this.start) &&
                ma.getDestination().equals(this.destination) &&
                ma.getDuration() == this.duration &&
                ma.getTimes().size() == this.times.size() &&
                ma.isInaccurate() == this.isInaccurate &&
                ma.getDetailedDuration().equals(this.detailedDuration)
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
        if (isInaccurate) {
            return start+", "+destination+", "+busRoute+", "+times+", "+duration+", "+detailedDuration;
        }
        else {
            return start+", "+destination+", "+busRoute+", "+times+", "+duration;
        }
    }

}
