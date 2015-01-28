package com.vaadin.tapio.googlemaps.client.services;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public enum TravelMode {
    /**
     * Specifies a bicycling directions request.
     */
    BICYCLING,

    /**
     * Specifies a driving directions request.
     */
    DRIVING,

    /**
     * Specifies a walking directions request.
     */
    WALKING;

    public String value() {
        return name();
    }

    public static TravelMode fromValue(String type) {
        return valueOf(type.toUpperCase());
    }

    public String toString() {
        return name();
    }

}
