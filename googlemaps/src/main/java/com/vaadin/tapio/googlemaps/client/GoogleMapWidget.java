package com.vaadin.tapio.googlemaps.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.maps.client.MapImpl;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.base.Size;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.drawinglib.DrawingControlOptions;
import com.google.gwt.maps.client.drawinglib.DrawingManager;
import com.google.gwt.maps.client.drawinglib.DrawingManagerOptions;
import com.google.gwt.maps.client.drawinglib.OverlayType;
import com.google.gwt.maps.client.events.center.CenterChangeMapEvent;
import com.google.gwt.maps.client.events.center.CenterChangeMapHandler;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.events.closeclick.CloseClickMapEvent;
import com.google.gwt.maps.client.events.closeclick.CloseClickMapHandler;
import com.google.gwt.maps.client.events.dragend.DragEndMapEvent;
import com.google.gwt.maps.client.events.dragend.DragEndMapHandler;
import com.google.gwt.maps.client.events.idle.IdleMapEvent;
import com.google.gwt.maps.client.events.idle.IdleMapHandler;
import com.google.gwt.maps.client.events.insertat.InsertAtMapEvent;
import com.google.gwt.maps.client.events.insertat.InsertAtMapHandler;
import com.google.gwt.maps.client.events.overlaycomplete.polygon.PolygonCompleteMapEvent;
import com.google.gwt.maps.client.events.removeat.RemoveAtMapEvent;
import com.google.gwt.maps.client.events.removeat.RemoveAtMapHandler;
import com.google.gwt.maps.client.events.setat.SetAtMapEvent;
import com.google.gwt.maps.client.events.setat.SetAtMapHandler;
import com.google.gwt.maps.client.events.tiles.TilesLoadedMapEvent;
import com.google.gwt.maps.client.events.tiles.TilesLoadedMapHandler;
import com.google.gwt.maps.client.layers.KmlLayer;
import com.google.gwt.maps.client.layers.KmlLayerOptions;
import com.google.gwt.maps.client.mvc.MVCArray;
import com.google.gwt.maps.client.overlays.*;
import com.google.gwt.maps.client.visualizationlib.HeatMapLayer;
import com.google.gwt.maps.client.visualizationlib.HeatMapLayerOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.vaadin.tapio.googlemaps.client.drawing.DrawingOptions;
import com.vaadin.tapio.googlemaps.client.events.*;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapHeatMapLayer;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapKmlLayer;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;

import java.util.*;

public class GoogleMapWidget extends FlowPanel implements RequiresResize {

    public static final String CLASSNAME = "googlemap";

    private MapWidget map;
    private MapOptions mapOptions;
    private Map<Marker, GoogleMapMarker> markerMap = new HashMap<Marker, GoogleMapMarker>();
    private Map<GoogleMapMarker, Marker> gmMarkerMap = new HashMap<GoogleMapMarker, Marker>();
    private Map<Polygon, GoogleMapPolygon> polygonMap = new HashMap<Polygon, GoogleMapPolygon>();
    private Map<Polyline, GoogleMapPolyline> polylineMap = new HashMap<Polyline, GoogleMapPolyline>();
    private Map<InfoWindow, GoogleMapInfoWindow> infoWindowMap = new HashMap<InfoWindow, GoogleMapInfoWindow>();
    private Map<KmlLayer, GoogleMapKmlLayer> kmlLayerMap = new HashMap<KmlLayer, GoogleMapKmlLayer>();
    private Map<HeatMapLayer, GoogleMapHeatMapLayer> heatMapLayerMap = new HashMap<HeatMapLayer, GoogleMapHeatMapLayer>();

    private MarkerClickListener markerClickListener = null;
    private MarkerDragListener markerDragListener = null;
    private InfoWindowClosedListener infoWindowClosedListener = null;
    private PolygonCompleteListener polygonCompleteListener = null;
    private PolygonEditListener polygonEditListener = null;

    protected DrawingManager drawingManager;
    private MapMoveListener mapMoveListener = null;
    private LatLngBounds allowedBoundsCenter = null;
    private LatLngBounds allowedBoundsVisibleArea = null;

    private MapClickListener mapClickListener = null;

    private LatLng center = null;
    private int zoom = 0;
    private boolean forceBoundUpdate = false;
    private boolean initListenerNotified = false;

    public GoogleMapWidget() {
        setStyleName(CLASSNAME);
    }

    public void initMap(LatLon center, int zoom, String mapTypeId, final MapInitListener initListener) {
        this.center = LatLng.newInstance(center.getLat(), center.getLon());
        this.zoom = zoom;

        mapOptions = MapOptions.newInstance();
        mapOptions.setMapTypeId(MapTypeId.fromValue(mapTypeId.toLowerCase()));
        mapOptions.setCenter(this.center);
        mapOptions.setZoom(this.zoom);
        final MapImpl mapImpl = MapImpl.newInstance(getElement(), mapOptions);
        mapImpl.addTilesLoadedHandler(new TilesLoadedMapHandler() {
            @Override
            public void onEvent(TilesLoadedMapEvent event) {
                if (!initListenerNotified) {
                    //call map init listener once
                    LatLon center = getCenter(mapImpl);
                    LatLon boundNE = getBoundNE(mapImpl);
                    LatLon boundSW = getBoundSW(mapImpl);
                    initListener.init(center, mapImpl.getZoom(), boundNE, boundSW);
                    initListenerNotified = true;
                }
            }
        });

        map = MapWidget.newInstance(mapImpl);
        // always when center has changed, check that it does not go out from
        // the given bounds
        map.addCenterChangeHandler(new CenterChangeMapHandler() {
            @Override
            public void onEvent(CenterChangeMapEvent event) {
                forceBoundUpdate = checkVisibleAreaBoundLimits();
                forceBoundUpdate = checkCenterBoundLimits();
            }
        });

        // do all updates when the map has stopped moving
        mapImpl.addIdleHandler(new IdleMapHandler() {
            @Override
            public void onEvent(IdleMapEvent event) {
                //scheduling due to vaadin 7.2 bug: http://dev.vaadin.com/ticket/14164
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        updateBounds(forceBoundUpdate);
                    }
                });
            }
        });

        mapImpl.addClickHandler(new ClickMapHandler() {
            @Override
            public void onEvent(ClickMapEvent event) {
                if (mapClickListener != null) {
                    LatLng latLng = event.getMouseEvent().getLatLng();
                    LatLon position = new LatLon(latLng.getLatitude(),
                            latLng.getLongitude());
                    mapClickListener.mapClicked(position);
                }
            }
        });
    }

    private LatLon getCenter(MapImpl mapImpl) {
        return new LatLon(mapImpl.getCenter().getLatitude(),
                mapImpl.getCenter().getLongitude());
    }

    private LatLon getBoundSW(MapImpl mapImpl) {
        return new LatLon(mapImpl.getBounds().getSouthWest().getLatitude(),
                mapImpl.getBounds().getSouthWest().getLongitude());
    }

    private LatLon getBoundNE(MapImpl mapImpl) {
        return new LatLon(mapImpl.getBounds().getNorthEast().getLatitude(),
                mapImpl.getBounds().getNorthEast().getLongitude());
    }

    private boolean checkVisibleAreaBoundLimits() {
        if (allowedBoundsVisibleArea == null) {
            return false;
        }
        double newCenterLat = map.getCenter().getLatitude();
        double newCenterLng = map.getCenter().getLongitude();

        LatLng mapNE = map.getBounds().getNorthEast();
        LatLng mapSW = map.getBounds().getSouthWest();

        LatLng limitNE = allowedBoundsVisibleArea.getNorthEast();
        LatLng limitSW = allowedBoundsVisibleArea.getSouthWest();

        double mapWidth = mapNE.getLongitude() - mapSW.getLongitude();
        double mapHeight = mapNE.getLatitude() - mapSW.getLatitude();

        double maxWidth = limitNE.getLongitude() - limitSW.getLongitude();
        double maxHeight = limitNE.getLatitude() - limitSW.getLatitude();

        if (mapWidth > maxWidth) {
            newCenterLng = allowedBoundsVisibleArea.getCenter().getLongitude();
        } else if (mapNE.getLongitude() > limitNE.getLongitude()) {
            newCenterLng -= (mapNE.getLongitude() - limitNE.getLongitude());
        } else if (mapSW.getLongitude() < limitSW.getLongitude()) {
            newCenterLng += (limitSW.getLongitude() - mapSW.getLongitude());
        }

        if (mapHeight > maxHeight) {
            newCenterLat = allowedBoundsVisibleArea.getCenter().getLatitude();
        } else if (mapNE.getLatitude() > limitNE.getLatitude()) {
            newCenterLat -= (mapNE.getLatitude() - limitNE.getLatitude());
        } else if (mapSW.getLatitude() < limitSW.getLatitude()) {
            newCenterLat += (limitSW.getLatitude() - mapSW.getLatitude());
        }

        if (newCenterLat != map.getCenter().getLatitude()
                || newCenterLng != map.getCenter().getLongitude()) {
            setCenter(new LatLon(newCenterLat, newCenterLng));
            return true;
        }

        return false;
    }

    private void updateBounds(boolean forceUpdate) {
        if (forceUpdate || zoom != map.getZoom() || center == null
                || center.getLatitude() != map.getCenter().getLatitude()
                || center.getLongitude() != map.getCenter().getLongitude()) {
            zoom = map.getZoom();
            center = map.getCenter();
            mapOptions.setZoom(zoom);
            mapOptions.setCenter(center);

            if (mapMoveListener != null) {
                mapMoveListener.mapMoved(map.getZoom(), getCenter(map),
                        getBoundNE(map), getBoundSW(map));
            }
        }
    }

    private LatLon getCenter(MapWidget map) {
        return new LatLon(map.getCenter().getLatitude(), map.getCenter().getLongitude());
    }

    private LatLon getBoundSW(MapWidget map) {
        return new LatLon(map.getBounds().getSouthWest().getLatitude(), map.getBounds()
                .getSouthWest().getLongitude());
    }

    private LatLon getBoundNE(MapWidget map) {
        return new LatLon(map.getBounds().getNorthEast().getLatitude(), map.getBounds()
                .getNorthEast().getLongitude());
    }

    private boolean checkCenterBoundLimits() {
        LatLng center = map.getCenter();
        if (allowedBoundsCenter == null || allowedBoundsCenter.contains(center)) {
            return false;
        }
        double lat = center.getLatitude();
        double lng = center.getLongitude();

        LatLng nortEast = allowedBoundsCenter.getNorthEast();
        LatLng southWest = allowedBoundsCenter.getSouthWest();
        if (lat > nortEast.getLatitude()) {
            lat = nortEast.getLatitude();
        }
        if (lng > nortEast.getLongitude()) {
            lng = nortEast.getLongitude();
        }
        if (lat < southWest.getLatitude()) {
            lat = southWest.getLatitude();
        }
        if (lng < southWest.getLongitude()) {
            lng = southWest.getLongitude();
        }

        setCenter(new LatLon(lat, lng));
        return true;
    }

    public boolean isMapInitiated() {
        return !(map == null);
    }

    public void setCenter(LatLon center) {
        this.center = LatLng.newInstance(center.getLat(), center.getLon());
        mapOptions.setZoom(map.getZoom());
        mapOptions.setCenter(this.center);
        map.panTo(this.center);
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
        mapOptions.setZoom(this.zoom);
        map.setZoom(this.zoom);
    }

    public void setMarkers(Collection<GoogleMapMarker> markers) {

        // clear removed markers
        for (Marker marker : markerMap.keySet()) {
            GoogleMapMarker gMapMarker = markerMap.get(marker);
            if (!markers.contains(gMapMarker)) {
                marker.setMap((MapWidget) null);
                gmMarkerMap.remove(gMapMarker);
                markerMap.remove(marker);
            }
        }

        for (GoogleMapMarker googleMapMarker : markers) {
            if (!gmMarkerMap.containsKey(googleMapMarker)) {

                final Marker marker = addMarker(googleMapMarker);
                markerMap.put(marker, googleMapMarker);
                gmMarkerMap.put(googleMapMarker, marker);

                marker.addClickHandler(new ClickMapHandler() {
                    @Override
                    public void onEvent(ClickMapEvent event) {
                        if (markerClickListener != null) {
                            markerClickListener.markerClicked(markerMap
                                    .get(marker));
                        }
                    }
                });
                marker.addDragEndHandler(new DragEndMapHandler() {
                    @Override
                    public void onEvent(DragEndMapEvent event) {
                        GoogleMapMarker gMarker = markerMap.get(marker);
                        LatLon oldPosition = gMarker.getPosition();
                        gMarker.setPosition(new LatLon(marker.getPosition()
                                .getLatitude(), marker.getPosition().getLongitude()));

                        if (markerDragListener != null) {
                            markerDragListener.markerDragged(gMarker,
                                    oldPosition);
                        }
                    }
                });
            } else {
                updateMarker(googleMapMarker);
            }
        }
    }

    private void updateMarker(GoogleMapMarker googleMapMarker) {
        Marker marker = gmMarkerMap.get(googleMapMarker);
        GoogleMapMarker oldGmMarker = markerMap.get(marker);

        if (!oldGmMarker.hasSameFieldValues(googleMapMarker)) {
            MarkerOptions options = createMarkerOptions(googleMapMarker);
            marker.setOptions(options);
        }

        gmMarkerMap.put(googleMapMarker, marker);
        markerMap.put(marker, googleMapMarker);
    }

    public void setMarkerClickListener(MarkerClickListener listener) {
        markerClickListener = listener;
    }

    public void setMapMoveListener(MapMoveListener listener) {
        mapMoveListener = listener;
    }

    public void setMapClickListener(MapClickListener listener) {
        mapClickListener = listener;
    }

    public void setMarkerDragListener(MarkerDragListener listener) {
        markerDragListener = listener;
    }

    public void setInfoWindowClosedListener(InfoWindowClosedListener listener) {
        infoWindowClosedListener = listener;
    }

    public void setPolygonCompleteListener(PolygonCompleteListener listener) {
        polygonCompleteListener = listener;
    }

    public void setPolygonEditListener(PolygonEditListener listener) {
        polygonEditListener = listener;
    }

    private Marker addMarker(GoogleMapMarker googleMapMarker) {
        MarkerOptions options = createMarkerOptions(googleMapMarker);

        final Marker marker = Marker.newInstance(options);
        marker.setMap(map);

        return marker;
    }

    private MarkerOptions createMarkerOptions(GoogleMapMarker googleMapMarker) {
        LatLng center = LatLng.newInstance(googleMapMarker.getPosition().getLat(),
                googleMapMarker.getPosition().getLon());
        MarkerOptions options = MarkerOptions.newInstance();
        options.setPosition(center);
        options.setTitle(googleMapMarker.getCaption());
        options.setDraggable(googleMapMarker.isDraggable());
        options.setOptimized(googleMapMarker.isOptimized());

        if (googleMapMarker.isAnimationEnabled()) {
            options.setAnimation(Animation.DROP);
        }

        if (googleMapMarker.getIconUrl() != null) {
            options.setIcon(googleMapMarker.getIconUrl());
        }
        return options;
    }

    public double getZoom() {
        return map.getZoom();
    }

    public double getLatitude() {
        return map.getCenter().getLatitude();
    }

    public double getLongitude() {
        return map.getCenter().getLongitude();
    }

    public void setCenterBoundLimits(LatLon limitNE, LatLon limitSW) {
        allowedBoundsCenter = LatLngBounds.newInstance(
                LatLng.newInstance(limitSW.getLat(), limitSW.getLon()),
                LatLng.newInstance(limitNE.getLat(), limitNE.getLon()));
    }

    public void clearCenterBoundLimits() {
        allowedBoundsCenter = null;
    }

    public void setVisibleAreaBoundLimits(LatLon limitNE, LatLon limitSW) {
        allowedBoundsVisibleArea = LatLngBounds.newInstance(
                LatLng.newInstance(limitSW.getLat(), limitSW.getLon()),
                LatLng.newInstance(limitNE.getLat(), limitNE.getLon()));
    }

    public void clearVisibleAreaBoundLimits() {
        allowedBoundsVisibleArea = null;
    }

    public void setPolygonOverlays(Map<Long, GoogleMapPolygon> polyOverlays) {
        for (Polygon polygon : polygonMap.keySet()) {
            polygon.setMap((MapWidget) null);
        }
        polygonMap.clear();

        for (GoogleMapPolygon overlay : polyOverlays.values()) {
            final MVCArray<LatLng> points = MVCArray.newInstance();
            for (LatLon latLon : overlay.getCoordinates()) {
                LatLng latLng = LatLng.newInstance(latLon.getLat(),
                        latLon.getLon());
                points.push(latLng);
            }

            PolygonOptions options = PolygonOptions.newInstance();
            options.setFillColor(overlay.getFillColor());
            options.setFillOpacity(overlay.getFillOpacity());
            options.setGeodesic(overlay.isGeodesic());
            options.setStrokeColor(overlay.getStrokeColor());
            options.setStrokeOpacity(overlay.getStrokeOpacity());
            options.setStrokeWeight(overlay.getStrokeWeight());
            options.setZindex(overlay.getzIndex());

            Polygon polygon = Polygon.newInstance(options);
            polygon.setPath(points);
            polygon.setMap(map);
            polygon.setEditable(overlay.isEditable());
            attachPolygonEditListeners(polygon, overlay);
            polygonMap.put(polygon, overlay);
        }

    }

    public void setPolylineOverlays(Set<GoogleMapPolyline> polylineOverlays) {
        for (Polyline polyline : polylineMap.keySet()) {
            polyline.setMap((MapWidget) null);
        }
        polylineMap.clear();

        for (GoogleMapPolyline overlay : polylineOverlays) {
            MVCArray<LatLng> points = MVCArray.newInstance();
            for (LatLon latLon : overlay.getCoordinates()) {
                LatLng latLng = LatLng.newInstance(latLon.getLat(), latLon.getLon());
                points.push(latLng);
            }

            PolylineOptions options = PolylineOptions.newInstance();
            options.setGeodesic(overlay.isGeodesic());
            options.setStrokeColor(overlay.getStrokeColor());
            options.setStrokeOpacity(overlay.getStrokeOpacity());
            options.setStrokeWeight(overlay.getStrokeWeight());
            options.setZindex(overlay.getzIndex());

            Polyline polyline = Polyline.newInstance(options);
            polyline.setPath(points);
            polyline.setMap(map);

            polylineMap.put(polyline, overlay);
        }
    }

    public void setKmlLayers(Collection<GoogleMapKmlLayer> layers) {
        for (KmlLayer kmlLayer : kmlLayerMap.keySet()) {
            kmlLayer.setMap((MapWidget) null);
        }
        kmlLayerMap.clear();

        for (GoogleMapKmlLayer gmLayer : layers) {
            KmlLayerOptions options = KmlLayerOptions.newInstance();
            options.setClickable(gmLayer.isClickable());
            options.setPreserveViewport(gmLayer.isViewportPreserved());
            options.setSuppressInfoWindows(gmLayer
                    .isInfoWindowRenderingDisabled());

            KmlLayer kmlLayer = KmlLayer.newInstance(gmLayer.getUrl(), options);
            kmlLayer.setMap(map);

            kmlLayerMap.put(kmlLayer, gmLayer);
        }
    }

    public void setHeatMapLayers(Collection<GoogleMapHeatMapLayer> layers) {
        for (HeatMapLayer heatMapLayer : heatMapLayerMap.keySet()) {
            heatMapLayer.setMap(null);
        }
        heatMapLayerMap.clear();
        
        for (GoogleMapHeatMapLayer heatMapLayer : layers) {
            HeatMapLayerOptions options = HeatMapLayerOptions.newInstance();

            if (heatMapLayer.getDissipating() != null) {
                options.setDissipating(heatMapLayer.getDissipating());
            }
            if (heatMapLayer.getMaxIntensity() != null) {
                options.setMaxIntensity(heatMapLayer.getMaxIntensity());
            }
            if (heatMapLayer.getOpacity() != null) {
                options.setOpacity(heatMapLayer.getOpacity());
            }
            if (heatMapLayer.getRadius() != null) {
                options.setRadius(heatMapLayer.getRadius());
            }

            if (heatMapLayer.getGradient() != null && !heatMapLayer.getGradient().isEmpty()) {
                JsArrayString gradient = JsArrayString.createArray().cast();
                for (String color : heatMapLayer.getGradient()) {
                    gradient.push(color);
                }
                options.setGradient(gradient);
            }
            HeatMapLayer layer = HeatMapLayer.newInstance(options);

            if (heatMapLayer.getData() != null && !heatMapLayer.getData().isEmpty()) {
                MVCArray<LatLng> data = MVCArray.newInstance();
                for (LatLon latLon : heatMapLayer.getData()) {
                    data.push(LatLng.newInstance(latLon.getLat(), latLon.getLon()));
                }
                layer.setData(data);
            } else if (heatMapLayer.getWeightedData() != null
                    && !heatMapLayer.getWeightedData().isEmpty()) {
                MVCArray<com.google.gwt.maps.client.visualizationlib.WeightedLocation> weightedData
                        = MVCArray.newInstance();
                for (WeightedLocation location : heatMapLayer.getWeightedData()) {
                    LatLng latLng = LatLng.newInstance(location.getLocation().getLat(),
                            location.getLocation().getLon());
                    weightedData.push(com.google.gwt.maps.client.visualizationlib
                            .WeightedLocation.newInstance(latLng, location.getWeight()));
                }
                layer.setDataWeighted(weightedData);
            } else {
                layer.setData(MVCArray.<LatLng>newInstance());
            }

            layer.setMap(map);
            heatMapLayerMap.put(layer, heatMapLayer);
        }
    }

    public void setMapType(String mapTypeId) {
        mapOptions.setMapTypeId(MapTypeId.fromValue(mapTypeId.toLowerCase()));
        map.setOptions(mapOptions);
    }

    public void setControls(Set<GoogleMapControl> controls) {
        mapOptions.setMapTypeControl(controls
                .contains(GoogleMapControl.MapType));
        mapOptions.setOverviewMapControl(controls
                .contains(GoogleMapControl.OverView));
        mapOptions.setPanControl(controls.contains(GoogleMapControl.Pan));
        mapOptions.setRotateControl(controls.contains(GoogleMapControl.Rotate));
        mapOptions.setScaleControl(controls.contains(GoogleMapControl.Scale));
        mapOptions.setStreetViewControl(controls
                .contains(GoogleMapControl.StreetView));
        mapOptions.setZoomControl(controls.contains(GoogleMapControl.Zoom));

        map.setOptions(mapOptions);
    }

    public void setDraggable(boolean draggable) {
        mapOptions.setDraggable(draggable);
        map.setOptions(mapOptions);
    }

    public void setKeyboardShortcutsEnabled(boolean keyboardShortcutsEnabled) {
        mapOptions.setKeyboardShortcuts(keyboardShortcutsEnabled);
        map.setOptions(mapOptions);
    }

    public void setScrollWheelEnabled(boolean scrollWheelEnabled) {
        mapOptions.setScrollWheel(scrollWheelEnabled);
        map.setOptions(mapOptions);
    }

    public void setMinZoom(int minZoom) {
        mapOptions.setMinZoom(minZoom);
        map.setOptions(mapOptions);
    }

    public void setMaxZoom(int maxZoom) {
        mapOptions.setMaxZoom(maxZoom);
        map.setOptions(mapOptions);
    }

    public MapWidget getMap() {
        return map;
    }

    public void triggerResize() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                map.triggerResize();
                map.setCenter(center);
            }
        };
        timer.schedule(20);
    }

    public void setInfoWindows(Collection<GoogleMapInfoWindow> infoWindows) {
        for (InfoWindow window : infoWindowMap.keySet()) {
            window.close();
        }
        infoWindowMap.clear();

        for (GoogleMapInfoWindow gmWindow : infoWindows) {
            InfoWindowOptions options = InfoWindowOptions.newInstance();
            String contents = gmWindow.getContent();

            // wrap the contents inside a div if there's a defined width or
            // height
            if (gmWindow.getHeight() != null || gmWindow.getWidth() != null) {
                StringBuffer contentWrapper = new StringBuffer("<div style=\"");
                if (gmWindow.getWidth() != null) {
                    contentWrapper.append("width:");
                    contentWrapper.append(gmWindow.getWidth());
                    contentWrapper.append(";");
                }
                if (gmWindow.getHeight() != null) {
                    contentWrapper.append("height:");
                    contentWrapper.append(gmWindow.getHeight());
                    contentWrapper.append(";");
                }
                contentWrapper.append("\" >");
                contentWrapper.append(contents);
                contentWrapper.append("</div>");
                contents = contentWrapper.toString();
            }

            options.setContent(contents);
            options.setDisableAutoPan(gmWindow.isAutoPanDisabled());
            if (gmWindow.getMaxWidth() != null) {
                options.setMaxWidth(gmWindow.getMaxWidth());
            }
            if (gmWindow.getPixelOffsetHeight() != null
                    && gmWindow.getPixelOffsetWidth() != null) {
                options.setPixelOffet(Size.newInstance(
                        gmWindow.getPixelOffsetWidth(),
                        gmWindow.getPixelOffsetHeight()));
            }
            if (gmWindow.getPosition() != null) {
                options.setPosition(LatLng.newInstance(gmWindow.getPosition()
                        .getLat(), gmWindow.getPosition().getLon()));
            }
            if (gmWindow.getzIndex() != null) {
                options.setZindex(gmWindow.getzIndex());
            }
            final InfoWindow window = InfoWindow.newInstance(options);
            if (gmMarkerMap.containsKey(gmWindow.getAnchorMarker())) {
                window.open(map, gmMarkerMap.get(gmWindow.getAnchorMarker()));
            } else {
                window.open(map);
            }
            infoWindowMap.put(window, gmWindow);

            window.addCloseClickHandler(new CloseClickMapHandler() {

                @Override
                public void onEvent(CloseClickMapEvent event) {
                    if (infoWindowClosedListener != null) {
                        infoWindowClosedListener.infoWindowClosed(infoWindowMap
                                .get(window));
                    }
                }
            });

        }
    }

    public void fitToBounds(LatLon boundsNE, LatLon boundsSW) {
        LatLng ne = LatLng.newInstance(boundsNE.getLat(), boundsNE.getLon());
        LatLng sw = LatLng.newInstance(boundsSW.getLat(), boundsSW.getLon());

        LatLngBounds bounds = LatLngBounds.newInstance(sw, ne);
        map.fitBounds(bounds);
        updateBounds(false);
    }

    public native void setVisualRefreshEnabled(boolean enabled)
    /*-{
        $wnd.google.maps.visualRefresh = enabled;
    }-*/;

    @Override
    public void onResize() {
        triggerResize();
    }

    public DrawingManager getDrawingManager() {
        return drawingManager;
    }

    public void setDrawingOptions(DrawingOptions vOptions) {
        if (vOptions == null) {
            if (drawingManager != null) {
                drawingManager.setMap(null);
                drawingManager = null;
            }
            return;
        }

        DrawingManagerOptions options = toDrawingManagerOptions(vOptions);

        final com.vaadin.tapio.googlemaps.client.drawing.PolygonOptions
                vPolygonOptions = vOptions.getPolygonOptions();
        options.setPolygonOptions(toPolygonOptions(vPolygonOptions));
        drawingManager = DrawingManager.newInstance(options);
        drawingManager.setMap(map);

        drawingManager.addPolygonCompleteHandler(
                new PolygonCompleteMapHandler(vPolygonOptions));
    }

    private DrawingManagerOptions toDrawingManagerOptions(DrawingOptions drawingOptions) {
        com.vaadin.tapio.googlemaps.client.drawing.DrawingControlOptions
                vControlOptions = drawingOptions.getDrawingControlOptions();
        DrawingControlOptions controlOptions = DrawingControlOptions.newInstance();

        ControlPosition cp = toControlPosition(vControlOptions.getPosition());
        if (cp != null) {
            controlOptions.setPosition(cp);
        }

        if (!vControlOptions.getDrawingModes().isEmpty()) {
            List<com.vaadin.tapio.googlemaps.client.drawing.OverlayType>
                    vDrawingModes = vControlOptions.getDrawingModes();

            int drawingModesNum = vDrawingModes.size();
            OverlayType[] drawingModes = new OverlayType[drawingModesNum];
            for (int i = 0; i < drawingModesNum; i++) {
                OverlayType ot = toOverlayType(vDrawingModes.get(i));
                if (ot != null) {
                    drawingModes[i] = ot;
                }
            }
            controlOptions.setDrawingModes(drawingModes);
        }

        DrawingManagerOptions options = DrawingManagerOptions.newInstance();
        options.setDrawingControlOptions(controlOptions);
        options.setDrawingControl(drawingOptions.isEnableDrawingControl());

        OverlayType ot = toOverlayType(drawingOptions.getInitialDrawingMode());
        if (ot != null) {
            options.setDrawingMode(ot);
        }

        return options;
    }

    private void attachPolygonEditListeners(final Polygon polygon,
            final GoogleMapPolygon vPolygon) {
        MVCArray path = polygon.getPath();
        if (path != null) {
            path.addInsertAtHandler(new InsertAtMapHandler() {
                @Override
                public void onEvent(InsertAtMapEvent event) {
                    firePolygonEdited(polygon, vPolygon, event.getIndex(),
                            PolygonEditListener.ActionType.INSERT);
                }
            });
            path.addSetAtHandler(new SetAtMapHandler() {
                @Override
                public void onEvent(SetAtMapEvent event) {
                    firePolygonEdited(polygon, vPolygon, event.getIndex(),
                            PolygonEditListener.ActionType.SET);
                }
            });
            path.addRemoveAtHandler(new RemoveAtMapHandler() {
                @Override
                public void onEvent(RemoveAtMapEvent event) {
                    firePolygonEdited(polygon, vPolygon, event.getIndex(),
                            PolygonEditListener.ActionType.REMOVE);
                }
            });
        }
    }

    private void firePolygonEdited(Polygon polygon, GoogleMapPolygon
            vPolygon, int idx, PolygonEditListener.ActionType action) {
        LatLng latLng = polygon.getPath().get(idx);
        polygonEditListener.polygonEdited(vPolygon, action, idx,
                new LatLon(latLng.getLatitude(), latLng.getLongitude()));
    }

    private PolygonOptions toPolygonOptions(
            com.vaadin.tapio.googlemaps.client.drawing.PolygonOptions vOptions) {
        PolygonOptions options = PolygonOptions.newInstance();
        options.setEditable(vOptions.isEditable());
        options.setClickable(vOptions.isClickable());
        options.setFillColor(vOptions.getFillColor());
        options.setFillOpacity(vOptions.getFillOpacity());
        options.setGeodesic(vOptions.isGeodesic());
        options.setStrokeColor(vOptions.getStrokeColor());
        options.setStrokeOpacity(vOptions.getStrokeOpacity());
        options.setStrokeWeight(vOptions.getStrokeWeight());
        options.setVisible(vOptions.isVisible());
        options.setZindex(vOptions.getZIndex());
        return options;
    }

    private OverlayType toOverlayType(
            com.vaadin.tapio.googlemaps.client.drawing.OverlayType vOverlayType) {
        if (vOverlayType == null) {
            return null;
        }

        switch (vOverlayType) {
            case POLYGON: return OverlayType.POLYGON;
            case CIRCLE: return OverlayType.POLYGON;
            case MARKER: return OverlayType.POLYGON;
            case POLYLINE: return OverlayType.POLYGON;
            case RECTANGLE: return OverlayType.POLYGON;
            default: return null;
        }
    }

    private ControlPosition toControlPosition(
            com.vaadin.tapio.googlemaps.client.drawing.ControlPosition vPosition) {
        if (vPosition == null) {
            return null;
        }

        switch (vPosition) {
            case BOTTOM_CENTER: return ControlPosition.BOTTOM_CENTER;
            case BOTTOM_LEFT: return ControlPosition.BOTTOM_LEFT;
            case BOTTOM_RIGHT: return ControlPosition.BOTTOM_RIGHT;
            case TOP_CENTER: return ControlPosition.TOP_CENTER;
            case TOP_LEFT: return ControlPosition.TOP_LEFT;
            case TOP_RIGHT: return ControlPosition.TOP_RIGHT;
            case LEFT_CENTER: return ControlPosition.LEFT_CENTER;
            case LEFT_TOP: return ControlPosition.LEFT_TOP;
            case LEFT_BOTTOM: return ControlPosition.LEFT_BOTTOM;
            case RIGHT_CENTER: return ControlPosition.RIGHT_CENTER;
            case RIGHT_TOP: return ControlPosition.RIGHT_TOP;
            case RIGHT_BOTTOM: return ControlPosition.RIGHT_BOTTOM;
            default: return null;
        }
    }

    private class PolygonCompleteMapHandler implements
            com.google.gwt.maps.client.events.overlaycomplete.polygon.PolygonCompleteMapHandler {

        private final com.vaadin.tapio.googlemaps.client.drawing.PolygonOptions polygonOptions;

        public PolygonCompleteMapHandler(com.vaadin.tapio.googlemaps.client.drawing.PolygonOptions polygonOptions) {
            this.polygonOptions = polygonOptions;
        }

        @Override
        public void onEvent(PolygonCompleteMapEvent event) {
            Polygon polygon = event.getPolygon();

            JsArray<LatLng> polygonCoordinates = polygon.getPath().getArray();
            List<LatLon> googlePolygonCoordinates =
                    new ArrayList<LatLon>(polygonCoordinates.length() * 2);

            for (int i = 0; i < polygonCoordinates.length(); i++) {
                LatLng latLng = polygonCoordinates.get(i);
                googlePolygonCoordinates.add(new LatLon(latLng.getLatitude(),
                        latLng.getLongitude()));
            }

            GoogleMapPolygon vPolygon = new GoogleMapPolygon();
            vPolygon.setCoordinates(googlePolygonCoordinates);

            if (polygonOptions != null) {
                vPolygon.setFillColor(polygonOptions.getFillColor());
                vPolygon.setFillOpacity(polygonOptions.getFillOpacity());
                vPolygon.setGeodesic(polygonOptions.isGeodesic());
                vPolygon.setStrokeColor(polygonOptions.getStrokeColor());
                vPolygon.setStrokeOpacity(polygonOptions.getStrokeOpacity());
                vPolygon.setStrokeWeight(polygonOptions.getStrokeWeight());
                vPolygon.setStrokeColor(polygonOptions.getStrokeColor());
                vPolygon.setzIndex(polygonOptions.getZIndex());
                vPolygon.setStrokeColor(polygonOptions.getStrokeColor());
            }
            vPolygon.setEditable(polygon.getEditable());
            polygonMap.put(polygon, vPolygon);
            attachPolygonEditListeners(polygon, vPolygon);
            polygonCompleteListener.polygonComplete(vPolygon);
        }
    }

    native public void consoleLog(String message) /*-{
      console.log(message );
    }-*/;
}