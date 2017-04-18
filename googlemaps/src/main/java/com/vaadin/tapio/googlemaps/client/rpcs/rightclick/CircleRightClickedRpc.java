package com.vaadin.tapio.googlemaps.client.rpcs.rightclick;

import com.vaadin.shared.communication.ServerRpc;

public interface CircleRightClickedRpc extends ServerRpc {

    void circleRightClicked(long circleId);
}