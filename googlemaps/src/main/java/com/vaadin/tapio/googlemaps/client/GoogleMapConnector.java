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
import com.vaadin.tapio.googlemaps.client.base.LatLon;
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
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapCircle;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
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
import com.vaadin.tapio.googlemaps.client.services.DirectionsResult;
import com.vaadin.tapio.googlemaps.client.services.DirectionsStatus;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The connector for the Google Maps JavaScript API v3.
 *
 * @author Tapio Aali <tapio@vaadin.com>
 */
@Connect(GoogleMap.class)
public class GoogleMapConnector extends AbstractComponentConnector implements
        MarkerClickListener, MarkerDoubleClickListener, MarkerDragListener, MarkerRightClickListener,
        MapMoveListener, MapClickListener, MapInitListener, MapRightClickListener,
        PolygonCompleteListener, PolygonEditListener, PolygonClickListener, PolygonRightClickListener,
        CircleClickListener, CircleDoubleClickListener, CircleCompleteListener, CircleRadiusChangeListener,
        CircleCenterChangeListener, CircleRightClickListener, DirectionsResultHandler, InfoWindowClosedListener {

    private static final long serialVersionUID = 646346521643L;

    public static boolean loadingApi = false;
    protected static boolean apiLoaded = false;
    protected static boolean mapInitiated = false;

    private boolean deferred = false;

    private InfoWindowClosedRpc infoWindowClosedRpc = RpcProxy.create(InfoWindowClosedRpc.class, this);
    private HandleDirectionsResultRpc handleDirectionsResultRpc = RpcProxy.create(HandleDirectionsResultRpc.class, this);

    private MarkerClickedRpc markerClickedRpc = RpcProxy.create(MarkerClickedRpc.class, this);
    private MarkerDoubleClickedRpc markerDoubleClickedRpc = RpcProxy.create(MarkerDoubleClickedRpc.class, this);
    private MarkerRightClickedRpc markerRightClickedRpc = RpcProxy.create(MarkerRightClickedRpc.class, this);
    private MarkerDraggedRpc markerDraggedRpc = RpcProxy.create(MarkerDraggedRpc.class, this);

    private MapClickedRpc mapClickRpc = RpcProxy.create(MapClickedRpc.class, this);
    private MapRightClickedRpc mapRightClickedRpc = RpcProxy.create(MapRightClickedRpc.class, this);
    private MapMovedRpc mapMovedRpc = RpcProxy.create(MapMovedRpc.class, this);
    private MapInitRpc mapInitRpc = RpcProxy.create(MapInitRpc.class, this);

    private PolygonClickedRpc polygonClickedRpc = RpcProxy.create(PolygonClickedRpc.class, this);
    private PolygonRightClickedRpc polygonRightClickedRpc = RpcProxy.create(PolygonRightClickedRpc.class, this);
    private PolygonCompleteRpc polygonCompleteRpc = RpcProxy.create(PolygonCompleteRpc.class, this);
    private PolygonEditRpc polygonEditRpc = RpcProxy.create(PolygonEditRpc.class, this);

    private CircleClickedRpc circleClickedRpc = RpcProxy.create(CircleClickedRpc.class, this);
    private CircleDoubleClickRpc circleDoubleClickRpc = RpcProxy.create(CircleDoubleClickRpc.class, this);
    private CircleRightClickedRpc circleRightClickedRpc = RpcProxy.create(CircleRightClickedRpc.class, this);
    private CircleCenterChangeRpc circleCenterChangeRpc = RpcProxy.create(CircleCenterChangeRpc.class, this);
    private CircleRadiusChangeRpc circleRadiusChangeRpc = RpcProxy.create(CircleRadiusChangeRpc.class, this);
    private CircleCompleteRpc circleCompleteRpc = RpcProxy.create(CircleCompleteRpc.class, this);

    public GoogleMapConnector() {
        registerRpc(PolygonRemoveVertexRpc.class, new PolygonRemoveVertexRpc() {
            @Override
            public void removeVertex(GoogleMapPolygon polygon, LatLon vertex) {
                getWidget().removeVertex(polygon, vertex);
            }
        });
    }

    private void initMap() {
        final GoogleMapWidget googleMap = getWidget();

        googleMap.setVisualRefreshEnabled(getState().visualRefreshEnabled);
        googleMap.initMap(getState().center, getState().zoom, getState().mapTypeId, this);
        googleMap.setRemoveMessage(getState().removeMessage);
        googleMap.setVertexRemovingEnabled(getState().vertexRemovingEnabled);

        googleMap.setMarkerClickListener(this);
        googleMap.setMarkerDoubleClickListener(this);
        googleMap.setMarkerRightClickListener(this);
        googleMap.setMarkerDragListener(this);

        googleMap.setMapClickListener(this);
        googleMap.setMapRightClickListener(this);
        googleMap.setMapMoveListener(this);

        googleMap.setInfoWindowClosedListener(this);
        googleMap.setDirectionsResultHandler(this);

        googleMap.setPolygonClickListener(this);
        googleMap.setPolygonRightClickListener(this);
        googleMap.setPolygonCompleteListener(this);
        googleMap.setPolygonEditListener(this);

        googleMap.setCircleClickListener(this);
        googleMap.setCircleDoubleClickListener(this);
        googleMap.setCircleRightClickListener(this);
        googleMap.setCircleCompleteListener(this);
        googleMap.setCircleCenterChangeListener(this);
        googleMap.setCircleRadiusChangeListener(this);

        if (deferred) {
            loadDeferred();
            deferred = false;
        }
        getLayoutManager().addElementResizeListener(googleMap.getElement(),
                new ElementResizeListener() {
                    @Override
                    public void onElementResize(ElementResizeEvent e) {
                        googleMap.triggerResize();
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

        if (stateChangeEvent.hasPropertyChanged("removeMessage") || initial) {
            widget.setRemoveMessage(getState().removeMessage);
        }

        if (stateChangeEvent.hasPropertyChanged("vertexRemovingEnabled")) {
            widget.setVertexRemovingEnabled(getState().vertexRemovingEnabled);
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

        if (stateChangeEvent.hasPropertyChanged("circles") || initial) {
            widget.setCircleOverlays(getState().circles);
        }

        if (stateChangeEvent.hasPropertyChanged("labels") || initial) {
            widget.setLabels(getState().labels.values());
        }

        if (stateChangeEvent.hasPropertyChanged("kmlLayers") || initial) {
            widget.setKmlLayers(getState().kmlLayers);
        }

        if (stateChangeEvent.hasPropertyChanged("heatMapLayers") || initial) {
            widget.setHeatMapLayers(getState().heatMapLayers);
        }

        if (stateChangeEvent.hasPropertyChanged("imageMapTypes") || initial) {
            widget.setImageMapTypes(getState().imageMapTypes);
        }

        if (stateChangeEvent.hasPropertyChanged("overlayImageMapTypes") || initial) {
            widget.setOverlayImageMapTypes(getState().overlayImageMapTypes);
        }

        if (stateChangeEvent.hasPropertyChanged("mapTypeIds") || initial) {
            widget.setMapTypes(getState().mapTypeIds);
        }

        if (stateChangeEvent.hasPropertyChanged("directionsRequests") || initial) {
            widget.processDirectionRequests(getState().directionsRequests.values());
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

    protected void loadMapApi() {
        if (loadingApi) {
            return;
        }
        loadingApi = true;
        ArrayList<LoadApi.LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
        loadLibraries.add(LoadApi.LoadLibrary.DRAWING);
        loadLibraries.add(LoadApi.LoadLibrary.VISUALIZATION);

        Runnable onLoad = new Runnable() {
            @Override
            public void run() {
                apiLoaded = true;
                loadingApi = false;
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

        load(onLoad, loadLibraries, language, params);
    }

    private static void load(Runnable onLoad, ArrayList<LoadApi.LoadLibrary> loadLibraries, LoadApi.Language language, String otherParams) {
        String op = "";
        if (otherParams != null) {
            op = op + "&" + otherParams;
        }

        if (loadLibraries != null) {
            op = op + "&" + getLibraries(loadLibraries);
        }

        if (language != null) {
            op = op + "&language=" + language.getValue();
        }

        AjaxLoader.AjaxLoaderOptions settings = AjaxLoader.AjaxLoaderOptions.newInstance();
        settings.setOtherParms(op);
        AjaxLoader.loadApi("maps", "3.25", onLoad, settings);
    }

    private static String getLibraries(ArrayList<LoadApi.LoadLibrary> loadLibraries) {
        if (loadLibraries == null) {
            return "";
        } else {
            StringBuilder s = new StringBuilder("libraries=");
            Iterator itr = loadLibraries.iterator();
            int i = 0;

            while (itr.hasNext()) {
                LoadApi.LoadLibrary ll = (LoadApi.LoadLibrary) itr.next();
                if (ll != null) {
                    if (i > 0) {
                        s.append(",");
                    }

                    s.append(ll.value());
                    ++i;
                }
            }

            return s.toString();
        }
    }

    private void loadDeferred() {
        GoogleMapWidget googleMap = getWidget();

        googleMap.setMarkers(getState().markers.values());
        googleMap.setLabels(getState().labels.values());
        googleMap.setPolygonOverlays(getState().polygons);
        googleMap.setPolylineOverlays(getState().polylines);
        googleMap.setCircleOverlays(getState().circles);
        googleMap.setKmlLayers(getState().kmlLayers);
        googleMap.setHeatMapLayers(getState().heatMapLayers);
        googleMap.setImageMapTypes(getState().imageMapTypes);
        googleMap.setOverlayImageMapTypes(getState().overlayImageMapTypes);
        googleMap.setInfoWindows(getState().infoWindows.values());
        googleMap.setMapTypes(getState().mapTypeIds);
        googleMap.setMapType(getState().mapTypeId);
        googleMap.setControls(getState().controls);
        googleMap.setDraggable(getState().draggable);
        googleMap.setKeyboardShortcutsEnabled(
                getState().keyboardShortcutsEnabled);
        googleMap.setScrollWheelEnabled(getState().scrollWheelEnabled);
        googleMap.setMinZoom(getState().minZoom);
        googleMap.setMaxZoom(getState().maxZoom);
        googleMap.setDrawingOptions(getState().drawingOptions);
        googleMap.processDirectionRequests(getState().directionsRequests.values());
        if (getState().fitToBoundsNE != null
                && getState().fitToBoundsSW != null) {
            googleMap.fitToBounds(getState().fitToBoundsNE,
                    getState().fitToBoundsSW);
        }
    }

    @Override
    public void markerClicked(GoogleMapMarker clickedMarker) {
        if (isEnabled()) {
            markerClickedRpc.markerClicked(clickedMarker.getId());
        }
    }

    @Override
    public void markerDoubleClicked(GoogleMapMarker clickedMarker) {
        if (isEnabled()) {
            markerDoubleClickedRpc.markerClicked(clickedMarker.getId());
        }
    }

    @Override
    public void mapMoved(int zoomLevel, LatLon center, LatLon boundsNE,
                         LatLon boundsSW) {
        if (isEnabled()) {
            mapMovedRpc.mapMoved(zoomLevel, center, boundsNE, boundsSW);
        }
    }

    @Override
    public void init(LatLon center, int zoom, LatLon boundsNE, LatLon boundsSW) {
        if (isEnabled()) {
            mapInitRpc.init(center, zoom, boundsNE, boundsSW);
        }
    }

    @Override
    public void markerDragged(GoogleMapMarker draggedMarker, LatLon oldPosition) {
        if (isEnabled()) {
            markerDraggedRpc.markerDragged(draggedMarker.getId(),
                    draggedMarker.getPosition());
        }
    }

    @Override
    public void infoWindowClosed(GoogleMapInfoWindow window) {
        if (isEnabled()) {
            infoWindowClosedRpc.infoWindowClosed(window.getId());
        }
    }

    @Override
    public void polygonComplete(GoogleMapPolygon polygon) {
        if (isEnabled()) {
            polygonCompleteRpc.polygonComplete(polygon);
        }
    }

    @Override
    public void mapClicked(LatLon position) {
        if (isEnabled()) {
            mapClickRpc.mapClicked(position);
        }
    }

    @Override
    public void polygonEdited(GoogleMapPolygon polygon, ActionType actionType, int idx, LatLon latLon) {
        if (isEnabled()) {
            polygonEditRpc.polygonEdited(polygon.getId(), actionType, idx, latLon);
        }
    }

    @Override
    public void polygonClicked(GoogleMapPolygon polygon) {
        if (isEnabled()) {
            polygonClickedRpc.polygonClicked(polygon.getId());
        }
    }

    @Override
    public void handle(long requestId, DirectionsResult result, DirectionsStatus status) {
        if (isEnabled()) {
            handleDirectionsResultRpc.handle(result, status, requestId);
        }
    }

    @Override
    public void radiusChange(GoogleMapCircle circle, double oldRadius) {
        if (isEnabled()) {
            circleRadiusChangeRpc.radiusChanged(circle.getId(), circle.getRadius());
        }
    }

    @Override
    public void circleDoubleClicked(GoogleMapCircle circle) {
        if (isEnabled()) {
            circleDoubleClickRpc.circleDoubleClicked(circle.getId());
        }
    }

    @Override
    public void circleComplete(GoogleMapCircle circle) {
        if (isEnabled()) {
            circleCompleteRpc.circleComplete(circle);
        }
    }

    @Override
    public void circleClicked(GoogleMapCircle clickedCircle) {
        if (isEnabled()) {
            circleClickedRpc.circleClicked(clickedCircle.getId());
        }
    }

    @Override
    public void centerChanged(GoogleMapCircle circle, LatLon oldCenter) {
        if (isEnabled()) {
            circleCenterChangeRpc.centerChanged(circle.getId(), circle.getCenter());
        }
    }

    @Override
    public void mapRightClicked(LatLon position) {
        if (isEnabled()) {
            mapRightClickedRpc.mapRightClicked(position);
        }
    }

    @Override
    public void circleRightClicked(GoogleMapCircle circle) {
        if (isEnabled()) {
            circleRightClickedRpc.circleRightClicked(circle.getId());
        }
    }

    @Override
    public void markerRightClicked(GoogleMapMarker marker) {
        if (isEnabled()) {
            markerRightClickedRpc.markerRightClicked(marker.getId());
        }
    }

    @Override
    public void polygonRightClicked(GoogleMapPolygon polygon) {
        if (isEnabled()) {
            polygonRightClickedRpc.polygonRightClicked(polygon.getId());
        }
    }
}