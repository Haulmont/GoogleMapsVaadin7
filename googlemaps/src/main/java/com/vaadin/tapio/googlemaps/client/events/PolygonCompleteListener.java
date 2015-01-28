package com.vaadin.tapio.googlemaps.client.events;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)

 */
public interface PolygonCompleteListener {
    void polygonComplete(GoogleMapPolygon polygon);
}
