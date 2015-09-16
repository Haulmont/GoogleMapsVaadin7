package com.vaadin.tapio.googlemaps.client.events.overlaycomplete;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapCircle;

/**
 * @author korotkov
 * @version $Id$
 */
public interface CircleCompleteListener {
    void circleComplete(GoogleMapCircle circle);
}
