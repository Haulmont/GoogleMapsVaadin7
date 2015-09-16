package com.vaadin.tapio.googlemaps.client.rpcs.doubleclick;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author korotkov
 * @version $Id$
 */
public interface CircleDoubleClickRpc extends ServerRpc {
    void circleDoubleClicked(long circleId);
}
