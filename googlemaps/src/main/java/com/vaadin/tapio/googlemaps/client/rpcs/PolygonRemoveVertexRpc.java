package com.vaadin.tapio.googlemaps.client.rpcs;

import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.tapio.googlemaps.client.base.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

public interface PolygonRemoveVertexRpc extends ClientRpc {

    void removeVertex(GoogleMapPolygon polygon, LatLon vertex);
}