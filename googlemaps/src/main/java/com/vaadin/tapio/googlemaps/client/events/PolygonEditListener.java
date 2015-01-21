package com.vaadin.tapio.googlemaps.client.events;

import com.vaadin.tapio.googlemaps.client.base.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

/**
 * @author korotkov
 * @version $Id$
 */
public interface PolygonEditListener {

    /**
     * Polygon edit action type
     *
     * <p> {@link #INSERT} - vertex have been inserted into polygon
     * <p> {@link #REMOVE} - vertex have been removed from polygon
     * <p> {@link #SET} - vertex coordinates have been changed
     */
    enum ActionType {
        INSERT,
        REMOVE,
        SET
    }

    void polygonEdited(GoogleMapPolygon polygon, ActionType actionType, int idx, LatLon latLon);
}
