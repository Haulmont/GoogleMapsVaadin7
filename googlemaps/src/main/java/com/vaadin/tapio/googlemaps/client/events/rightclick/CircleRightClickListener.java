package com.vaadin.tapio.googlemaps.client.events.rightclick;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapCircle;

/**
 * Interface for listeners notified after circle right click.
 */
public interface CircleRightClickListener {
    /**
     * Called after circle right click.
     *
     * @param circle GoogleMapCircle that was right clicked
     */
    void circleRightClicked(GoogleMapCircle circle);
}