package com.vaadin.tapio.googlemaps.client.rpcs.overlaycomplete;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)

 */
public interface PolygonCompleteRpc extends ServerRpc {
    void polygonComplete(GoogleMapPolygon polygon);
}
