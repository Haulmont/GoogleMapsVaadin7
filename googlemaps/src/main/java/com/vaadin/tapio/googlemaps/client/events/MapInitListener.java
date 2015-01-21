package com.vaadin.tapio.googlemaps.client.events;

import com.vaadin.tapio.googlemaps.client.base.LatLon;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public interface MapInitListener {
    public void init(LatLon center, int zoom, LatLon boundsNE, LatLon boundsSW);
}
