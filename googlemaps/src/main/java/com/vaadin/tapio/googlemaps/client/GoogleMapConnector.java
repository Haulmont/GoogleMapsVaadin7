package com.vaadin.tapio.googlemaps.client;

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
import com.vaadin.tapio.googlemaps.client.services.DirectionsResult;
import com.vaadin.tapio.googlemaps.client.services.DirectionsStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Connect(GoogleMap.class)
public class GoogleMapConnector extends AbstractComponentContainerConnector implements
        MarkerClickListener, MarkerDoubleClickListener, MapMoveListener, MapClickListener,
        MarkerDragListener, InfoWindowClosedListener, PolygonCompleteListener, PolygonEditListener,
        MapInitListener, DirectionsResultHandler, CircleClickListener, CircleDoubleClickListener,
        CircleCompleteListener, CircleRadiusChangeListener, CircleCenterChangeListener, MapTypeChangeListener,
        PolygonClickListener {

    private static final long serialVersionUID = -357262975672050103L;

    public static boolean loadingApi = false;
    public static boolean apiLoaded = false;

    private final List<GoogleMapInitListener> initListeners = new ArrayList<GoogleMapInitListener>();

    private boolean deferred = false;
    private MarkerClickedRpc markerClickedRpc = RpcProxy.create(MarkerClickedRpc.class, this);
    private MarkerDoubleClickedRpc markerDoubleClickedRpc = RpcProxy.create(MarkerDoubleClickedRpc.class, this);
    private MapMovedRpc mapMovedRpc = RpcProxy.create(MapMovedRpc.class, this);
    private MapInitRpc mapInitRpc = RpcProxy.create(MapInitRpc.class, this);
    private MapClickedRpc mapClickRpc = RpcProxy.create(MapClickedRpc.class, this);
    private MarkerDraggedRpc markerDraggedRpc = RpcProxy.create(MarkerDraggedRpc.class, this);
    private InfoWindowClosedRpc infoWindowClosedRpc = RpcProxy.create(InfoWindowClosedRpc.class, this);
    private PolygonCompleteRpc polygonCompleteRpc = RpcProxy.create(PolygonCompleteRpc.class, this);
    private PolygonEditRpc polygonEditRpc = RpcProxy.create(PolygonEditRpc.class, this);
    private PolygonClickedRpc polygonClickedRpc = RpcProxy.create(PolygonClickedRpc.class, this);
    private HandleDirectionsResultRpc handleDirectionsResultRpc = RpcProxy.create(HandleDirectionsResultRpc.class, this);
    private CircleClickedRpc circleClickedRpc = RpcProxy.create(CircleClickedRpc.class, this);
    private CircleDoubleClickRpc circleDoubleClickRpc = RpcProxy.create(CircleDoubleClickRpc.class, this);
    private CircleCenterChangeRpc circleCenterChangeRpc = RpcProxy.create(CircleCenterChangeRpc.class, this);
    private CircleRadiusChangeRpc circleRadiusChangeRpc = RpcProxy.create(CircleRadiusChangeRpc.class, this);
    private CircleCompleteRpc circleCompleteRpc = RpcProxy.create(CircleCompleteRpc.class, this);
    private final MapTypeChangedRpc mapTypeChangedRpc = RpcProxy.create(MapTypeChangedRpc.class, this);

    public GoogleMapConnector() {
    }

    protected void initMap() {
        getWidget().initMap(getState().center, getState().zoom, getState().mapTypeId, this);
        getWidget().setMarkerClickListener(this);
        getWidget().setMarkerDoubleClickListener(this);
        getWidget().setMapMoveListener(this);
        getWidget().setMapClickListener(this);
        getWidget().setMarkerDragListener(this);
        getWidget().setInfoWindowClosedListener(this);
        getWidget().setMapTypeChangeListener(this);
        getWidget().setPolygonCompleteListener(this);
        getWidget().setPolygonEditListener(this);
        getWidget().setPolygonClickListener(this);
        getWidget().setDirectionsResultHandler(this);
        getWidget().setCircleClickListener(this);
        getWidget().setCircleDoubleClickListener(this);
        getWidget().setCircleCompleteListener(this);
        getWidget().setCircleCenterChangeListener(this);
        getWidget().setCircleRadiusChangeListener(this);
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

        load(onLoad, loadLibraries, language, params);
    }

    private static void load(Runnable onLoad, ArrayList<LoadApi.LoadLibrary> loadLibraries, LoadApi.Language language, String otherParams) {
        String op = "";
        if(otherParams != null) {
            op = op + "&" + otherParams;
        }

        if(loadLibraries != null) {
            op = op + "&" + getLibraries(loadLibraries);
        }

        if(language != null) {
            op = op + "&language=" + language.getValue();
        }

        AjaxLoader.AjaxLoaderOptions settings = AjaxLoader.AjaxLoaderOptions.newInstance();
        settings.setOtherParms(op);
        AjaxLoader.loadApi("maps", "3.25", onLoad, settings);
    }

    private static String getLibraries(ArrayList<LoadApi.LoadLibrary> loadLibraries) {
        if(loadLibraries == null) {
            return "";
        } else {
            String s = "libraries=";
            Iterator itr = loadLibraries.iterator();
            int i = 0;

            while(itr.hasNext()) {
                LoadApi.LoadLibrary ll = (LoadApi.LoadLibrary)itr.next();
                if(ll != null) {
                    if(i > 0) {
                        s = s + ",";
                    }

                    s = s + ll.value();
                    ++i;
                }
            }

            return s;
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
        getWidget().setCircleOverlays(getState().circles);
        getWidget().setKmlLayers(getState().kmlLayers);
        getWidget().setHeatMapLayers(getState().heatMapLayers);
        getWidget().setImageMapTypes(getState().imageMapTypes);
        getWidget().setOverlayImageMapTypes(getState().overlayImageMapTypes);
        getWidget().setInfoWindows(getState().infoWindows.values());
        getWidget().setMapTypes(getState().mapTypeIds);
        getWidget().setMapType(getState().mapTypeId);
        getWidget().setControls(getState().controls);
        getWidget().setDraggable(getState().draggable);
        getWidget().setKeyboardShortcutsEnabled(
                getState().keyboardShortcutsEnabled);
        getWidget().setScrollWheelEnabled(getState().scrollWheelEnabled);
        getWidget().setMinZoom(getState().minZoom);
        getWidget().setMaxZoom(getState().maxZoom);
        getWidget().setDrawingOptions(getState().drawingOptions);
        getWidget().processDirectionRequests(getState().directionsRequests.values());
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
    public void markerDoubleClicked(GoogleMapMarker clickedMarker) {
        markerDoubleClickedRpc.markerClicked(clickedMarker.getId());
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

    @Override
    public void polygonClicked(GoogleMapPolygon polygon) {
        polygonClickedRpc.polygonClicked(polygon.getId());
    }

    @Override
    public void handle(long requestId, DirectionsResult result, DirectionsStatus status) {
        handleDirectionsResultRpc.handle(result, status, requestId);
    }

    @Override
    public void radiusChange(GoogleMapCircle circle, double oldRadius) {
        circleRadiusChangeRpc.radiusChanged(circle.getId(), circle.getRadius());
    }

    @Override
    public void circleDoubleClicked(GoogleMapCircle circle) {
        circleDoubleClickRpc.circleDoubleClicked(circle.getId());
    }

    @Override
    public void circleComplete(GoogleMapCircle circle) {
        circleCompleteRpc.circleComplete(circle);
    }

    @Override
    public void circleClicked(GoogleMapCircle clickedCircle) {
        circleClickedRpc.circleClicked(clickedCircle.getId());
    }

    @Override
    public void centerChanged(GoogleMapCircle circle, LatLon oldCenter) {
        circleCenterChangeRpc.centerChanged(circle.getId(), circle.getCenter());
    }
}
