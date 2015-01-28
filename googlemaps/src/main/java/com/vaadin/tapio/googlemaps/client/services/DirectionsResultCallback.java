package com.vaadin.tapio.googlemaps.client.services;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public interface DirectionsResultCallback {
    void onCallback(DirectionsResult result, DirectionsStatus status);
}
