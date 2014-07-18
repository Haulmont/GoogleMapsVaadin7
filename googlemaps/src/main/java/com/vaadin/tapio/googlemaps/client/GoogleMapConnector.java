package com.vaadin.tapio.googlemaps.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.user.client.ui.Widget;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.events.*;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.tapio.googlemaps.client.rpcs.*;

@Connect(GoogleMap.class)
public class GoogleMapConnector extends AbstractComponentContainerConnector implements
        MarkerClickListener, MapMoveListener, MapClickListener,
        MarkerDragListener, InfoWindowClosedListener, MapTypeChangeListener,
        PolygonCompleteListener, PolygonEditListener, MapInitListener {

    private static final long serialVersionUID = -357262975672050103L;

    public static boolean apiLoaded = false;

    public static boolean loadingApi = false;

    private final List<GoogleMapInitListener> initListeners = new ArrayList<GoogleMapInitListener>();

    private boolean deferred = false;
    private MarkerClickedRpc markerClickedRpc = RpcProxy.create(
            MarkerClickedRpc.class, this);
    private MapMovedRpc mapMovedRpc = RpcProxy.create(
            MapMovedRpc.class, this);
    private MapInitRpc mapInitRpc = RpcProxy.create(
            MapInitRpc.class, this);
    private MapClickedRpc mapClickRpc = RpcProxy.create(
            MapClickedRpc.class, this);
    private MarkerDraggedRpc markerDraggedRpc = RpcProxy.create(
            MarkerDraggedRpc.class, this);
    private InfoWindowClosedRpc infoWindowClosedRpc = RpcProxy.create(
            InfoWindowClosedRpc.class, this);
    private PolygonCompleteRpc polygonCompleteRpc = RpcProxy.create(
            PolygonCompleteRpc.class, this);
    private PolygonEditRpc polygonEditRpc = RpcProxy.create(
            PolygonEditRpc.class, this);
    private final MapTypeChangedRpc mapTypeChangedRpc = RpcProxy
            .create(MapTypeChangedRpc.class, this);

    public GoogleMapConnector() {
    }

    protected void loadMapApi() {
        if (loadingApi) {
            return;
        }
        loadingApi = true;
        ArrayList<LoadApi.LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
        Runnable onLoad = new Runnable() {
            @Override
            public void run() {
                apiLoaded = true;
                loadingApi = false;
                for (GoogleMapInitListener listener : initListeners) {
                    listener.mapsApiLoaded();
                }
                initMap();
            }
        };

        LoadApi.Language language = null;
        if (getState().language != null) {
            language = LoadApi.Language.fromValue(getState().language);
        }

        String params = null;
        if (getState().clientId != null) {
            params = "client=" + getState().clientId;
        } else if (getState().apiKey != null) {
            params = "key=" + getState().apiKey;
        }

        if (getState().apiUrl != null) {
            AjaxLoader.init(getState().apiKey, getState().apiUrl);
        }

        LoadApi.go(onLoad, loadLibraries, false, language, params);
    }

    protected void initMap() {
        getWidget().initMap(getState().center, getState().zoom, getState().mapTypeId, this);
        getWidget().setMarkerClickListener(this);
        getWidget().setMapMoveListener(this);
        getWidget().setMapClickListener(this);
        getWidget().setMarkerDragListener(this);
        getWidget().setInfoWindowClosedListener(this);
        getWidget().setMapTypeChangeListener(this);
        getWidget().setPolygonCompleteListener(this);
        getWidget().setPolygonEditListener(this);
        getLayoutManager().addElementResizeListener(getWidget().getElement(),
            new ElementResizeListener() {
                @Override
                public void onElementResize(ElementResizeEvent e) {
                    getWidget().triggerResize();
                }
            });
        MapWidget map = getWidget().getMap();
        updateFromState(true);
        for (GoogleMapInitListener listener : initListeners) {
            listener.mapWidgetInitiated(map);
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        if (!apiLoaded) {
            loadMapApi();
            return;
        } else if (getWidget().getMap() == null) {
            initMap();
        }
        updateFromState(stateChangeEvent.isInitialStateChange());
    }

    protected void updateFromState(boolean initial) {
        updateVisibleAreaAndCenterBoundLimits();

        LatLng center = LatLng.newInstance(getState().center.getLat(),
            getState().center.getLon());
        getWidget().setCenter(center);
        getWidget().setZoom(getState().zoom);
        getWidget().setTrafficLayerVisible(getState().trafficLayerVisible);
        getWidget().setMarkers(getState().markers.values());
        getWidget().setPolygonOverlays(getState().polygons);
        getWidget().setPolylineOverlays(getState().polylines);
        getWidget().setKmlLayers(getState().kmlLayers);
        getWidget().setInfoWindows(getState().infoWindows.values());
        getWidget().setMapType(getState().mapTypeId);
        getWidget().setControls(getState().controls);
        getWidget().setDraggable(getState().draggable);
        getWidget().setKeyboardShortcutsEnabled(
                getState().keyboardShortcutsEnabled);
        getWidget().setScrollWheelEnabled(getState().scrollWheelEnabled);
        getWidget().setMinZoom(getState().minZoom);
        getWidget().setMaxZoom(getState().maxZoom);
        getWidget().setDrawingOptions(getState().drawingOptions);
        if (getState().fitToBoundsNE != null
                && getState().fitToBoundsSW != null) {
            getWidget().fitToBounds(getState().fitToBoundsNE,
                    getState().fitToBoundsSW);
        }
        getWidget().updateOptionsAndPanning();
        if (initial) {
            getWidget().triggerResize();
        }
		onConnectorHierarchyChange(null);
    }

    protected void updateVisibleAreaAndCenterBoundLimits() {
        if (getState().limitCenterBounds) {
            getWidget().setCenterBoundLimits(getState().centerNELimit,
                getState().centerSWLimit);
        } else {
            getWidget().clearCenterBoundLimits();
        }

        if (getState().limitVisibleAreaBounds) {
            getWidget().setVisibleAreaBoundLimits(getState().visibleAreaNELimit,
                getState().visibleAreaSWLimit);
        } else {
            getWidget().clearVisibleAreaBoundLimits();
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(GoogleMapWidget.class);
    }

    @Override
    public GoogleMapWidget getWidget() {
        return (GoogleMapWidget) super.getWidget();
    }

    @Override
    public void init(LatLon center, int zoom, LatLon boundsNE, LatLon boundsSW) {
        mapInitRpc.init(center, zoom, boundsNE, boundsSW);
    }

    @Override
    public GoogleMapState getState() {
        return (GoogleMapState) super.getState();
    }

    @Override
    public void infoWindowClosed(GoogleMapInfoWindow window) {
        infoWindowClosedRpc.infoWindowClosed(window.getId());
    }

    @Override
    public void markerDragged(GoogleMapMarker draggedMarker,
        LatLon oldPosition) {
        markerDraggedRpc.markerDragged(draggedMarker.getId(),
            draggedMarker.getPosition());
    }

    @Override
    public void polygonComplete(GoogleMapPolygon polygon) {
        polygonCompleteRpc.polygonComplete(polygon);
    }

    @Override
    public void mapClicked(LatLon position) {
        mapClickRpc.mapClicked(position);
    }

    @Override
    public void mapMoved(int zoomLevel, LatLon center, LatLon boundsNE,
        LatLon boundsSW) {
        mapMovedRpc.mapMoved(zoomLevel, center, boundsNE, boundsSW);
    }

    @Override
    public void markerClicked(GoogleMapMarker clickedMarker) {
        markerClickedRpc.markerClicked(clickedMarker.getId());
    }

    @Override
    public void mapTypeChanged(MapTypeId mapTypeId) {
        mapTypeChangedRpc.mapTypeChanged(mapTypeId.toString());
    }

    public void addInitListener(GoogleMapInitListener listener) {
        if (apiLoaded) {
            listener.mapsApiLoaded();
        }
        if (getWidget().getMap() != null) {
            listener.mapWidgetInitiated(getWidget().getMap());
        }
        initListeners.add(listener);
    }

    @Override
    public void onConnectorHierarchyChange(
        ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        Map<Long, Widget> infoWindowContents = new HashMap<>();
        List<ComponentConnector> children = getChildComponents();
        for (ComponentConnector connector : children) {
            for (String style : connector.getState().styles) {
                if (style.startsWith("content-for-infowindow-")) {
                    String identifier = style
                        .replace("content-for-infowindow-", "");
                    Long id = Long.parseLong(identifier);
                    infoWindowContents.put(id, connector.getWidget());
                    getWidget().setInfoWindowContents(infoWindowContents);
                }
            }
        }
    }

    @Override
    public void updateCaption(ComponentConnector connector) {

    }

    @Override
    public void polygonEdited(GoogleMapPolygon polygon, ActionType actionType, int idx, LatLon latLon) {
        polygonEditRpc.polygonEdited(polygon.getId(), actionType, idx, latLon);
    }
}
