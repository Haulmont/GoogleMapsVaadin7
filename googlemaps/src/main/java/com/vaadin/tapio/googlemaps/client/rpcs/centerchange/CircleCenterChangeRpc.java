package com.vaadin.tapio.googlemaps.client.rpcs.centerchange;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.base.LatLon;

/**
 * @author korotkov
 * @version $Id$
 */
public interface CircleCenterChangeRpc extends ServerRpc {
    void centerChanged(long circleId, LatLon newCenter);
}
