package com.vaadin.tapio.googlemaps.demo;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.GoogleMapControl;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.WeightedLocation;
import com.vaadin.tapio.googlemaps.client.drawing.*;
import com.vaadin.tapio.googlemaps.client.events.*;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapHeatMapLayer;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapKmlLayer;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.tapio.googlemaps.demo.events.OpenInfoWindowOnMarkerClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Google Maps UI for testing and demoing.
 * 
 * @author Tapio Aali <tapio@vaadin.com>
 */
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private GoogleMapMarker kakolaMarker = new GoogleMapMarker(
            "DRAGGABLE: Kakolan vankila", new LatLon(60.44291, 22.242415),
            true, null);
    private GoogleMapInfoWindow kakolaInfoWindow = new GoogleMapInfoWindow(
            "Kakola used to be a provincial prison.", kakolaMarker);
    private final String apiKey = "";

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "com.vaadin.tapio.googlemaps.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.addTab(getMainTabContent(), "Main");
        tabSheet.addTab(getDrawingTabContent(), "Drawing");

        setContent(tabSheet);
    }

    private Component getMainTabContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        final GoogleMap googleMap =
                new GoogleMap(new LatLon(60.440963, 22.25122), 10, apiKey, new MapInitListener() {
                    @Override
                    public void init(LatLon center, int zoom, LatLon boundNE, LatLon boundSW) {
                        Preconditions.checkNotNull(center);
                        Preconditions.checkArgument(zoom == 10);
                        Preconditions.checkNotNull(boundNE);
                        Preconditions.checkNotNull(boundSW);
                    }
                });

        googleMap.setSizeFull();
        kakolaMarker.setAnimationEnabled(false);
        googleMap.addMarker(kakolaMarker);
        googleMap.addMarker("DRAGGABLE: Paavo Nurmi Stadion", new LatLon(
                60.442423, 22.26044), true, "VAADIN/1377279006_stadium.png");
        googleMap.addMarker("NOT DRAGGABLE: Iso-Heikkilä", new LatLon(
                60.450403, 22.230399), false, null);
        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);

        kakolaInfoWindow.setWidth("400px");
        kakolaInfoWindow.setHeight("500px");

        content.addComponent(googleMap);
        content.setExpandRatio(googleMap, 1.0f);

        final Panel console = new Panel();
        console.setHeight("100px");
        final CssLayout consoleLayout = new CssLayout();
        console.setContent(consoleLayout);
        content.addComponent(console);

        HorizontalLayout buttonLayoutRow1 = new HorizontalLayout();
        buttonLayoutRow1.setHeight("26px");
        content.addComponent(buttonLayoutRow1);

        HorizontalLayout buttonLayoutRow2 = new HorizontalLayout();
        buttonLayoutRow2.setHeight("26px");
        content.addComponent(buttonLayoutRow2);

        OpenInfoWindowOnMarkerClickListener infoWindowOpener = new OpenInfoWindowOnMarkerClickListener(
                googleMap, kakolaMarker, kakolaInfoWindow);
        googleMap.addMarkerClickListener(infoWindowOpener);

        googleMap.addMarkerClickListener(new MarkerClickListener() {
            @Override
            public void markerClicked(GoogleMapMarker clickedMarker) {
                Label consoleEntry = new Label("Marker \""
                        + clickedMarker.getCaption() + "\" at ("
                        + clickedMarker.getPosition().getLat() + ", "
                        + clickedMarker.getPosition().getLon() + ") clicked.");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });
        googleMap.addMarkerDoubleClickListener(new MarkerDoubleClickListener() {
            @Override
            public void markerDoubleClicked(GoogleMapMarker clickedMarker) {
                Label consoleEntry = new Label("Marker \""
                        + clickedMarker.getCaption() + "\" at ("
                        + clickedMarker.getPosition().getLat() + ", "
                        + clickedMarker.getPosition().getLon() + ") double clicked.");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        googleMap.addMapMoveListener(new MapMoveListener() {
            @Override
            public void mapMoved(int zoomLevel, LatLon center,
                                 LatLon boundsNE, LatLon boundsSW) {
                Label consoleEntry = new Label("Map moved to ("
                        + center.getLat() + ", " + center.getLon() + "), zoom "
                        + zoomLevel + ", boundsNE: (" + boundsNE.getLat()
                        + ", " + boundsNE.getLon() + "), boundsSW: ("
                        + boundsSW.getLat() + ", " + boundsSW.getLon() + ")"
                        + " map (center, boundNE, boundSW): ("
                        + googleMap.getCenter().getLat() + ", " + googleMap.getCenter().getLon() + "), ("
                        + googleMap.getBoundNE().getLat() + ", " + googleMap.getBoundNE().getLon() + "), ("
                        + googleMap.getBoundSW().getLat() + ", " + googleMap.getBoundSW().getLon() + "))");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        googleMap.addMapClickListener(new MapClickListener() {
            @Override
            public void mapClicked(LatLon position) {
                Label consoleEntry = new Label("Map click to ("
                        + position.getLat() + ", " + position.getLon() + ")");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        googleMap.addMarkerDragListener(new MarkerDragListener() {
            @Override
            public void markerDragged(GoogleMapMarker draggedMarker,
                                      LatLon oldPosition) {
                Label consoleEntry = new Label("Marker \""
                        + draggedMarker.getCaption() + "\" dragged from ("
                        + oldPosition.getLat() + ", " + oldPosition.getLon()
                        + ") to (" + draggedMarker.getPosition().getLat()
                        + ", " + draggedMarker.getPosition().getLon() + ")");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        googleMap.addInfoWindowClosedListener(new InfoWindowClosedListener() {

            @Override
            public void infoWindowClosed(GoogleMapInfoWindow window) {
                Label consoleEntry = new Label("InfoWindow \""
                        + window.getContent() + "\" closed");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        Button moveCenterButton = new Button(
                "Move over Luonnonmaa (60.447737, 21.991668), zoom 12",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.setCenter(new LatLon(60.447737, 21.991668));
                        googleMap.setZoom(12);
                    }
                });
        buttonLayoutRow1.addComponent(moveCenterButton);

        Button limitCenterButton = new Button(
                "Limit center between (60.619324, 22.712753), (60.373484, 21.945083)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.setCenterBoundLimits(new LatLon(60.619324,
                                22.712753), new LatLon(60.373484, 21.945083));
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow1.addComponent(limitCenterButton);

        Button limitVisibleAreaButton = new Button(
                "Limit visible area between (60.494439, 22.397835), (60.373484, 21.945083)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.setVisibleAreaBoundLimits(new LatLon(
                                60.494439, 22.397835), new LatLon(60.420632,
                                22.138626));
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow1.addComponent(limitVisibleAreaButton);

        Button zoomToBoundsButton = new Button("Zoom to bounds",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.fitToBounds(new LatLon(60.45685853323144,
                                22.320034754486073), new LatLon(
                                60.4482979242303, 22.27887893936156));

                    }
                });
        buttonLayoutRow1.addComponent(zoomToBoundsButton);
        Button addPolyOverlayButton = new Button("Add overlay over Luonnonmaa",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        ArrayList<LatLon> points = new ArrayList<LatLon>();
                        points.add(new LatLon(60.484715, 21.923706));
                        points.add(new LatLon(60.446636, 21.941387));
                        points.add(new LatLon(60.422496, 21.99546));
                        points.add(new LatLon(60.427326, 22.06464));
                        points.add(new LatLon(60.446467, 22.064297));

                        GoogleMapPolygon overlay = new GoogleMapPolygon(points,
                                "#ae1f1f", 0.8, "#194915", 0.5, 3);
                        googleMap.addPolygonOverlay(overlay);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(addPolyOverlayButton);

        Button addPolyLineButton = new Button("Draw line from Turku to Raisio",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        ArrayList<LatLon> points = new ArrayList<LatLon>();
                        points.add(new LatLon(60.448118, 22.253738));
                        points.add(new LatLon(60.455144, 22.24198));
                        points.add(new LatLon(60.460222, 22.211939));
                        points.add(new LatLon(60.488224, 22.174602));
                        points.add(new LatLon(60.486025, 22.169195));

                        GoogleMapPolyline overlay = new GoogleMapPolyline(
                                points, "#d31717", 0.8, 10);
                        googleMap.addPolyline(overlay);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(addPolyLineButton);
        Button addPolyLineButton2 = new Button(
                "Draw line from Turku to Raisio2", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ArrayList<LatLon> points2 = new ArrayList<LatLon>();
                points2.add(new LatLon(60.448118, 22.253738));
                points2.add(new LatLon(60.486025, 22.169195));
                GoogleMapPolyline overlay2 = new GoogleMapPolyline(
                        points2, "#d31717", 0.8, 10);
                googleMap.addPolyline(overlay2);
                event.getButton().setEnabled(false);
            }
        });
        buttonLayoutRow2.addComponent(addPolyLineButton2);
        Button changeToTerrainButton = new Button("Change to terrain map",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.setMapType(GoogleMap.MapType.Terrain);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(changeToTerrainButton);

        Button changeControls = new Button("Remove street view control",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.removeControl(GoogleMapControl.StreetView);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(changeControls);

        Button addInfoWindowButton = new Button(
                "Add InfoWindow to Kakola marker", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                googleMap.openInfoWindow(kakolaInfoWindow);
            }
        });
        buttonLayoutRow2.addComponent(addInfoWindowButton);

        Button moveMarkerButton = new Button("Move kakola marker",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        kakolaMarker.setPosition(new LatLon(60.3, 22.242415));
                        googleMap.addMarker(kakolaMarker);
                    }
                });
        buttonLayoutRow2.addComponent(moveMarkerButton);

        Button addKmlLayerButton = new Button("Add KML layer",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap
                                .addKmlLayer(new GoogleMapKmlLayer(
                                        "http://maps.google.it/maps/"
                                                + "ms?authuser=0&ie=UTF8&hl=it&oe=UTF8&msa=0&"
                                                + "output=kml&msid=212897908682884215672.0004ecbac547d2d635ff5"));
                    }
                });
        buttonLayoutRow2.addComponent(addKmlLayerButton);

        Button addHeatMapLayerButton = new Button("Add HeatMap layer",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        List<LatLon> data = Lists.newArrayList(new LatLon(60.493086028202555, 22.081146240234375), new LatLon(60.4944387701424, 22.137451171875),
                                new LatLon(60.50691090821668, 22.163543701171875), new LatLon(60.50661090821668, 22.163543701171875),
                                new LatLon(60.50721090821668, 22.163543701171875), new LatLon(60.50631090821668, 22.163543701171875),
                                new LatLon(60.50751090821668, 22.163543701171875), new LatLon(60.50551090821668, 22.163543701171875),
                                new LatLon(60.50791090821668, 22.163543701171875), new LatLon(60.50501090821668, 22.163543701171875),
                                new LatLon(60.50811090821668, 22.163543701171875), new LatLon(60.50451090821668, 22.163543701171875),
                                new LatLon(60.50831090821668, 22.163543701171875), new LatLon(60.50401090821668, 22.163543701171875),
                                new LatLon(60.534319275500465, 22.163543701171875),
                                new LatLon(60.535670296892135, 22.199249267578125), new LatLon(60.53296819772097, 22.247314453125),
                                new LatLon(60.559979041457176, 22.270660400390625), new LatLon(60.553903567535286, 22.181396484375),
                                new LatLon(60.534319275500465, 22.111358642578125), new LatLon(60.512019280355226, 22.11822509765625),
                                new LatLon(60.505258674134495, 22.325592041015625), new LatLon(60.521481759711904, 22.34893798828125),
                                new LatLon(60.53398151134199, 22.359237670898438), new LatLon(60.540060727006335, 22.342071533203125),
                                new LatLon(60.542424558089834, 22.317352294921875), new LatLon(60.53499479324469, 22.324905395507812),
                                new LatLon(60.48513799355608, 22.295165312499986), new LatLon(60.534319275500465, 22.3626708984375),
                                new LatLon(60.530603675918925, 22.322158813476562), new LatLon(60.55322844442426, 22.335891723632812),
                                new LatLon(60.544450561596406, 22.335891723632812), new LatLon(60.540060727006335, 22.357177734375),
                                new LatLon(60.53364374365928, 22.337265014648438), new LatLon(60.48649106706476, 22.29653860351561),
                                new LatLon(60.53026587299222, 22.344818115234375), new LatLon(60.52215754533236, 22.353057861328125),
                                new LatLon(60.521481759711904, 22.361297607421875), new LatLon(60.5133712323503, 22.347564697265625),
                                new LatLon(60.51134328320246, 22.369537353515625), new LatLon(60.497820378092264, 22.34344482421875),
                                new LatLon(60.497144084718585, 22.364044189453125), new LatLon(60.48158544292342, 22.34893798828125));
                        GoogleMapHeatMapLayer layer = new GoogleMapHeatMapLayer(data);

                        googleMap.addHeatMapLayer(layer);
                    }
                });
        buttonLayoutRow2.addComponent(addHeatMapLayerButton);

        Button addWeightedHeatMapLayerButton = new Button("Add weighted HeatMap layer",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        List<WeightedLocation> data = Lists.newArrayList(
                                new WeightedLocation(new LatLon(60.303086028202555, 21.801146240234375), 10),
                                new WeightedLocation(new LatLon(60.503086028202555, 22.801146240234375), 50),
                                new WeightedLocation(new LatLon(60.403086028202555, 22.801146240234375), 500));
                        GoogleMapHeatMapLayer layer = new GoogleMapHeatMapLayer();
                        layer.setRadius(10.0);
                        layer.setWeightedData(data);
                        googleMap.addHeatMapLayer(layer);
                    }
                });
        buttonLayoutRow2.addComponent(addWeightedHeatMapLayerButton);

        Button currentBounds = new Button("Current bounds",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Label consoleEntry = new Label("Current bounds \""
                                + " (boundNE, boundSW): ("
                                + googleMap.getBoundNE().getLat() + ", " + googleMap.getBoundNE().getLon() + "), ("
                                + googleMap.getBoundSW().getLat() + ", " + googleMap.getBoundSW().getLon() + "))");
                        consoleLayout.addComponent(consoleEntry, 0);
                    }
                });
        buttonLayoutRow2.addComponent(currentBounds);
        return content;
    }

    public Component getDrawingTabContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        HorizontalLayout mapAndOptionsLayout = new HorizontalLayout();
        mapAndOptionsLayout.setSizeFull();

        final GoogleMap googleMap = new GoogleMap(
                new LatLon(60.440963, 22.25122), 10, apiKey);
        googleMap.setSizeFull();

        // General Options
        final ComboBox initialDrawingMode = new ComboBox("initial drawing mode",
                Arrays.asList(OverlayType.values()));
        final CheckBox enableDrawingControls = new CheckBox("drawing controls", true);
        VerticalLayout generalOptions = new VerticalLayout(
                new Label(new Label("<h2>General drawing options</h2>", ContentMode.HTML)),
                enableDrawingControls,
                initialDrawingMode
        );

        // Control options
        final OptionGroup controlsPosition = new OptionGroup("Position", Arrays.asList(ControlPosition.values()));
        controlsPosition.setValue(ControlPosition.TOP_CENTER);
        final OptionGroup drawingModeControls = new OptionGroup("Drawing mode controls", Arrays.asList(OverlayType.values()));
        drawingModeControls.setMultiSelect(true);
        drawingModeControls.setValue(OverlayType.POLYGON);
        final VerticalLayout controlsOptions = new VerticalLayout();
        controlsOptions.addComponent(new Label("<h2>Controls options</h2>", ContentMode.HTML));
        controlsOptions.addComponent(controlsPosition);
        controlsOptions.addComponent(drawingModeControls);

        // Polygon options
        final CheckBox pgClickable = new CheckBox("clickable", true);
        final CheckBox pgEditable = new CheckBox("editable", true);
        final CheckBox pgGeodesic = new CheckBox("geodesic", false);
        final CheckBox pgVisible = new CheckBox("visible", true);
        final TextField pgFillColor = new TextField("fill color", "#345678");
        final TextField pgFillOpacity = new TextField("fill opacity", "0.5");
        final TextField pgStrokeColor = new TextField("stroke color", "#123456");
        final TextField pgStrokeOpacity = new TextField("stroke opacity", "0.8");
        final TextField pgStrokeWeight = new TextField("stroke weight", "5");
        final TextField pgZIndex = new TextField("z index", "1");
        ColorPicker pgfillColorPicker = new ColorPicker("test color picker", Color.MAGENTA);
        ColorPicker pgstrokeColorPicker = new ColorPicker("test2 color picker", Color.BLACK);
        VerticalLayout polygonOptionsLayout = new VerticalLayout(
                new Label("<h2>Polygon drawing options</h2>", ContentMode.HTML),
                pgClickable, pgEditable, pgGeodesic, pgVisible, pgFillColor,
                pgFillOpacity, pgStrokeColor, pgStrokeOpacity, pgStrokeWeight,
                pgZIndex, pgfillColorPicker, pgstrokeColorPicker
        );


        Button apply = new Button("Apply");
        apply.setWidth("50px");
        apply.setHeight("20px");
        apply.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                DrawingOptions options = new DrawingOptions();
                options.setInitialDrawingMode((OverlayType) initialDrawingMode.getValue());
                options.setEnableDrawingControl(enableDrawingControls.getValue());

                DrawingControlOptions controlOptions = new DrawingControlOptions(
                        (ControlPosition)controlsPosition.getValue(),
                        new ArrayList<OverlayType>((Collection)drawingModeControls.getValue()));

                PolygonOptions polygonOptions = new PolygonOptions();
                polygonOptions.setClickable(pgClickable.getValue());
                polygonOptions.setEditable(pgEditable.getValue());
                polygonOptions.setGeodesic(pgGeodesic.getValue());
                polygonOptions.setVisible(pgVisible.getValue());
                polygonOptions.setFillColor(pgFillColor.getValue());
                polygonOptions.setFillOpacity(Double.valueOf(pgFillOpacity.getValue()));
                polygonOptions.setStrokeColor(pgStrokeColor.getValue());
                polygonOptions.setStrokeOpacity(Double.valueOf(pgStrokeOpacity.getValue()));
                polygonOptions.setStrokeWeight(Integer.valueOf(pgStrokeWeight.getValue()));
                polygonOptions.setZIndex(Integer.valueOf(pgZIndex.getValue()));

                options.setDrawingControlOptions(controlOptions);
                options.setPolygonOptions(polygonOptions);

                googleMap.setDrawingOptions(options);
            }
        });

        Panel drawingPanel = new Panel();
        drawingPanel.setWidth("300px");
        VerticalLayout drawingOptions = new VerticalLayout();
        drawingOptions.addComponent(generalOptions);
        drawingOptions.addComponent(controlsOptions);
        drawingOptions.addComponent(polygonOptionsLayout);
        drawingOptions.addComponent(apply);
        drawingOptions.setWidth("300px");
//        googleMap.setHeight("600px");
//        googleMap.setWidth("100%");
        mapAndOptionsLayout.addComponent(googleMap);
        mapAndOptionsLayout.setExpandRatio(googleMap, 1.0f);
        drawingPanel.setContent(drawingOptions);
        mapAndOptionsLayout.addComponent(drawingPanel);

        content.addComponent(mapAndOptionsLayout);
        googleMap.addPolygonCompleteListener(new PolygonCompleteListener() {
            @Override
            public void polygonComplete(GoogleMapPolygon googleMapPolygon) {
                Notification.show("Polygon complete", Arrays.toString(
                        googleMapPolygon.getCoordinates().toArray()),
                        Notification.Type.TRAY_NOTIFICATION);
            }
        });
        googleMap.addPolygonEditListener(new PolygonEditListener() {
            @Override
            public void polygonEdited(GoogleMapPolygon polygon, ActionType
                    actionType, int idx, LatLon latLon) {
                String coords = Joiner.on('\n').join(Lists.transform(
                        polygon.getCoordinates(), new Function<LatLon, String>() {
                    @Override
                    public String apply(LatLon latLon) {
                        return latLon.getLat() + "," + latLon.getLon();
                    }
                }));

                Notification.show("Polygon edited", "New coordinates: \n"
                        + coords, Notification.Type.TRAY_NOTIFICATION);
            }
        });
        return content;

    }
}