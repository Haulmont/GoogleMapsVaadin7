package com.vaadin.tapio.googlemaps.client.rpcs;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author korotkov
 * @version $Id$
 */
public interface MarkerDoubleClickedRpc extends ServerRpc {
    public void markerClicked(long markerId);
}
