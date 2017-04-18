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
import com.google.gwt.maps.client.base.Point;
import com.google.gwt.maps.client.base.Size;
import com.google.gwt.maps.client.controls.MapTypeControlOptions;
import com.google.gwt.maps.client.drawinglib.DrawingManager;
import com.google.gwt.maps.client.drawinglib.DrawingManagerOptions;
import com.google.gwt.maps.client.events.center.CenterChangeMapEvent;
import com.google.gwt.maps.client.events.center.CenterChangeMapHandler;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.events.closeclick.CloseClickMapEvent;
import com.google.gwt.maps.client.events.closeclick.CloseClickMapHandler;
import com.google.gwt.maps.client.events.dblclick.DblClickMapEvent;
import com.google.gwt.maps.client.events.dblclick.DblClickMapHandler;
import com.google.gwt.maps.client.events.domready.DomReadyMapEvent;
import com.google.gwt.maps.client.events.domready.DomReadyMapHandler;
import com.google.gwt.maps.client.events.dragend.DragEndMapEvent;
import com.google.gwt.maps.client.events.dragend.DragEndMapHandler;
import com.google.gwt.maps.client.events.idle.IdleMapEvent;
import com.google.gwt.maps.client.events.idle.IdleMapHandler;
import com.google.gwt.maps.client.events.insertat.InsertAtMapEvent;
import com.google.gwt.maps.client.events.insertat.InsertAtMapHandler;
import com.google.gwt.maps.client.events.overlaycomplete.circle.CircleCompleteMapEvent;
import com.google.gwt.maps.client.events.overlaycomplete.polygon.PolygonCompleteMapEvent;
import com.google.gwt.maps.client.events.radius.RadiusChangeMapEvent;
import com.google.gwt.maps.client.events.radius.RadiusChangeMapHandler;
import com.google.gwt.maps.client.events.removeat.RemoveAtMapEvent;
import com.google.gwt.maps.client.events.removeat.RemoveAtMapHandler;
import com.google.gwt.maps.client.events.rightclick.RightClickMapEvent;
import com.google.gwt.maps.client.events.rightclick.RightClickMapHandler;
import com.google.gwt.maps.client.events.setat.SetAtMapEvent;
import com.google.gwt.maps.client.events.setat.SetAtMapHandler;
import com.google.gwt.maps.client.events.tiles.TilesLoadedMapEvent;
import com.google.gwt.maps.client.events.tiles.TilesLoadedMapHandler;
import com.google.gwt.maps.client.layers.KmlLayer;
import com.google.gwt.maps.client.layers.KmlLayerOptions;
import com.google.gwt.maps.client.maptypes.ImageMapType;
import com.google.gwt.maps.client.mvc.MVCArray;
import com.google.gwt.maps.client.overlays.*;
import com.google.gwt.maps.client.services.DirectionsResult;
import com.google.gwt.maps.client.services.DirectionsService;
import com.google.gwt.maps.client.services.DirectionsStatus;
import com.google.gwt.maps.client.visualizationlib.HeatMapLayer;
import com.google.gwt.maps.client.visualizationlib.HeatMapLayerOptions;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.vaadin.tapio.googlemaps.client.base.LatLon;
import com.vaadin.tapio.googlemaps.client.base.WeightedLocation;
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
import com.vaadin.tapio.googlemaps.client.services.DirectionsRequest;

import java.util.*;

public class GoogleMapWidget extends FlowPanel implements RequiresResize {

    public static final String CLASSNAME = "googlemap";

    private MapWidget map;
    private MapOptions mapOptions;
    private Map<Marker, GoogleMapMarker> markerMap = new HashMap<Marker, GoogleMapMarker>();
    private Map<GoogleMapMarker, Marker> gmMarkerMap = new HashMap<GoogleMapMarker, Marker>();
    private Map<Polygon, GoogleMapPolygon> polygonMap = new HashMap<Polygon, GoogleMapPolygon>();
    private Map<Circle, GoogleMapCircle> circleMap = new HashMap<Circle, GoogleMapCircle>();
    private Map<Polyline, GoogleMapPolyline> polylineMap = new HashMap<Polyline, GoogleMapPolyline>();
    private Map<InfoWindow, GoogleMapInfoWindow> infoWindowMap = new HashMap<InfoWindow, GoogleMapInfoWindow>();
    private Map<KmlLayer, GoogleMapKmlLayer> kmlLayerMap = new HashMap<KmlLayer, GoogleMapKmlLayer>();
    private Map<HeatMapLayer, GoogleMapHeatMapLayer> heatMapLayerMap = new HashMap<HeatMapLayer, GoogleMapHeatMapLayer>();
    private Map<ImageMapType, GoogleImageMapType> imageMapTypes = new LinkedHashMap<ImageMapType, GoogleImageMapType>();
    private Map<ImageMapType, GoogleImageMapType> overlayImageMapTypes = new LinkedHashMap<ImageMapType, GoogleImageMapType>();

    private MarkerClickListener markerClickListener = null;
    private MarkerDoubleClickListener markerDoubleClickListener = null;
    private MarkerDragListener markerDragListener = null;
    private MarkerRightClickListener markerRightClickListener = null;

    private InfoWindowClosedListener infoWindowClosedListener = null;
    private DirectionsResultHandler directionsResultHandler = null;

    private PolygonCompleteListener polygonCompleteListener = null;
    private PolygonClickListener polygonClickListener = null;
    private PolygonEditListener polygonEditListener = null;
    private PolygonRightClickListener polygonRightClickListener = null;

    private CircleClickListener circleClickListener = null;
    private CircleDoubleClickListener circleDoubleClickListener = null;
    private CircleRightClickListener circleRightClickListener = null;
    private CircleCompleteListener circleCompleteListener = null;
    private CircleCenterChangeListener circleCenterChangeListener = null;
    private CircleRadiusChangeListener circleRadiusChangeListener = null;

    private DirectionsService directionsService;

    protected DrawingManager drawingManager;
    private MapMoveListener mapMoveListener = null;
    private LatLngBounds allowedBoundsCenter = null;
    private LatLngBounds allowedBoundsVisibleArea = null;

    private MapClickListener mapClickListener = null;
    private MapRightClickListener mapRightClickListener = null;

    private LatLng center = null;
    private int zoom = 0;
    private boolean forceBoundUpdate = false;
    private boolean initListenerNotified = false;
    private transient boolean markerDoubleClicked = false;

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
        mapImpl.addRightClickHandler(new RightClickMapHandler() {
            @Override
            public void onEvent(RightClickMapEvent rightClickMapEvent) {
                if (mapRightClickListener != null) {
                    LatLng latLng = rightClickMapEvent.getMouseEvent().getLatLng();
                    LatLon position = new LatLon(latLng.getLatitude(), latLng.getLongitude());
                    mapRightClickListener.mapRightClicked(position);
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
        List<GoogleMapMarker> removedMarkers = getRemovedMarkers(markers);
        removeMarkers(removedMarkers);

        for (GoogleMapMarker googleMapMarker : markers) {
            if (!gmMarkerMap.containsKey(googleMapMarker)) {

                final Marker marker = addMarker(googleMapMarker);
                markerMap.put(marker, googleMapMarker);
                gmMarkerMap.put(googleMapMarker, marker);

                marker.addClickHandler(new ClickMapHandler() {
                    @Override
                    public void onEvent(ClickMapEvent event) {
                        if (markerClickListener != null) {
                            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                @Override
                                public void execute() {
                                    Timer timer = new Timer() {
                                        @Override
                                        public void run() {
                                            if (!markerDoubleClicked) {
                                                markerClickListener.markerClicked(markerMap.get(marker));
                                            }
                                        }
                                    };
                                    timer.schedule(500);
                                }
                            });
                        }
                    }
                });
                marker.addDblClickHandler(new DblClickMapHandler() {
                    @Override
                    public void onEvent(DblClickMapEvent event) {
                        markerDoubleClicked = true;
                        if (markerDoubleClickListener != null) {
                            markerDoubleClickListener.markerDoubleClicked(markerMap
                                    .get(marker));
                        }
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                Timer timer = new Timer() {
                                    @Override
                                    public void run() {
                                        markerDoubleClicked = false;
                                    }
                                };
                                timer.schedule(500);
                            }
                        });
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
                marker.addRightClickHandler(new RightClickMapHandler() {
                    @Override
                    public void onEvent(RightClickMapEvent rightClickMapEvent) {
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                Timer timer = new Timer() {
                                    @Override
                                    public void run() {
                                        if (!markerDoubleClicked && markerRightClickListener != null) {
                                            markerRightClickListener.markerRightClicked(markerMap.get(marker));
                                        }
                                    }
                                };
                                timer.schedule(500);
                            }
                        });
                    }
                });
            } else {
                updateMarker(googleMapMarker);
            }
        }
    }

    private List<GoogleMapMarker> getRemovedMarkers(Collection<GoogleMapMarker> newMarkers) {
        List<GoogleMapMarker> result = new ArrayList<GoogleMapMarker>();

        for (GoogleMapMarker oldMarker : gmMarkerMap.keySet()) {
            if (!newMarkers.contains(oldMarker)) {
                result.add(oldMarker);
            }
        }
        return result;
    }

    private void removeMarkers(List<GoogleMapMarker> markers) {
        for (GoogleMapMarker gMarker : markers) {
            Marker marker = gmMarkerMap.get(gMarker);
            marker.close();
            marker.setMap((MapWidget) null);

            markerMap.remove(marker);
            gmMarkerMap.remove(gMarker);
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

    public void setMarkerRightClickListener(MarkerRightClickListener markerRightClickListener) {
        this.markerRightClickListener = markerRightClickListener;
    }

    public void setMarkerDoubleClickListener(MarkerDoubleClickListener listener) {
        markerDoubleClickListener = listener;
    }

    public void setMapMoveListener(MapMoveListener listener) {
        mapMoveListener = listener;
    }

    public void setMapClickListener(MapClickListener listener) {
        mapClickListener = listener;
    }

    protected void setMapRightClickListener(MapRightClickListener mapRightClickListener) {
        this.mapRightClickListener = mapRightClickListener;
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

    public void setPolygonClickListener(PolygonClickListener listener) {
        polygonClickListener = listener;
    }

    protected void setPolygonRightClickListener(PolygonRightClickListener polygonRightClickListener) {
        this.polygonRightClickListener = polygonRightClickListener;
    }

    public void setDirectionsResultHandler(DirectionsResultHandler handler) {
        directionsResultHandler = handler;
    }

    public void setCircleCompleteListener(CircleCompleteListener circleCompleteListener) {
        this.circleCompleteListener = circleCompleteListener;
    }

    public void setCircleClickListener(CircleClickListener circleClickListener) {
        this.circleClickListener = circleClickListener;
    }

    protected void setCircleRightClickListener(CircleRightClickListener circleRightClickListener) {
        this.circleRightClickListener = circleRightClickListener;
    }

    public void setCircleCenterChangeListener(CircleCenterChangeListener circleCenterChangeListener) {
        this.circleCenterChangeListener = circleCenterChangeListener;
    }

    public void setCircleDoubleClickListener(CircleDoubleClickListener circleDoubleClickListener) {
        this.circleDoubleClickListener = circleDoubleClickListener;
    }

    public void setCircleRadiusChangeListener(CircleRadiusChangeListener circleRadiusChangeListener) {
        this.circleRadiusChangeListener = circleRadiusChangeListener;
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

        if (googleMapMarker.getIconUrl() != null) {
            options.setIcon(googleMapMarker.getIconUrl());
        }

        if (googleMapMarker.getMarkerImage() != null) {
            options.setIcon(createMarkerImage(googleMapMarker.getMarkerImage()));
        }

        if (googleMapMarker.isAnimationEnabled()) {
            options.setAnimation(Animation.DROP);
        }
        return options;
    }

    private MarkerImage createMarkerImage(com.vaadin.tapio.googlemaps.client.base.MarkerImage googleMapMarkerImage) {
        if (googleMapMarkerImage == null) {
            return null;
        }

        MarkerImage markerImage = MarkerImage.newInstance(googleMapMarkerImage.getUrl());
        markerImage.setSize(createSize(googleMapMarkerImage.getSize()));
        markerImage.setAnchor(createPoint(googleMapMarkerImage.getAnchor()));
        markerImage.setOrigin(createPoint(googleMapMarkerImage.getOrigin()));
        markerImage.setScaledSize(createSize(googleMapMarkerImage.getScaledSize()));

        return markerImage;
    }

    private Point createPoint(com.vaadin.tapio.googlemaps.client.base.Point googleMapPoint) {
        if (googleMapPoint == null) {
            return null;
        }
        return Point.newInstance(googleMapPoint.getX(), googleMapPoint.getY());
    }

    private Size createSize(com.vaadin.tapio.googlemaps.client.base.Size googleMapSize) {
        if (googleMapSize == null) {
            return null;
        }
        return Size.newInstance(googleMapSize.getWidth(), googleMapSize.getHeight(),
                googleMapSize.getWidthUnit(), googleMapSize.getHeightUnit());
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
            polygon.setMap(null);
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

    public void setCircleOverlays(Map<Long, GoogleMapCircle> circleOverlays) {
        for (Circle circle : circleMap.keySet()) {
            circle.setMap(null);
        }
        circleMap.clear();

        for (GoogleMapCircle overlay : circleOverlays.values()) {

            CircleOptions options = CircleOptions.newInstance();

            options.setCenter(GoogleMapAdapterUtils.toLatLng(overlay.getCenter()));
            options.setRadius(overlay.getRadius());
            options.setFillColor(overlay.getFillColor());
            options.setFillOpacity(overlay.getFillOpacity());
            options.setStrokeColor(overlay.getStrokeColor());
            options.setStrokeOpacity(overlay.getStrokeOpacity());
            options.setStrokeWeight(overlay.getStrokeWeight());
            options.setZindex(overlay.getzIndex());
            options.setClickable(overlay.isClickable());

            Circle circle = Circle.newInstance(options);

            circle.setRadius(overlay.getRadius());
            circle.setMap(map);
            circle.setEditable(overlay.isEditable());

            attachCircleListeners(circle);
            circleMap.put(circle, overlay);
        }
    }

    private void attachCircleListeners(final Circle circle) {
        circle.addCenterChangeHandler(new CenterChangeMapHandler() {
            @Override
            public void onEvent(CenterChangeMapEvent event) {
                GoogleMapCircle vCircle = circleMap.get(circle);
                LatLon oldCenter = vCircle.getCenter();
                vCircle.setCenter(GoogleMapAdapterUtils.fromLatLng(circle.getCenter()));
                if (circleCenterChangeListener != null && !Objects.equals(oldCenter, circle.getCenter())) {
                    circleCenterChangeListener.centerChanged(vCircle, oldCenter);
                }
            }
        });
        circle.addClickHandler(new ClickMapHandler() {
            @Override
            public void onEvent(ClickMapEvent event) {
                if (circleClickListener != null) {
                    final GoogleMapCircle vCircle = circleMap.get(circle);
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            circleClickListener.circleClicked(vCircle);
                        }
                    });
                }
            }
        });
        circle.addDblClickHandler(new DblClickMapHandler() {
            @Override
            public void onEvent(DblClickMapEvent event) {
                if (circleDoubleClickListener != null) {
                    final GoogleMapCircle vCircle = circleMap.get(circle);
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            circleDoubleClickListener.circleDoubleClicked(vCircle);
                        }
                    });
                }
            }
        });
        circle.addRadiusChangeHandler(new RadiusChangeMapHandler() {
            @Override
            public void onEvent(RadiusChangeMapEvent event) {
                GoogleMapCircle vCircle = circleMap.get(circle);
                double oldRadius = vCircle.getRadius();
                vCircle.setRadius(circle.getRadius());
                if (circleRadiusChangeListener != null && vCircle.getRadius() != oldRadius) {
                    circleRadiusChangeListener.radiusChange(vCircle, oldRadius);
                }
            }
        });
        circle.addRightClickHandler(new RightClickMapHandler() {
            @Override
            public void onEvent(RightClickMapEvent rightClickMapEvent) {
                Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                        if (circleRightClickListener != null) {
                            circleRightClickListener.circleRightClicked(circleMap.get(circle));
                        }
                    }
                });
            }
        });
    }

    public void setPolylineOverlays(Set<GoogleMapPolyline> polylineOverlays) {
        for (Polyline polyline : polylineMap.keySet()) {
            polyline.setMap(null);
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
            kmlLayer.setMap(null);
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

    public void processDirectionRequests(Collection<DirectionsRequest> requests) {
        directionsService = DirectionsService.newInstance();
        for (final DirectionsRequest googleMapRequest : requests) {
            final long id = googleMapRequest.getId();
            final com.google.gwt.maps.client.services.DirectionsRequest request =
                    GoogleMapAdapterUtils.toDirectionsRequest(googleMapRequest);
            directionsService.route(request, new GoogleDirectionsResultHandler(id));
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

    public void setImageMapTypes(Set<GoogleImageMapType> mapTypes) {
        //no need to clear registry, will re-set map types instead
        imageMapTypes.clear();

        for (GoogleImageMapType mapType : mapTypes) {
            ImageMapType imageMapType = GoogleMapAdapterUtils.toImageMapType(mapType);
            map.getMapTypeRegistry().set(mapType.getMapTypeId().toUpperCase(), imageMapType);
            imageMapTypes.put(imageMapType, mapType);
        }
    }

    public void setOverlayImageMapTypes(Set<GoogleImageMapType> mapTypes) {
        map.getOverlayMapTypes().clear();
        overlayImageMapTypes.clear();

        for (GoogleImageMapType mapType : mapTypes) {
            ImageMapType imageMapType = GoogleMapAdapterUtils.toImageMapType(mapType);
            map.getOverlayMapTypes().insertAt(mapType.getOverlayMapTypePosition(), imageMapType);
            overlayImageMapTypes.put(imageMapType, mapType);
        }
    }

    public void setMapType(String mapTypeId) {
        try {
            MapTypeId standardMapId = MapTypeId.fromValue(mapTypeId.toLowerCase());
            mapOptions.setMapTypeId(standardMapId);
        } catch (IllegalArgumentException ignored) {
            mapOptions.setMapTypeId(mapTypeId);
        }
        map.setOptions(mapOptions);
    }
//
//    public void setMapTypeString(String mapTypeId) {
//        mapOptions.setMapTypeId(mapTypeId.toUpperCase());
//        map.setOptions(mapOptions);
//    }

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

            // wrap the contents inside a div if there's a defined width or height
            if (gmWindow.getHeight() != null || gmWindow.getWidth() != null) {
                StringBuilder contentWrapper = new StringBuilder("<div style=\"");
                if (gmWindow.getWidth() != null) {
                    contentWrapper.append("width:")
                            .append(gmWindow.getWidth())
                            .append(";");
                }
                if (gmWindow.getHeight() != null) {
                    contentWrapper.append("height:")
                            .append(gmWindow.getHeight())
                            .append(";");
                }
                contentWrapper.append("\" >")
                        .append(contents)
                        .append("</div>");
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

            window.addDomReadyHandler(new DomReadyMapHandler() {
                @Override
                public void onEvent(DomReadyMapEvent event) {
                    setInfoWindowClass();
                }
            });

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

    private native void setInfoWindowClass() /*-{
        var infoWindows = $doc.getElementsByClassName("gm-style-iw");

        for (i = 0; i < infoWindows.length; i++) {
            var infoWindow = infoWindows[i];
            if (infoWindow.className.indexOf("gm-style-iw-cuba") >= 0) {
                continue;
            }
            infoWindow.className += " gm-style-iw-cuba";

            var rootInfoWindow = infoWindow.parentElement;
            var rootBubble = rootInfoWindow.firstChild;
            var closeBtn = rootInfoWindow.lastChild;
            rootInfoWindow.className = "gm-style-iw-cuba-root";
            rootBubble.className = "gm-style-iw-cuba-bubble";
            closeBtn.className = "gm-style-iw-cuba-close";

            var e = rootBubble.firstChild;
            e.className = "gm-style-iw-cuba-bubble-anchor-shadow";
            e.nextSibling.className = "gm-style-iw-cuba-bubble-shadow";
            e.nextSibling.nextSibling.className = "gm-style-iw-cuba-bubble-anchor";
            rootBubble.lastChild.className = "gm-style-iw-cuba-bubble";
        }
    }-*/;

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

        DrawingManagerOptions options = GoogleMapAdapterUtils.toDrawingManagerOptions(vOptions);

        final com.vaadin.tapio.googlemaps.client.drawing.PolygonOptions vPolygonOptions = vOptions.getPolygonOptions();
        options.setPolygonOptions(GoogleMapAdapterUtils.toPolygonOptions(vPolygonOptions));

        final com.vaadin.tapio.googlemaps.client.drawing.CircleOptions vCircleOptions = vOptions.getCircleOptions();
        options.setCircleOptions(GoogleMapAdapterUtils.toCircleOptions(vCircleOptions));

        drawingManager = DrawingManager.newInstance(options);
        drawingManager.setMap(map);

        drawingManager.addPolygonCompleteHandler(new PolygonCompleteMapHandler(vPolygonOptions));
        drawingManager.addCircleCompleteHandler(new CircleCompleteMapHandler(vCircleOptions));
    }

    private void attachPolygonEditListeners(final Polygon polygon,
                                            final GoogleMapPolygon vPolygon) {
        polygon.addClickHandler(new ClickMapHandler() {
            @Override
            public void onEvent(ClickMapEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        polygonClickListener.polygonClicked(vPolygon);
                    }
                });
            }
        });
        polygon.addRightClickHandler(new RightClickMapHandler() {
            @Override
            public void onEvent(RightClickMapEvent rightClickMapEvent) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (polygonRightClickListener != null) {
                            polygonRightClickListener.polygonRightClicked(vPolygon);
                        }
                    }
                });
            }
        });
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
        switch (action) {
            case INSERT:
                vPolygon.getCoordinates().add(idx, GoogleMapAdapterUtils.fromLatLng(latLng));
                break;
            case REMOVE:
                vPolygon.getCoordinates().remove(idx);
                break;
            case SET:
                LatLon existing = vPolygon.getCoordinates().get(idx);
                existing.setLat(latLng.getLatitude());
                existing.setLon(latLng.getLongitude());
                break;
        }
        polygonEditListener.polygonEdited(vPolygon, action, idx,
                new LatLon(latLng.getLatitude(), latLng.getLongitude()));
    }

    public void setMapTypes(List<String> mapTypeIds) {
        MapTypeControlOptions mapTypeControlOptions = mapOptions.getMapTypeControlOptions();
        if (mapTypeControlOptions == null) {
            mapTypeControlOptions = MapTypeControlOptions.newInstance();
        }
        mapTypeControlOptions.setMapTypeIds(mapTypeIds.toArray(new String[mapTypeIds.size()]));
        mapOptions.setMapTypeControlOptions(mapTypeControlOptions);
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

    private class CircleCompleteMapHandler implements
            com.google.gwt.maps.client.events.overlaycomplete.circle.CircleCompleteMapHandler {

        private final com.vaadin.tapio.googlemaps.client.drawing.CircleOptions circleOptions;

        public CircleCompleteMapHandler(com.vaadin.tapio.googlemaps.client.drawing.CircleOptions circleOptions) {
            this.circleOptions = circleOptions;
        }

        @Override
        public void onEvent(CircleCompleteMapEvent event) {
            Circle circle = event.getCircle();

            GoogleMapCircle vCircle = new GoogleMapCircle();
            vCircle.setRadius(circle.getRadius());
            vCircle.setCenter(GoogleMapAdapterUtils.fromLatLng(circle.getCenter()));

            if (circleOptions != null) {
                vCircle.setFillColor(circleOptions.getFillColor());
                vCircle.setFillOpacity(circleOptions.getFillOpacity());
                vCircle.setStrokeColor(circleOptions.getStrokeColor());
                vCircle.setStrokeOpacity(circleOptions.getStrokeOpacity());
                vCircle.setStrokeWeight(circleOptions.getStrokeWeight());
                vCircle.setStrokeColor(circleOptions.getStrokeColor());
                vCircle.setzIndex(circleOptions.getZIndex());
                vCircle.setStrokeColor(circleOptions.getStrokeColor());
            }

            vCircle.setEditable(circle.getEditable());
            circleMap.put(circle, vCircle);
            attachCircleListeners(circle);
            circleCompleteListener.circleComplete(vCircle);
        }
    }

    native public void consoleLog(String message) /*-{
      console.log(message );
    }-*/;

    private class GoogleDirectionsResultHandler implements com.google.gwt.maps.client.services.DirectionsResultHandler {
        private final long id;
        private long requestId;

        public GoogleDirectionsResultHandler(long id) {
            this.id = id;
            requestId = id;
        }

        @Override
        public void onCallback(DirectionsResult result, DirectionsStatus status) {
            consoleLog("Callback for direction request with id " + requestId);
            directionsResultHandler.handle(id,
                    GoogleMapAdapterUtils.fromDirectionsResult(result),
                    GoogleMapAdapterUtils.fromDirectionsStatus(status));
        }
    }
}