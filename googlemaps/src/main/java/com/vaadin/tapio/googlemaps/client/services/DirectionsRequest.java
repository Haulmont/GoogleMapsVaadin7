package com.vaadin.tapio.googlemaps.client.services;

import com.vaadin.tapio.googlemaps.client.base.LatLon;

import java.io.Serializable;
import java.util.List;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public class DirectionsRequest implements Serializable {
    private static final long serialVersionUID = -4351120323324218168L;

    private static long idCounter = 0;

    private long id;
    private List<DirectionsWaypoint> waypoints;
    private boolean avoidHighways;
    private boolean avoidTolls;
    private boolean optimizeWaypoints;
    private boolean provideRouteAlternatives;
    private LatLon origin;
    private LatLon destination;
    private String region;
    private TravelMode travelMode;
    private UnitSystem unitSystem;

    public DirectionsRequest() {
        id = idCounter;
        idCounter++;
    }

    public DirectionsRequest(LatLon origin, LatLon destination, TravelMode travelMode) {
        this();
        this.origin = origin;
        this.destination = destination;
        this.travelMode = travelMode;
    }

    public List<DirectionsWaypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<DirectionsWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public boolean isAvoidHighways() {
        return avoidHighways;
    }

    public void setAvoidHighways(boolean avoidHighways) {
        this.avoidHighways = avoidHighways;
    }

    public boolean isAvoidTolls() {
        return avoidTolls;
    }

    public void setAvoidTolls(boolean avoidTolls) {
        this.avoidTolls = avoidTolls;
    }

    public boolean isOptimizeWaypoints() {
        return optimizeWaypoints;
    }

    public void setOptimizeWaypoints(boolean optimizeWaypoints) {
        this.optimizeWaypoints = optimizeWaypoints;
    }

    public boolean isProvideRouteAlternatives() {
        return provideRouteAlternatives;
    }

    public void setProvideRouteAlternatives(boolean provideRouteAlternatives) {
        this.provideRouteAlternatives = provideRouteAlternatives;
    }

    public LatLon getOrigin() {
        return origin;
    }

    public void setOrigin(LatLon origin) {
        this.origin = origin;
    }

    public LatLon getDestination() {
        return destination;
    }

    public void setDestination(LatLon destination) {
        this.destination = destination;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public TravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    public UnitSystem getUnitSystem() {
        return unitSystem;
    }

    public void setUnitSystem(UnitSystem unitSystem) {
        this.unitSystem = unitSystem;
    }

    public long getId() {
        return id;
    }
}
