package com.vaadin.tapio.googlemaps.client;

import com.vaadin.shared.ui.AbstractComponentContainerState;
import com.vaadin.tapio.googlemaps.client.base.LatLon;
import com.vaadin.tapio.googlemaps.client.drawing.DrawingOptions;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapHeatMapLayer;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapKmlLayer;
import com.vaadin.tapio.googlemaps.client.overlays.*;
import com.vaadin.tapio.googlemaps.client.services.DirectionsRequest;

import java.util.*;

/**
 * The shared state of the Google Maps. Contains also the default values.
 */
public class GoogleMapState extends AbstractComponentContainerState {
    private static final long serialVersionUID = 646346522643L;

    public String apiKey = null;
    public String clientId = null;

    // defaults to the language setting of the browser
    public String language = null;
    public String mapTypeId = "Roadmap";
    public LatLon center = new LatLon(51.477811, -0.001475);
    public LatLon boundNE = null;
    public LatLon boundSW = null;
    public int zoom = 8;
    public int maxZoom = 21;
    public int minZoom = 0;

    public boolean draggable = true;
    public boolean keyboardShortcutsEnabled = true;
    public boolean scrollWheelEnabled = true;

    public boolean supportDrawing = false;

    public DrawingOptions drawingOptions = null;

    public Set<GoogleMapControl> controls = new HashSet<GoogleMapControl>(
            Arrays.asList(GoogleMapControl.MapType, GoogleMapControl.Pan,
                    GoogleMapControl.Rotate, GoogleMapControl.Scale,
                    GoogleMapControl.StreetView, GoogleMapControl.Zoom));

    public boolean limitCenterBounds = false;
    public LatLon centerSWLimit = new LatLon(0.0, 0.0);
    public LatLon centerNELimit = new LatLon(0.0, 0.0);

    public boolean limitVisibleAreaBounds = false;
    public LatLon visibleAreaSWLimit = new LatLon(0.0, 0.0);
    public LatLon visibleAreaNELimit = new LatLon(0.0, 0.0);

    public LatLon fitToBoundsNE = null;
    public LatLon fitToBoundsSW = null;

    public Set<GoogleMapPolyline> polylines = new HashSet<GoogleMapPolyline>();
    public Set<GoogleMapKmlLayer> kmlLayers = new HashSet<GoogleMapKmlLayer>();
    public Set<GoogleMapHeatMapLayer> heatMapLayers = new HashSet<GoogleMapHeatMapLayer>();

    public Map<Long, DirectionsRequest> directionsRequests = new HashMap<Long, DirectionsRequest>();

    public Map<Long, GoogleMapMarker> markers = new HashMap<Long, GoogleMapMarker>();

    public Map<Long, GoogleMapInfoWindow> infoWindows = new HashMap<Long, GoogleMapInfoWindow>();
    public boolean trafficLayerVisible = false;

    public Map<Long, GoogleMapPolygon> polygons = new HashMap<Long, GoogleMapPolygon>();

    public Map<Long, GoogleMapCircle> circles = new HashMap<Long, GoogleMapCircle>();

    public String apiUrl = null;

    public Map<Long, String> infoWindowContentIdentifiers = new HashMap<>();
}