package com.vaadin.tapio.googlemaps.client.events.click;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapCircle;

/**
 * @author korotkov
 * @version $Id$
 */
public interface CircleClickListener {
    void circleClicked(GoogleMapCircle clickedCircle);
}
