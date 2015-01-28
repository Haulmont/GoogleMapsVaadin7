package com.vaadin.tapio.googlemaps.client.services;

import java.io.Serializable;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public class Distance implements Serializable {
    private static final long serialVersionUID = 8516378440142447051L;

    private String text;
    private int value;

    public Distance() {
    }

    public Distance(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * A string representation of the distance value, using the UnitSystem specified in the request.
     */
    public String getText() {
        return text;
    }

    /**
     * A string representation of the distance value, using the UnitSystem specified in the request.
     *
     * @param text text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * The distance in meters.
     */
    public int getValue() {
        return value;
    }

    /**
     * The distance in meters.
     *
     * @param value value
     */
    public void setValue(int value) {
        this.value = value;
    }
}
