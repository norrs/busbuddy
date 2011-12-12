package no.norrs.busbuddy.pub.api.model;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswer {

    private String start;
    private String destination;
    private int busRoute;

    public MetaAnswer(String start, String destination, int busRoute) {
        this.start = start;
        this.destination = destination;
        this.busRoute = busRoute;
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

}
