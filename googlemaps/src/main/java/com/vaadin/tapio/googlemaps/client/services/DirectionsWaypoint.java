package com.vaadin.tapio.googlemaps.client.services;

import com.vaadin.tapio.googlemaps.client.base.LatLon;

import java.io.Serializable;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public class DirectionsWaypoint implements Serializable {
    private static final long serialVersionUID = -2981796552431286404L;

    private LatLon location;
    private boolean stopOver = false;

    public DirectionsWaypoint(LatLon location, boolean stopOver) {
        this.location = location;
        this.stopOver = stopOver;
    }

    public DirectionsWaypoint() {
    }

    public boolean isStopOver() {
        return stopOver;
    }

    public void setStopOver(boolean stopOver) {
        this.stopOver = stopOver;
    }

    public LatLon getLocation() {
        return location;
    }

    public void setLocation(LatLon location) {
        this.location = location;
    }
}
