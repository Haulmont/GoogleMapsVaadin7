package com.vaadin.tapio.googlemaps.client.rpcs.rightclick;

import com.vaadin.shared.communication.ServerRpc;

public interface PolygonRightClickedRpc extends ServerRpc {

    void polygonRightClicked(long polygonId);
}