package com.vaadin.tapio.googlemaps.client.rpcs.rightclick;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.base.LatLon;

public interface MapRightClickedRpc extends ServerRpc {

    void mapRightClicked(LatLon position);
}