package com.vaadin.tapio.googlemaps.client;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.events.*;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.tapio.googlemaps.client.rpcs.*;

import java.util.ArrayList;

/**
 * The connector for the Google Maps JavaScript API v3.
 * 
 * @author Tapio Aali <tapio@vaadin.com>
 */
@Connect(GoogleMap.class)
public class GoogleMapConnector extends AbstractComponentConnector implements
        MarkerClickListener, MarkerDoubleClickListener, MapMoveListener, MapClickListener,
        MarkerDragListener, InfoWindowClosedListener,
        PolygonCompleteListener, PolygonEditListener, MapInitListener {

    private static final long serialVersionUID = 646346521643L;

    protected static boolean apiLoaded = false;
    protected static boolean mapInitiated = false;

    private boolean deferred = false;
    private MarkerClickedRpc markerClickedRpc = RpcProxy.create(
            MarkerClickedRpc.class, this);
    private MarkerDoubleClickedRpc markerDoubleClickedRpc = RpcProxy.create(
            MarkerDoubleClickedRpc.class, this);
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

    public GoogleMapConnector() {
    }

    private void initMap() {
        getWidget().setVisualRefreshEnabled(getState().visualRefreshEnabled);
        getWidget().initMap(getState().center, getState().zoom, getState().mapTypeId, this);
        getWidget().setMarkerClickListener(this);
        getWidget().setMarkerDoubleClickListener(this);
        getWidget().setMapMoveListener(this);
        getWidget().setMapClickListener(this);
        getWidget().setMarkerDragListener(this);
        getWidget().setInfoWindowClosedListener(this);
        getWidget().setPolygonCompleteListener(this);
        getWidget().setPolygonEditListener(this);

        if (deferred) {
            loadDeferred();
            deferred = false;
        }
        getLayoutManager().addElementResizeListener(getWidget().getElement(),
                new ElementResizeListener() {
                    @Override
                    public void onElementResize(ElementResizeEvent e) {
                        getWidget().triggerResize();
                    }
                });
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
    public GoogleMapState getState() {
        return (GoogleMapState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        GoogleMapWidget widget = getWidget();
        // settings that can be set without API being loaded/map initiated
        if (getState().limitCenterBounds) {
            widget.setCenterBoundLimits(getState().centerNELimit,
                    getState().centerSWLimit);
        } else {
            widget.clearCenterBoundLimits();
        }

        if (getState().limitVisibleAreaBounds) {
            widget.setVisibleAreaBoundLimits(
                    getState().visibleAreaNELimit,
                    getState().visibleAreaSWLimit);
        } else {
            widget.clearVisibleAreaBoundLimits();
        }

        // load API/init map
        if (!apiLoaded) {
            deferred = true;
            loadMapApi();
            apiLoaded = true;
            return;
        } else if (!widget.isMapInitiated()) {
            deferred = true;
            initMap();
            return;
        }

        // settings that require initiated map
        boolean initial = stateChangeEvent.isInitialStateChange();
        // do not set zoom/center again if the change originated from client
        if (!getState().locationFromClient || initial) {
            if (getState().center.getLat() != widget.getLatitude()
                    || getState().center.getLon() != widget.getLongitude()) {
                widget.setCenter(getState().center);
            }
            if (getState().zoom != widget.getZoom()) {
                widget.setZoom(getState().zoom);
            }
        }

        if (stateChangeEvent.hasPropertyChanged("markers") || initial) {
            widget.setMarkers(getState().markers.values());
        }

        if (stateChangeEvent.hasPropertyChanged("polygons") || initial) {
            widget.setPolygonOverlays(getState().polygons);
        }
        if (stateChangeEvent.hasPropertyChanged("polylines") || initial) {
            widget.setPolylineOverlays(getState().polylines);
        }

        if (stateChangeEvent.hasPropertyChanged("kmlLayers") || initial) {
            widget.setKmlLayers(getState().kmlLayers);
        }

        if (stateChangeEvent.hasPropertyChanged("heatMapLayers") || initial) {
            widget.setHeatMapLayers(getState().heatMapLayers);
        }

        if (stateChangeEvent.hasPropertyChanged("mapTypeId") || initial) {
            widget.setMapType(getState().mapTypeId);
        }

        if (stateChangeEvent.hasPropertyChanged("controls") || initial) {
            widget.setControls(getState().controls);
        }

        if (stateChangeEvent.hasPropertyChanged("draggable") || initial) {
            widget.setDraggable(getState().draggable);
        }
        if (stateChangeEvent.hasPropertyChanged("keyboardShortcutsEnabled")
                || initial) {
            widget.setKeyboardShortcutsEnabled(
                    getState().keyboardShortcutsEnabled);
        }
        if (stateChangeEvent.hasPropertyChanged("scrollWheelEnabled")
                || initial) {
            widget.setScrollWheelEnabled(getState().scrollWheelEnabled);
        }
        if (stateChangeEvent.hasPropertyChanged("minZoom") || initial) {
            widget.setMinZoom(getState().minZoom);
        }
        if (stateChangeEvent.hasPropertyChanged("maxZoom") || initial) {
            widget.setMaxZoom(getState().maxZoom);
        }

        if (stateChangeEvent.hasPropertyChanged("infoWindows") || initial) {
            widget.setInfoWindows(getState().infoWindows.values());
        }

        if (stateChangeEvent.hasPropertyChanged("visualRefreshEnabled")
                || initial) {
            widget.setVisualRefreshEnabled(getState().visualRefreshEnabled);
        }

        if (stateChangeEvent.hasPropertyChanged("fitToBoundsNE")
                || stateChangeEvent.hasPropertyChanged("fitToBoundsSW")
                || initial) {
            if (getState().fitToBoundsNE != null
                    && getState().fitToBoundsSW != null) {
                widget.fitToBounds(getState().fitToBoundsNE,
                        getState().fitToBoundsSW);
            }
        }

        if (stateChangeEvent.hasPropertyChanged("drawingOptions") || initial) {
            widget.setDrawingOptions(getState().drawingOptions);
        }

        if (initial) {
            widget.triggerResize();
        }

    }

    private void loadMapApi() {
        StringBuilder otherParams = new StringBuilder();
        if (getState().language != null) {
            otherParams.append("&language=").append(getState().language);
        }

        ArrayList<LoadApi.LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
        loadLibraries.add(LoadApi.LoadLibrary.DRAWING);
        loadLibraries.add(LoadApi.LoadLibrary.VISUALIZATION);

        Runnable callback = new Runnable() {
            public void run() {
                initMap();
            }
        };

        AjaxLoader.init(getState().apiKey);
        
        LoadApi.go(callback, loadLibraries, false, otherParams.toString());
    }

    private void loadDeferred() {
        getWidget().setMarkers(getState().markers.values());
        getWidget().setPolygonOverlays(getState().polygons);
        getWidget().setPolylineOverlays(getState().polylines);
        getWidget().setKmlLayers(getState().kmlLayers);
        getWidget().setHeatMapLayers(getState().heatMapLayers);
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
    }

    @Override
    public void markerClicked(GoogleMapMarker clickedMarker) {
        markerClickedRpc.markerClicked(clickedMarker.getId());
    }

    @Override
    public void markerDoubleClicked(GoogleMapMarker clickedMarker) {
        markerDoubleClickedRpc.markerClicked(clickedMarker.getId());
    }

    @Override
    public void mapMoved(int zoomLevel, LatLon center, LatLon boundsNE,
            LatLon boundsSW) {
        mapMovedRpc.mapMoved(zoomLevel, center, boundsNE, boundsSW);
    }

    @Override
    public void init(LatLon center, int zoom, LatLon boundsNE, LatLon boundsSW) {
        mapInitRpc.init(center, zoom, boundsNE, boundsSW);
    }

    @Override
    public void markerDragged(GoogleMapMarker draggedMarker, LatLon oldPosition) {
        markerDraggedRpc.markerDragged(draggedMarker.getId(),
                draggedMarker.getPosition());
    }

    @Override
    public void infoWindowClosed(GoogleMapInfoWindow window) {
        infoWindowClosedRpc.infoWindowClosed(window.getId());
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
    public void polygonEdited(GoogleMapPolygon polygon, ActionType actionType, int idx, LatLon latLon) {
        polygonEditRpc.polygonEdited(polygon.getId(), actionType, idx, latLon);
    }
}
