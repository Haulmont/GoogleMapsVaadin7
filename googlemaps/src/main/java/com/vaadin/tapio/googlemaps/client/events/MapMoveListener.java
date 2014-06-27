package com.vaadin.tapio.googlemaps.client.events;

import com.vaadin.tapio.googlemaps.client.LatLon;

import java.io.Serializable;

/**
 * Interface for listening map move and zoom events.
 * 
 * @author Henri Muurimaa
 */
public interface MapMoveListener extends Serializable {
    /**
     * Handle a MapMoveEvent.
     * 
     * @param zoomLevel
     *            The new zoom level.
     * @param center
     *            The new center.
     * @param boundsNE
     *            The position of the north-east corner of the map.
     * @param boundsSW
     *            The position of the south-west corner of the map.
     */
    public void mapMoved(int zoomLevel, LatLon center, LatLon boundsNE,
            LatLon boundsSW);
}
