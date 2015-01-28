package com.vaadin.tapio.googlemaps.client.services;

import com.vaadin.tapio.googlemaps.client.base.LatLon;
import com.vaadin.tapio.googlemaps.client.base.LatLonBounds;

import java.io.Serializable;
import java.util.List;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public class DirectionsRoute implements Serializable {
    private static final long serialVersionUID = 8115813324830592935L;

    private LatLonBounds bounds;
    private String copyrights;
    private List<DirectionsLeg> legs;
    private List<LatLon> overviewPath;
    private String[] warnings;
    private int[] waypointOrder;

    public DirectionsRoute() {
    }

    public LatLonBounds getBounds() {
        return bounds;
    }

    public void setBounds(LatLonBounds bounds) {
        this.bounds = bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public List<DirectionsLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<DirectionsLeg> legs) {
        this.legs = legs;
    }

    public List<LatLon> getOverviewPath() {
        return overviewPath;
    }

    public void setOverviewPath(List<LatLon> overviewPath) {
        this.overviewPath = overviewPath;
    }

    public String[] getWarnings() {
        return warnings;
    }

    public void setWarnings(String[] warnings) {
        this.warnings = warnings;
    }

    public int[] getWaypointOrder() {
        return waypointOrder;
    }

    public void setWaypointOrder(int[] waypointOrder) {
        this.waypointOrder = waypointOrder;
    }
}
