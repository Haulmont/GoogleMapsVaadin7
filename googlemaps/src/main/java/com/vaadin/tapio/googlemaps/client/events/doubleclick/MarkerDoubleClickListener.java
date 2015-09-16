package com.vaadin.tapio.googlemaps.client.events.doubleclick;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)

 */
public interface MarkerDoubleClickListener {
    /**
     * Handle a MarkerDoubleClickEvent.
     *
     * @param clickedMarker
     *            The marker that was clicked.
     */
    public void markerDoubleClicked(GoogleMapMarker clickedMarker);
}
