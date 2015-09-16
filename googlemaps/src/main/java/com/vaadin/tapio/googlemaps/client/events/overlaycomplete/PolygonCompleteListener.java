package com.vaadin.tapio.googlemaps.client.events.overlaycomplete;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)

 */
public interface PolygonCompleteListener {
    void polygonComplete(GoogleMapPolygon polygon);
}
