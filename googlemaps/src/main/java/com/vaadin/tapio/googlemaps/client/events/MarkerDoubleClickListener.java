package com.vaadin.tapio.googlemaps.client.events;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

/**
 * @author korotkov
 * @version $Id$
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
