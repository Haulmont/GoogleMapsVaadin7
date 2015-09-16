package com.vaadin.tapio.googlemaps.client.rpcs.overlaycomplete;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapCircle;

/**
 * @author korotkov
 * @version $Id$
 */
public interface CircleCompleteRpc extends ServerRpc {
    void circleComplete(GoogleMapCircle circle);
}
