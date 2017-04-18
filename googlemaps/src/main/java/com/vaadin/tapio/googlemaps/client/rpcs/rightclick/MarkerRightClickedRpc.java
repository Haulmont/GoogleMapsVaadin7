package com.vaadin.tapio.googlemaps.client.rpcs.rightclick;

import com.vaadin.shared.communication.ServerRpc;

public interface MarkerRightClickedRpc extends ServerRpc {

    void markerRightClicked(long markerId);
}