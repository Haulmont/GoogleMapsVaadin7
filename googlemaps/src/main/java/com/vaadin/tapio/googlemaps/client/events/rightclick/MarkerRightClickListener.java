package com.vaadin.tapio.googlemaps.client.events.rightclick;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

/**
 * Interface for listeners notified after marker right click.
 */
public interface MarkerRightClickListener {
    /**
     * Called after marker right click.
     *
     * @param marker GoogleMapMarker that was right clicked
     */
    void markerRightClicked(GoogleMapMarker marker);
}