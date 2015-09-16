package com.vaadin.tapio.googlemaps.client.events.centerchange;

import com.vaadin.tapio.googlemaps.client.base.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapCircle;

/**
 * @author korotkov
 * @version $Id$
 */
public interface CircleCenterChangeListener {
    void centerChanged(GoogleMapCircle circle, LatLon oldCenter);
}
