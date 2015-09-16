package com.vaadin.tapio.googlemaps.client.rpcs.radiuschange;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author korotkov
 * @version $Id$
 */
public interface CircleRadiusChangeRpc extends ServerRpc {
    void radiusChanged(long circleId, double newRadius);
}
