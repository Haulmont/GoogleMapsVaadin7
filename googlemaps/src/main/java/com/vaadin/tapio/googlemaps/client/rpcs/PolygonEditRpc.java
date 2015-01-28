package com.vaadin.tapio.googlemaps.client.rpcs;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.base.LatLon;
import com.vaadin.tapio.googlemaps.client.events.PolygonEditListener;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)

 */
public interface PolygonEditRpc extends ServerRpc {
    public void polygonEdited(long polygonId, PolygonEditListener.ActionType
            actionType, int idx, LatLon latLon);
}
