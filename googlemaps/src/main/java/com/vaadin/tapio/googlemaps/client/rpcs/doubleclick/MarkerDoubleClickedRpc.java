package com.vaadin.tapio.googlemaps.client.rpcs.doubleclick;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)

 */
public interface MarkerDoubleClickedRpc extends ServerRpc {
    public void markerClicked(long markerId);
}
