package com.vaadin.tapio.googlemaps.client.rpcs;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.services.DirectionsResult;
import com.vaadin.tapio.googlemaps.client.services.DirectionsStatus;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public interface HandleDirectionsResultRpc extends ServerRpc {
    void handle(DirectionsResult result, DirectionsStatus status, long directionsRequestId);
}
