package com.vaadin.tapio.googlemaps.client.events;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

/**
 * @author korotkov
 * @version $Id$
 */
public interface PolygonCompleteListener {
    void polygonComplete(GoogleMapPolygon polygon);
}
