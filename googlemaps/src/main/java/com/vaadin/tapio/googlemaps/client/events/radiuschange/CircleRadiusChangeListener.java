package com.vaadin.tapio.googlemaps.client.events.radiuschange;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapCircle;

/**
 * @author korotkov
 * @version $Id$
 */
public interface CircleRadiusChangeListener {
    void radiusChange(GoogleMapCircle circle, double oldRadius);
}
