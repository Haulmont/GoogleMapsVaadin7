package com.vaadin.tapio.googlemaps.client.events.rightclick;

import com.vaadin.tapio.googlemaps.client.base.LatLon;

/**
 * Interface for listeners notified after map right click.
 */
public interface MapRightClickListener {
    /**
     * Called after map right click.
     *
     * @param position position that was right clicked
     */
    void mapRightClicked(LatLon position);
}