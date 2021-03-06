package com.vaadin.tapio.googlemaps.client.services;

import java.io.Serializable;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public class Duration implements Serializable {
    private static final long serialVersionUID = 1497719462229185605L;

    private String text;
    private int value;

    public Duration() {
    }

    public Duration(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * @return duration value string representation
     */
    public String getText() {
        return text;
    }

    /**
     * String duration value string representation
     * @param text value
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return duration in seconds
     */
    public int getValue() {
        return value;
    }

    /**
     * Duration in seconds
     * @param value duration
     */
    public void setValue(int value) {
        this.value = value;
    }
}
