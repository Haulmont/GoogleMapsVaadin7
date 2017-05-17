package com.vaadin.tapio.googlemaps;

import com.vaadin.tapio.googlemaps.client.GoogleMapControl;
import com.vaadin.tapio.googlemaps.client.GoogleMapState;
import com.vaadin.tapio.googlemaps.client.base.LatLon;
import com.vaadin.tapio.googlemaps.client.drawing.DrawingOptions;
import com.vaadin.tapio.googlemaps.client.events.*;
import com.vaadin.tapio.googlemaps.client.events.centerchange.CircleCenterChangeListener;
import com.vaadin.tapio.googlemaps.client.events.click.CircleClickListener;
import com.vaadin.tapio.googlemaps.client.events.click.MapClickListener;
import com.vaadin.tapio.googlemaps.client.events.click.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.events.click.PolygonClickListener;
import com.vaadin.tapio.googlemaps.client.events.doubleclick.CircleDoubleClickListener;
import com.vaadin.tapio.googlemaps.client.events.doubleclick.MarkerDoubleClickListener;
import com.vaadin.tapio.googlemaps.client.events.overlaycomplete.CircleCompleteListener;
import com.vaadin.tapio.googlemaps.client.events.overlaycomplete.PolygonCompleteListener;
import com.vaadin.tapio.googlemaps.client.events.radiuschange.CircleRadiusChangeListener;
import com.vaadin.tapio.googlemaps.client.events.rightclick.CircleRightClickListener;
import com.vaadin.tapio.googlemaps.client.events.rightclick.MapRightClickListener;
import com.vaadin.tapio.googlemaps.client.events.rightclick.MarkerRightClickListener;
import com.vaadin.tapio.googlemaps.client.events.rightclick.PolygonRightClickListener;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapHeatMapLayer;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapKmlLayer;
import com.vaadin.tapio.googlemaps.client.maptypes.GoogleImageMapType;
import com.vaadin.tapio.googlemaps.client.overlays.*;
import com.vaadin.tapio.googlemaps.client.rpcs.*;
import com.vaadin.tapio.googlemaps.client.rpcs.centerchange.CircleCenterChangeRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.click.CircleClickedRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.click.MapClickedRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.click.MarkerClickedRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.click.PolygonClickedRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.doubleclick.CircleDoubleClickRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.doubleclick.MarkerDoubleClickedRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.overlaycomplete.CircleCompleteRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.overlaycomplete.PolygonCompleteRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.radiuschange.CircleRadiusChangeRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.rightclick.CircleRightClickedRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.rightclick.MapRightClickedRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.rightclick.MarkerRightClickedRpc;
import com.vaadin.tapio.googlemaps.client.rpcs.rightclick.PolygonRightClickedRpc;
import com.vaadin.tapio.googlemaps.client.services.DirectionsRequest;
import com.vaadin.tapio.googlemaps.client.services.DirectionsResult;
import com.vaadin.tapio.googlemaps.client.services.DirectionsResultCallback;
import com.vaadin.tapio.googlemaps.client.services.DirectionsStatus;

import java.util.*;

/**
 * The class representing Google Maps.
 *
 * @author Tapio Aali <tapio@vaadin.com>
 */
public class GoogleMap extends com.vaadin.ui.AbstractComponent {

    private static final byte MIN_VERTEX_COUNT = 3;

    private static final long serialVersionUID = -2869498659894907433L;

    /**
     * Base map types supported by Google Maps.
     */
    public enum MapType {
        Hybrid, Roadmap, Satellite, Terrain
    }

    private Map<Long, DirectionsResultCallback> directionsCallbacks = new HashMap<Long, DirectionsResultCallback>();

    protected GoogleMapMarker getMarker(long markerId) {
        return getState(false).markers.get(markerId);
    }

    private MarkerClickedRpc markerClickedRpc = new MarkerClickedRpc() {
        private static final long serialVersionUID = -1895207589346639292L;

        @Override
        public void markerClicked(long markerId) {
            GoogleMapMarker marker = getMarker(markerId);
            for (MarkerClickListener listener : markerClickListeners) {
                listener.markerClicked(marker);
            }
        }
    };

    private MarkerDoubleClickedRpc markerDoubleClickedRpc = new MarkerDoubleClickedRpc() {
        private static final long serialVersionUID = 72001405321104167L;

        @Override
        public void markerClicked(long markerId) {
            GoogleMapMarker marker = getMarker(markerId);
            for (MarkerDoubleClickListener listener : markerDoubleClickListeners) {
                listener.markerDoubleClicked(marker);
            }
        }
    };

    private MarkerRightClickedRpc markerRightClickedRpc = new MarkerRightClickedRpc() {
        @Override
        public void markerRightClicked(long markerId) {
            GoogleMapMarker marker = getMarker(markerId);
            for (MarkerRightClickListener listener : markerRightClickListeners) {
                listener.markerRightClicked(marker);
            }
        }
    };

    private MarkerDraggedRpc markerDraggedRpc = new MarkerDraggedRpc() {
        private static final long serialVersionUID = 7427899436428646969L;

        @Override
        public void markerDragged(long markerId, LatLon newPosition) {
            GoogleMapMarker marker = getMarker(markerId);
            LatLon oldPosition = marker.getPosition();
            marker.setPosition(newPosition);
            for (MarkerDragListener listener : markerDragListeners) {
                listener.markerDragged(marker, oldPosition);
            }
        }
    };

    private MapMovedRpc mapMovedRpc = new MapMovedRpc() {
        private static final long serialVersionUID = -8853831335700786314L;

        @Override
        public void mapMoved(int zoomLevel, LatLon center, LatLon boundsNE,
                             LatLon boundsSW) {
            getState().locationFromClient = true;
            getState().zoom = zoomLevel;
            getState().center = center;
            getState().boundNE = boundsNE;
            getState().boundSW = boundsSW;
            fitToBounds(null, null);

            for (MapMoveListener listener : mapMoveListeners) {
                listener.mapMoved(zoomLevel, center, boundsNE, boundsSW);
            }
        }
    };

    private MapClickedRpc mapClickedRpc = new MapClickedRpc() {
        private static final long serialVersionUID = -3074239582333387650L;

        @Override
        public void mapClicked(LatLon position) {
            for (MapClickListener listener : mapClickListeners) {
                listener.mapClicked(position);
            }
        }
    };

    private MapRightClickedRpc mapRightClickedRpc = new MapRightClickedRpc() {
        @Override
        public void mapRightClicked(LatLon position) {
            for (MapRightClickListener listener : mapRightClickListeners) {
                listener.mapRightClicked(position);
            }
        }
    };

    private MapInitRpc mapInitRpc = new MapInitRpc() {

        private static final long serialVersionUID = 9112208038019675738L;

        @Override
        public void init(LatLon center, int zoom, LatLon boundsNE, LatLon boundsSW) {
            getState().boundNE = boundsNE;
            getState().boundSW = boundsSW;
            if (initListener != null) {
                initListener.init(center, zoom, boundsNE, boundsSW);
            }
        }
    };

    private InfoWindowClosedRpc infoWindowClosedRpc = new InfoWindowClosedRpc() {

        private static final long serialVersionUID = -7969087900137607456L;

        @Override
        public void infoWindowClosed(long windowId) {
            GoogleMapInfoWindow window = getState(false).infoWindows.get(windowId);
            for (InfoWindowClosedListener listener : infoWindowClosedListeners) {
                listener.infoWindowClosed(window);
            }
            getState().infoWindows.remove(windowId);
        }
    };

    protected GoogleMapPolygon getPolygon(long polygonId) {
        return getState(false).polygons.get(polygonId);
    }

    private PolygonCompleteRpc polygonCompleteRpc = new PolygonCompleteRpc() {
        private static final long serialVersionUID = 8989540297240790126L;

        @Override
        public void polygonComplete(GoogleMapPolygon polygon) {
            if (polygon == null) {
                return;
            }
            getState().polygons.put(polygon.getId(), polygon);
            for (PolygonCompleteListener listener : polygonCompleteListeners) {
                listener.polygonComplete(polygon);
            }
        }
    };

    private PolygonEditRpc polygonEditRpc = new PolygonEditRpc() {
        private static final long serialVersionUID = -8138362526979836605L;

        @Override
        public void polygonEdited(long polygonId, PolygonEditListener.ActionType actionType, int idx, LatLon latLon) {
            if (actionType == null || latLon == null) {
                return;
            }
            GoogleMapPolygon polygon = getPolygon(polygonId);
            if (polygon == null) {
                return;
            }

            switch (actionType) {
                case INSERT:
                    polygon.getCoordinates().add(idx, latLon);
                    break;
                case REMOVE:
                    polygon.getCoordinates().remove(idx);
                    break;
                case SET:
                    LatLon existing = polygon.getCoordinates().get(idx);
                    existing.setLat(latLon.getLat());
                    existing.setLon(latLon.getLon());
                    latLon = existing;
                    break;
            }
            for (PolygonEditListener listener : polygonEditListeners) {
                listener.polygonEdited(polygon, actionType, idx, latLon);
            }
        }
    };

    private PolygonClickedRpc polygonClickedRpc = new PolygonClickedRpc() {
        private static final long serialVersionUID = -8630070910806102818L;

        @Override
        public void polygonClicked(long polygonId) {
            GoogleMapPolygon polygon = getPolygon(polygonId);
            for (PolygonClickListener listener : polygonClickListeners) {
                listener.polygonClicked(polygon);
            }
        }
    };

    private PolygonRightClickedRpc polygonRightClickedRpc = new PolygonRightClickedRpc() {
        @Override
        public void polygonRightClicked(long polygonId) {
            GoogleMapPolygon polygon = getPolygon(polygonId);
            for (PolygonRightClickListener listener : polygonRightClickListeners) {
                listener.polygonRightClicked(polygon);
            }
        }
    };

    private HandleDirectionsResultRpc handleDirectionsResultRpc = new HandleDirectionsResultRpc() {
        private static final long serialVersionUID = 2075879581561166850L;

        @Override
        public void handle(DirectionsResult result, DirectionsStatus status, long directionsRequestId) {
            DirectionsResultCallback handler = directionsCallbacks.get(directionsRequestId);
            if (handler != null) {
                try {
                    handler.onCallback(result, status);
                } finally {
                    getState().directionsRequests.remove(directionsRequestId);
                    directionsCallbacks.remove(directionsRequestId);
                }
            }
        }
    };

    protected GoogleMapCircle getCircle(long circleId) {
        return getState(false).circles.get(circleId);
    }

    private CircleCompleteRpc circleCompleteRpc = new CircleCompleteRpc() {
        private static final long serialVersionUID = 8989540297240790126L;

        @Override
        public void circleComplete(GoogleMapCircle circle) {
            if (circle == null) {
                return;
            }
            getState().circles.put(circle.getId(), circle);
            for (CircleCompleteListener listener : circleCompleteListeners) {
                listener.circleComplete(circle);
            }
        }
    };

    private CircleClickedRpc circleClickedRpc = new CircleClickedRpc() {

        private static final long serialVersionUID = -147202438775817921L;

        @Override
        public void circleClicked(long circleId) {
            GoogleMapCircle circle = getCircle(circleId);
            for (CircleClickListener listener : circleClickListeners) {
                listener.circleClicked(circle);
            }
        }
    };

    private CircleDoubleClickRpc circleDoubleClickRpc = new CircleDoubleClickRpc() {

        private static final long serialVersionUID = 3257369147581938217L;

        @Override
        public void circleDoubleClicked(long circleId) {
            GoogleMapCircle circle = getCircle(circleId);
            for (CircleDoubleClickListener listener : circleDoubleClickListeners) {
                listener.circleDoubleClicked(circle);
            }
        }
    };

    private CircleRightClickedRpc circleRightClickedRpc = new CircleRightClickedRpc() {
        @Override
        public void circleRightClicked(long circleId) {
            GoogleMapCircle circle = getCircle(circleId);
            for (CircleRightClickListener listener : circleRightClickListeners) {
                listener.circleRightClicked(circle);
            }
        }
    };

    private CircleCenterChangeRpc circleCenterChangeRpc = new CircleCenterChangeRpc() {

        private static final long serialVersionUID = 5703076552698659247L;

        @Override
        public void centerChanged(long circleId, LatLon newCenter) {
            GoogleMapCircle circle = getCircle(circleId);
            LatLon oldCenter = circle.getCenter();
            circle.setCenter(newCenter);
            for (CircleCenterChangeListener listener : circleCenterChangeListeners) {
                listener.centerChanged(circle, oldCenter);
            }
        }
    };

    private CircleRadiusChangeRpc circleRadiusChangeRpc = new CircleRadiusChangeRpc() {

        private static final long serialVersionUID = 4898056413558408843L;

        @Override
        public void radiusChanged(long circleId, double newRadius) {
            GoogleMapCircle circle = getCircle(circleId);
            double oldRadius = circle.getRadius();
            circle.setRadius(newRadius);
            for (CircleRadiusChangeListener listener : circleRadiusChangeListeners) {
                listener.radiusChange(circle, oldRadius);
            }
        }
    };

    private List<MarkerClickListener> markerClickListeners = new ArrayList<MarkerClickListener>();
    private List<MarkerRightClickListener> markerRightClickListeners = new ArrayList<MarkerRightClickListener>();
    private List<MarkerDoubleClickListener> markerDoubleClickListeners = new ArrayList<MarkerDoubleClickListener>();
    private List<MarkerDragListener> markerDragListeners = new ArrayList<MarkerDragListener>();

    private List<MapMoveListener> mapMoveListeners = new ArrayList<MapMoveListener>();
    private List<MapClickListener> mapClickListeners = new ArrayList<MapClickListener>();
    private List<MapRightClickListener> mapRightClickListeners = new ArrayList<MapRightClickListener>();

    private List<InfoWindowClosedListener> infoWindowClosedListeners = new ArrayList<InfoWindowClosedListener>();
    private MapInitListener initListener;

    private List<PolygonCompleteListener> polygonCompleteListeners = new ArrayList<PolygonCompleteListener>();
    private List<PolygonEditListener> polygonEditListeners = new ArrayList<PolygonEditListener>();
    private List<PolygonClickListener> polygonClickListeners = new ArrayList<PolygonClickListener>();
    private List<PolygonRightClickListener> polygonRightClickListeners = new ArrayList<PolygonRightClickListener>();

    private List<CircleCompleteListener> circleCompleteListeners = new ArrayList<CircleCompleteListener>();
    private List<CircleCenterChangeListener> circleCenterChangeListeners = new ArrayList<CircleCenterChangeListener>();
    private List<CircleRadiusChangeListener> circleRadiusChangeListeners = new ArrayList<CircleRadiusChangeListener>();
    private List<CircleClickListener> circleClickListeners = new ArrayList<CircleClickListener>();
    private List<CircleDoubleClickListener> circleDoubleClickListeners = new ArrayList<CircleDoubleClickListener>();
    private List<CircleRightClickListener> circleRightClickListeners = new ArrayList<CircleRightClickListener>();

    /**
     * Initiates a new GoogleMap object with default settings from the
     * {@link GoogleMapState state object}.
     *
     * @param apiKey   The Maps API key from Google. Not required when developing in
     *                 localhost or when using a client id. Use null or empty string
     *                 to disable.
     * @param clientId Google Maps API for Work client ID. Use this instead of API
     *                 key if available. Use null or empty string to disable.
     * @param language The language to use with maps. See
     *                 https://developers.google.com/maps/faq#languagesupport for the
     *                 list of the supported languages. Use null or empty string to
     *                 disable.
     */
    public GoogleMap(String apiKey, String clientId, String language) {
        if (apiKey != null && !apiKey.isEmpty()) {
            getState().apiKey = apiKey;
        }
        if (clientId != null && !clientId.isEmpty()) {
            getState().clientId = clientId;
        }
        if (language != null && !language.isEmpty()) {
            getState().language = language;
        }

        registerRpc(markerClickedRpc);
        registerRpc(markerDoubleClickedRpc);
        registerRpc(markerRightClickedRpc);
        registerRpc(markerDraggedRpc);

        registerRpc(mapMovedRpc);
        registerRpc(mapClickedRpc);
        registerRpc(mapRightClickedRpc);
        registerRpc(mapInitRpc);

        registerRpc(infoWindowClosedRpc);
        registerRpc(handleDirectionsResultRpc);

        registerRpc(polygonClickedRpc);
        registerRpc(polygonRightClickedRpc);
        registerRpc(polygonCompleteRpc);
        registerRpc(polygonEditRpc);

        registerRpc(circleClickedRpc);
        registerRpc(circleDoubleClickRpc);
        registerRpc(circleRightClickedRpc);

        registerRpc(circleCenterChangeRpc);
        registerRpc(circleCompleteRpc);
        registerRpc(circleRadiusChangeRpc);
    }

    /**
     * Creates a new GoogleMap object with the given center. Other settings will
     * be {@link GoogleMapState defaults of the state object}.
     *
     * @param center   Coordinates of the center.
     * @param apiKey   The Maps server key from Google
     * @param clientId the client ID for the Business API. All client IDs begin with a gme- prefix. Not required
     *                 when developing in localhost.
     * @param language language
     */
    public GoogleMap(LatLon center, String apiKey, String clientId, String language) {
        this(apiKey, clientId, language);
        getState().center = center;
    }

    /**
     * Creates a new GoogleMap object with the given center and zoom. Other
     * settings will be {@link GoogleMapState defaults of the state object}.
     *
     * @param center   Coordinates of the center.
     * @param zoom     Amount of zoom.
     * @param apiKey   The Maps server key from Google
     * @param clientId the client ID for the Business API. All client IDs begin with a gme- prefix. Not required
     *                 when developing in localhost.
     * @param language language
     */
    public GoogleMap(LatLon center, int zoom, String apiKey, String clientId, String language) {
        this(apiKey, clientId, language);
        getState().zoom = zoom;
        getState().center = center;
    }

    /**
     * Creates a new GoogleMap object with the given center, zoom and ability
     * to set init listener. Other settings will be
     * {@link GoogleMapState defaults of the state object}.
     *
     * @param center       Coordinates of the center.
     * @param zoom         Amount of zoom.
     * @param apiKey       The Maps server key from Google
     * @param clientId     the client ID for the Business API. All client IDs begin with a gme- prefix. Not required
     *                     when developing in localhost.
     * @param language     language
     * @param initListener listener which will be called once, on map initialization. Map initialization
     *                     corresponds to "tilesloaded" event in google map api v3.
     */
    public GoogleMap(LatLon center, int zoom, String apiKey, String clientId, String language,
                     MapInitListener initListener) {
        this(apiKey, clientId, language);
        getState().zoom = zoom;
        getState().center = center;
        this.initListener = initListener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#getState()
     */
    @Override
    protected GoogleMapState getState() {
        return (GoogleMapState) super.getState();
    }

    @Override
    protected GoogleMapState getState(boolean markAsDirty) {
        return (GoogleMapState) super.getState(markAsDirty);
    }

    /**
     * Sets the center of the map to the given coordinates.
     *
     * @param center The new coordinates of the center.
     */
    public void setCenter(LatLon center) {
        getState().locationFromClient = false;
        getState().center = center;
    }

    /**
     * Returns the current position of the center of the map.
     *
     * @return Coordinates of the center.
     */
    public LatLon getCenter() {
        return getState(false).center;
    }

    /**
     * @return the current position of north-east bound of the map
     */
    public LatLon getBoundNE() {
        return getState(false).boundNE;
    }

    /**
     * @return the current position of south-west bound of the map
     */
    public LatLon getBoundSW() {
        return getState(false).boundSW;
    }

    /**
     * Zooms the map to the given value.
     *
     * @param zoom New amount of the zoom.
     */
    public void setZoom(int zoom) {
        getState().locationFromClient = false;
        getState().zoom = zoom;
    }

    /**
     * Returns the current zoom of the map.
     *
     * @return Current value of the zoom.
     */
    public int getZoom() {
        return getState(false).zoom;
    }

    /**
     * Adds a new marker to the map.
     *
     * @param caption   Caption of the marker shown when the marker is hovered.
     * @param position  Coordinates of the marker on the map.
     * @param draggable Set true to enable dragging of the marker.
     * @param iconUrl   The url of the icon of the marker.
     * @return GoogleMapMarker object created with the given settings.
     */
    public GoogleMapMarker addMarker(String caption, LatLon position,
                                     boolean draggable, String iconUrl) {
        GoogleMapMarker marker = new GoogleMapMarker(caption, position,
                draggable, iconUrl);
        getState().markers.put(marker.getId(), marker);
        return marker;
    }

    /**
     * Adds a marker to the map.
     *
     * @param marker The marker to add.
     */
    public void addMarker(GoogleMapMarker marker) {
        getState().markers.put(marker.getId(), marker);
    }

    /**
     * Removes a marker from the map.
     *
     * @param marker The marker to remove.
     */
    public void removeMarker(GoogleMapMarker marker) {
        getState().markers.remove(marker.getId());
    }

    /**
     * Removes all the markers from the map.
     */
    public void clearMarkers() {
        getState().markers.clear();
    }

    /**
     * Checks if a marker has been added to the map.
     *
     * @param marker The marker to check.
     * @return true, if the marker has been added to the map.
     */
    public boolean hasMarker(GoogleMapMarker marker) {
        return getState(false).markers.containsKey(marker.getId());
    }

    /**
     * Returns the markers that have been added to he map.
     *
     * @return Set of the markers.
     */
    public Collection<GoogleMapMarker> getMarkers() {
        return getState(false).markers.values();
    }

    /**
     * Adds a MarkerClickListener to the map.
     *
     * @param listener The listener to add.
     */
    public void addMarkerClickListener(MarkerClickListener listener) {
        markerClickListeners.add(listener);
    }

    /**
     * Removes a MarkerClickListener from the map.
     *
     * @param listener The listener to remove.
     */
    public void removeMarkerClickListener(MarkerClickListener listener) {
        markerClickListeners.remove(listener);
    }

    /**
     * Adds a MarkerRightClickListener to the map.
     *
     * @param listener the listener to add
     */
    public void addMarkerRightClickListener(MarkerRightClickListener listener) {
        markerRightClickListeners.add(listener);
    }

    /**
     * Removes a MarkerRightClickListener from the map.
     *
     * @param listener the listener to remove
     */
    public void removeMarkerRightClickListener(MarkerRightClickListener listener) {
        markerRightClickListeners.remove(listener);
    }

    /**
     * Adds a MarkerDoubleClickListener to the map.
     *
     * @param listener The listener to add.
     */
    public void addMarkerDoubleClickListener(MarkerDoubleClickListener listener) {
        markerDoubleClickListeners.add(listener);
    }

    /**
     * Removes a MarkerClickListener from the map.
     *
     * @param listener The listener to remove.
     */
    public void removeMarkerDoubleClickListener(MarkerDoubleClickListener listener) {
        markerDoubleClickListeners.remove(listener);
    }

    /**
     * Adds a MarkerDragListener to the map.
     *
     * @param listener The listener to add.
     */
    public void addMarkerDragListener(MarkerDragListener listener) {
        markerDragListeners.add(listener);
    }

    public void addPolygonCompleteListener(PolygonCompleteListener listener) {
        polygonCompleteListeners.add(listener);
    }

    public void removePolygonCompleteListener(PolygonCompleteListener listener) {
        polygonCompleteListeners.remove(listener);
    }

    public void addPolygonClickListener(PolygonClickListener listener) {
        polygonClickListeners.add(listener);
    }

    public void removePolygonClickListener(PolygonClickListener listener) {
        polygonClickListeners.remove(listener);
    }

    /**
     * Adds a PolygonRightClickListener to the map.
     *
     * @param listener the listener to add
     */
    public void addPolygonRightClickListener(PolygonRightClickListener listener) {
        polygonRightClickListeners.add(listener);
    }

    /**
     * Removes a PolygonRightClickListener from the map.
     *
     * @param listener the listener to remove.
     */
    public void removePolygonRightClickListener(PolygonRightClickListener listener) {
        polygonRightClickListeners.remove(listener);
    }

    public void addPolygonEditListener(PolygonEditListener listener) {
        polygonEditListeners.add(listener);
    }

    public void removePolygonEditListener(PolygonEditListener listener) {
        polygonEditListeners.remove(listener);
    }

    public void addCircleCompleteListener(CircleCompleteListener listener) {
        circleCompleteListeners.add(listener);
    }

    public void removeCircleCompleteListener(CircleCompleteListener listener) {
        circleCompleteListeners.remove(listener);
    }

    public void addCircleClickListener(CircleClickListener listener) {
        circleClickListeners.add(listener);
    }

    public void removeCircleClickListener(CircleClickListener listener) {
        circleClickListeners.remove(listener);
    }

    /**
     * Adds a CircleRightClickListener to the map.
     *
     * @param listener the listener to add
     */
    public void addCircleRightClickListener(CircleRightClickListener listener) {
        circleRightClickListeners.add(listener);
    }

    /**
     * Removes a CircleRightClickListener from the map.
     *
     * @param listener the listener to remove
     */
    public void removeCircleRightClickListener(CircleRightClickListener listener) {
        circleRightClickListeners.remove(listener);
    }

    public void addCircleDoubleClickListener(CircleDoubleClickListener listener) {
        circleDoubleClickListeners.add(listener);
    }

    public void removeCircleDoubleClickListener(CircleDoubleClickListener listener) {
        circleDoubleClickListeners.remove(listener);
    }

    public void addCircleCenterChangeListener(CircleCenterChangeListener listener) {
        circleCenterChangeListeners.add(listener);
    }

    public void removeCircleCenterChangeListener(CircleCenterChangeListener listener) {
        circleCenterChangeListeners.remove(listener);
    }

    public void addCircleRadiusChangeListener(CircleRadiusChangeListener listener) {
        circleRadiusChangeListeners.add(listener);
    }

    public void removeCircleRadiusChangeListener(CircleRadiusChangeListener listener) {
        circleRadiusChangeListeners.remove(listener);
    }

    /**
     * Removes a MarkerDragListener from the map.
     *
     * @param listener The listener to remove.
     */
    public void removeMarkerDragListener(MarkerDragListener listener) {
        markerDragListeners.remove(listener);
    }

    /**
     * Adds a MapMoveListener to the map.
     *
     * @param listener The listener to add.
     */
    public void addMapMoveListener(MapMoveListener listener) {
        mapMoveListeners.add(listener);
    }

    /**
     * Removes a MapMoveListener from the map.
     *
     * @param listener The listener to add.
     */
    public void removeMapMoveListener(MapMoveListener listener) {
        mapMoveListeners.remove(listener);
    }

    /**
     * Adds a MapClickListener to the map.
     *
     * @param listener The listener to add.
     */
    public void addMapClickListener(MapClickListener listener) {
        mapClickListeners.add(listener);
    }

    /**
     * Removes a MapClickListener from the map.
     *
     * @param listener The listener to add.
     */
    public void removeMapClickListener(MapClickListener listener) {
        mapClickListeners.remove(listener);
    }

    /**
     * Adds a MapRightClickListener to the map.
     *
     * @param listener the listener to add
     */
    public void addMapRightClickListener(MapRightClickListener listener) {
        mapRightClickListeners.add(listener);
    }

    /**
     * Removes a MapRightClickListener from the map.
     *
     * @param listener the listener to remove
     */
    public void removeMapRightClickListener(MapRightClickListener listener) {
        mapRightClickListeners.remove(listener);
    }

    /**
     * Adds an InfoWindowClosedListener to the map.
     *
     * @param listener The listener to add.
     */
    public void addInfoWindowClosedListener(InfoWindowClosedListener listener) {
        infoWindowClosedListeners.add(listener);
    }

    /**
     * Removes an InfoWindowClosedListener from the map.
     *
     * @param listener The listener to remove.
     */
    public void removeInfoWindowClosedListener(InfoWindowClosedListener listener) {
        infoWindowClosedListeners.remove(listener);
    }

    /**
     * Checks if limiting of the center bounds is enabled.
     *
     * @return true, if enabled
     */
    public boolean isCenterBoundLimitsEnabled() {
        return getState(false).limitCenterBounds;
    }

    /**
     * Enables/disables limiting of the center bounds.
     *
     * @param enable Set true to enable the limiting.
     */
    public void setCenterBoundLimitsEnabled(boolean enable) {
        getState().limitCenterBounds = enable;
    }

    /**
     * Sets the limits of the bounds of the center to given values.
     *
     * @param limitNE The coordinates of the northeast limit.
     * @param limitSW The coordinates of the southwest limit.
     */
    public void setCenterBoundLimits(LatLon limitNE, LatLon limitSW) {
        getState().centerNELimit = limitNE;
        getState().centerSWLimit = limitSW;
        getState().limitCenterBounds = true;
    }

    /**
     * Adds a polygon overlay to the map.
     *
     * @param polygon The GoogleMapPolygon to add.
     */
    public void addPolygonOverlay(GoogleMapPolygon polygon) {
        getState().polygons.put(polygon.getId(), polygon);
    }

    /**
     * Removes a polygon overlay from the map.
     *
     * @param polygon The GoogleMapPolygon to remove.
     */
    public void removePolygonOverlay(GoogleMapPolygon polygon) {
        getState().polygons.remove(polygon.getId());
    }

    /**
     * Adds a circle overlay to the map
     *
     * @param circle The GoogleMapCircle to add
     */
    public void addCircleOverlay(GoogleMapCircle circle) {
        getState().circles.put(circle.getId(), circle);
    }

    /**
     * Removes a circle overlay from the map
     *
     * @param circle The GoogleMapCircle to remove
     */
    public void removeCircleOverlay(GoogleMapCircle circle) {
        getState().circles.remove(circle.getId());
    }

    /**
     * Adds a polyline to the map.
     *
     * @param polyline The GoogleMapPolyline to add.
     */
    public void addPolyline(GoogleMapPolyline polyline) {
        getState().polylines.add(polyline);
    }

    /**
     * Removes a polyline from the map.
     *
     * @param polyline The GoogleMapPolyline to add.
     */
    public void removePolyline(GoogleMapPolyline polyline) {
        getState().polylines.remove(polyline);
    }

    /**
     * Adds a KML layer to the map.
     *
     * @param kmlLayer The KML layer to add.
     */
    public void addKmlLayer(GoogleMapKmlLayer kmlLayer) {
        getState().kmlLayers.add(kmlLayer);
    }

    /**
     * Removes a KML layer from the map.
     *
     * @param kmlLayer The KML layer to remove.
     */
    public void removeKmlLayer(GoogleMapKmlLayer kmlLayer) {
        getState().kmlLayers.remove(kmlLayer);
    }

    /**
     * Adds a HeatMap layer to the map.
     *
     * @param heatMapLayer The HeatMap layer to add.
     */
    public void addHeatMapLayer(GoogleMapHeatMapLayer heatMapLayer) {
        getState().heatMapLayers.add(heatMapLayer);
    }

    /**
     * Removes a HeatMap layer from the map.
     *
     * @param heatMapLayer The HeatMap layer to remove.
     */
    public void removeHeatMapLayer(GoogleMapHeatMapLayer heatMapLayer) {
        getState().heatMapLayers.remove(heatMapLayer);
    }

    public void addImageMapType(GoogleImageMapType imageMapType) {
        getState().imageMapTypes.add(imageMapType);
    }

    public void removeImageMapType(GoogleImageMapType imageMapType) {
        getState().imageMapTypes.remove(imageMapType);
    }

    public void addOverlayImageMapType(GoogleImageMapType imageMapType) {
        getState().overlayImageMapTypes.add(imageMapType);
    }

    public void removeOverlayImageMapType(GoogleImageMapType imageMapType) {
        getState().overlayImageMapTypes.remove(imageMapType);
    }

    /**
     * Sets the type of the base map.
     *
     * @param type The new MapType to use.
     */
    public void setMapType(MapType type) {
        getState().mapTypeId = type.name();
    }

    /**
     * Returns the current type of the base map.
     *
     * @return The current MapType.
     */
    public String getMapType() {
        return getState(false).mapTypeId;
    }

    public void setMapType(String mapTypeId) {
        getState().mapTypeId = mapTypeId;
    }

    /**
     * Checks if the map is currently draggable.
     *
     * @return true, if the map draggable.
     */
    public boolean isDraggable() {
        return getState(false).draggable;
    }

    /**
     * Enables/disables dragging of the map.
     *
     * @param draggable Set to true to enable dragging.
     */
    public void setDraggable(boolean draggable) {
        getState().draggable = draggable;
    }

    /**
     * Checks if the keyboard shortcuts are enabled.
     *
     * @return true, if the shortcuts are enabled.
     */
    public boolean areKeyboardShortcutsEnabled() {
        return getState(false).keyboardShortcutsEnabled;
    }

    /**
     * Enables/disables the keyboard shortcuts.
     *
     * @param enabled Set true to enable keyboard shortcuts.
     */
    public void setKeyboardShortcutsEnabled(boolean enabled) {
        getState().keyboardShortcutsEnabled = enabled;
    }

    /**
     * Checks if the scroll wheel is enabled.
     *
     * @return true, if the scroll wheel is enabled
     */
    public boolean isScrollWheelEnabled() {
        return getState(false).scrollWheelEnabled;
    }

    /**
     * Enables/disables the scroll wheel.
     *
     * @param enabled Set true to enable scroll wheel.
     */
    public void setScrollWheelEnabled(boolean enabled) {
        getState().scrollWheelEnabled = enabled;
    }

    /**
     * Returns the currently enabled map controls.
     *
     * @return Currently enabled map controls.
     */
    public Set<GoogleMapControl> getControls() {
        return getState(false).controls;
    }

    /**
     * Sets the controls of the map.
     *
     * @param controls The new controls to use.
     */
    public void setControls(Set<GoogleMapControl> controls) {
        getState().controls = controls;
    }

    public void setMapTypes(List<String> mapTypeIds) {
        getState().mapTypeIds = new ArrayList<String>(mapTypeIds);
    }

    /**
     * Enables the given control on the map. Does nothing if the control is
     * already enabled.
     *
     * @param control The control to enable.
     */
    public void addControl(GoogleMapControl control) {
        getState().controls.add(control);
    }

    /**
     * Removes the control from the map. Does nothing if the control isn't
     * enabled.
     *
     * @param control The control to remove.
     */
    public void removeControl(GoogleMapControl control) {
        getState().controls.remove(control);
    }

    /**
     * Enables/disables limiting of the bounds of the visible area.
     *
     * @param enabled Set true to enable the limiting.
     */
    public void setVisibleAreaBoundLimitsEnabled(boolean enabled) {
        getState().limitVisibleAreaBounds = enabled;

    }

    /**
     * Checks if limiting of the bounds of the visible area is enabled.
     *
     * @return true if enabled
     */
    public boolean isVisibleAreaBoundLimitsEnabled() {
        return getState(false).limitVisibleAreaBounds;
    }

    /**
     * Sets the limits of the bounds of the visible area to the given values.
     * NOTE: Using the feature does not affect zooming, consider using
     * {@link #setMinZoom(int)} too.
     *
     * @param limitNE The coordinates of the northeast limit.
     * @param limitSW The coordinates of the southwest limit.
     */
    public void setVisibleAreaBoundLimits(LatLon limitNE, LatLon limitSW) {
        getState().visibleAreaNELimit = limitNE;
        getState().visibleAreaSWLimit = limitSW;
        getState().limitVisibleAreaBounds = true;
    }

    /**
     * Sets the maximum allowed amount of zoom (default 21.0).
     *
     * @param maxZoom The maximum amount for zoom.
     */
    public void setMaxZoom(int maxZoom) {
        getState().maxZoom = maxZoom;
    }

    /**
     * Returns the current maximum amount of zoom.
     *
     * @return maximum amount of zoom
     */
    public int getMaxZoom() {
        return getState(false).maxZoom;
    }

    /**
     * Sets the minimum allowed amount of zoom (default 0.0).
     *
     * @param minZoom The minimum amount for zoom.
     */
    public void setMinZoom(int minZoom) {
        getState().minZoom = minZoom;
    }

    /**
     * Returns the current minimum amount of zoom.
     *
     * @return minimum amount of zoom
     */
    public int getMinZoom() {
        return getState(false).minZoom;
    }

    /**
     * Opens an info window.
     *
     * @param infoWindow The window to open.
     */
    public void openInfoWindow(GoogleMapInfoWindow infoWindow) {
        getState().infoWindows.put(infoWindow.getId(), infoWindow);
    }

    /**
     * Closes an info window.
     *
     * @param infoWindow The window to close.
     */
    public void closeInfoWindow(GoogleMapInfoWindow infoWindow) {
        getState().infoWindows.remove(infoWindow.getId());
    }

    /**
     * Checks if an info window is open.
     *
     * @param infoWindow The window to check.
     * @return true, if the window is open.
     */
    public boolean isInfoWindowOpen(GoogleMapInfoWindow infoWindow) {
        return getState(false).infoWindows.containsKey(infoWindow.getId());
    }

    /**
     * Enables/disables new visual style of the map. NOTICE: this must be set
     * before rendering the map.
     *
     * @param enabled Set true to enable (defaul false).
     */
    public void setVisualRefreshEnabled(boolean enabled) {
        getState().visualRefreshEnabled = enabled;
    }

    /**
     * Checks if the new visual style is enabled.
     *
     * @return true, if visual refresh is enabled
     */
    public boolean isVisualRefreshEnabled() {
        return getState(false).visualRefreshEnabled;
    }

    /**
     * Tries to fit the visible area of the map inside given boundaries by
     * modifying zoom and/or center.
     *
     * @param boundsNE The northeast boundaries.
     * @param boundsSW The southwest boundaries.
     */
    public void fitToBounds(LatLon boundsNE, LatLon boundsSW) {
        getState().fitToBoundsNE = boundsNE;
        getState().fitToBoundsSW = boundsSW;
    }

    private boolean isClientId(String apiKeyOrClientId) {
        return apiKeyOrClientId != null && apiKeyOrClientId.startsWith("gme-");
    }

    public void setDrawingOptions(DrawingOptions drawingOptions) {
        getState().drawingOptions = drawingOptions;
    }

    public DrawingOptions getDrawingOptions() {
        return getState(false).drawingOptions;
    }

    public void route(DirectionsRequest request, DirectionsResultCallback handler) {
        getState().directionsRequests.put(request.getId(), request);
        directionsCallbacks.put(request.getId(), handler);
    }

    public void addLabel(GoogleMapLabel label) {
        getState().labels.put(label.getId(), label);
    }

    public void removeLabel(GoogleMapLabel label) {
        getState().labels.remove(label.getId(), label);
    }

    public void removePolygonVertex(GoogleMapPolygon polygon, LatLon vertex) {
        if (polygon == null || vertex == null)
            return;

        if (polygon.getCoordinates().size() <= MIN_VERTEX_COUNT)
            return;

        getRpcProxy(PolygonRemoveVertexRpc.class).removeVertex(polygon, vertex);
    }

    public void setRemoveMessage(String message) {
        if (message == null || message.isEmpty())
            return;

        if (!getState(false).removeMessage.equals(message)) {
            getState().removeMessage = message;
        }
    }

    public String getRemoveMessage() {
        return getState(false).removeMessage;
    }

    public void setVertexRemovingEnabled(boolean enabled) {
        if (getState(false).vertexRemovingEnabled != enabled) {
            getState().vertexRemovingEnabled = enabled;
        }
    }

    public boolean isVertexRemovingEnabled() {
        return getState(false).vertexRemovingEnabled;
    }
}
