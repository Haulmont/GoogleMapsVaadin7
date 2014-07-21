package com.vaadin.tapio.googlemaps.client.layers;

import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.WeightedLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Igor Korotkov (igor@ikorotkov.com)
 */
public class GoogleMapHeatMapLayer {
    private static final long serialVersionUID = 7426132367355158931L;

    private static long idCounter = 0;

    private long id;

    private List<LatLon> data = new ArrayList<LatLon>();
    private List<WeightedLocation> weightedData = new ArrayList<WeightedLocation>();
    private List<String> gradient;
    private Double opacity = 0.6;
    private Double radius;
    private Double maxIntensity;
    private Boolean dissipating;

    public GoogleMapHeatMapLayer() {
        id = idCounter;
        idCounter++;
    }

    public GoogleMapHeatMapLayer(List<LatLon> data) {
        this();
        this.data = data;
    }

    public GoogleMapHeatMapLayer(List<LatLon> data, List<WeightedLocation> weightedData) {
        this();
        this.data = data;
        this.weightedData = weightedData;
    }

    public List<LatLon> getData() {
        return data;
    }

    public void setData(List<LatLon> data) {
        this.data = data;
    }

    public List<WeightedLocation> getWeightedData() {
        return weightedData;
    }

    public void setWeightedData(List<WeightedLocation> weightedData) {
        this.weightedData = weightedData;
    }

    public List<String> getGradient() {
        return gradient;
    }

    public void setGradient(List<String> gradient) {
        this.gradient = gradient;
    }

    public Double getOpacity() {
        return opacity;
    }

    public void setOpacity(Double opacity) {
        this.opacity = opacity;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Double getMaxIntensity() {
        return maxIntensity;
    }

    public void setMaxIntensity(Double maxIntensity) {
        this.maxIntensity = maxIntensity;
    }

    public Boolean getDissipating() {
        return dissipating;
    }

    public void setDissipating(Boolean dissipating) {
        this.dissipating = dissipating;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GoogleMapHeatMapLayer other = (GoogleMapHeatMapLayer) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }


}
