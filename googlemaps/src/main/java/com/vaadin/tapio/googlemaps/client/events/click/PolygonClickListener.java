package com.vaadin.tapio.googlemaps.client.events.click;

import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

import java.io.Serializable;

/**
 * @author korotkov
 * @version $Id$
 */
public interface PolygonClickListener extends Serializable {
    public void polygonClicked(GoogleMapPolygon polygon);
}
