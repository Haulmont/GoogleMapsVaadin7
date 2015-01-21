package com.vaadin.tapio.googlemaps.client.rpcs;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.base.LatLon;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public interface MapInitRpc extends ServerRpc {
    public void init(LatLon center, int zoom, LatLon boundsNE, LatLon boundsSW);
}
