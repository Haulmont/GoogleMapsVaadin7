package com.vaadin.tapio.googlemaps.client.rpcs;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

/**
 * @author korotkov
 * @version $Id$
 */
public interface PolygonCompleteRpc extends ServerRpc {
    void polygonComplete(GoogleMapPolygon polygon);
}
