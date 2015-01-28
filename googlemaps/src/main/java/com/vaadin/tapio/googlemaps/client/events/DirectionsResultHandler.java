package com.vaadin.tapio.googlemaps.client.events;

import com.vaadin.tapio.googlemaps.client.services.DirectionsResult;
import com.vaadin.tapio.googlemaps.client.services.DirectionsStatus;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public interface DirectionsResultHandler {
    void handle(long requestId, DirectionsResult result, DirectionsStatus status);
}
