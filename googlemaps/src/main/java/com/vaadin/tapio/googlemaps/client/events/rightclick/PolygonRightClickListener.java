package com.vaadin.tapio.googlemaps.client.events.rightclick;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

/**
 * Interface for listeners notified after polygon right click.
 */
public interface PolygonRightClickListener {
    /**
     * Called after polygon right click.
     *
     * @param polygon GoogleMapPolygon that was right clicked
     */
    void polygonRightClicked(GoogleMapPolygon polygon);
}