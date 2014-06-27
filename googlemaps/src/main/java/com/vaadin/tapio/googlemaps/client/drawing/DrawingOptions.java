package com.vaadin.tapio.googlemaps.client.drawing;

import java.io.Serializable;

/**
 * @author korotkov
 * @version $Id$
 */
public class DrawingOptions implements Serializable {

    private static final long serialVersionUID = -806817086700404391L;

    protected PolygonOptions polygonOptions;
    protected OverlayType initialDrawingMode;
    protected boolean enableDrawingControl;
    protected DrawingControlOptions drawingControlOptions;

    public DrawingOptions() {
    }

    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

    public void setPolygonOptions(PolygonOptions polygonOptions) {
        this.polygonOptions = polygonOptions;
    }

    public OverlayType getInitialDrawingMode() {
        return initialDrawingMode;
    }

    public void setInitialDrawingMode(OverlayType initialDrawingMode) {
        this.initialDrawingMode = initialDrawingMode;
    }

    public boolean isEnableDrawingControl() {
        return enableDrawingControl;
    }

    public void setEnableDrawingControl(boolean enableDrawingControl) {
        this.enableDrawingControl = enableDrawingControl;
    }

    public DrawingControlOptions getDrawingControlOptions() {
        return drawingControlOptions;
    }

    public void setDrawingControlOptions(DrawingControlOptions drawingControlOptions) {
        this.drawingControlOptions = drawingControlOptions;
    }
}
