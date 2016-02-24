package com.vaadin.tapio.googlemaps.client.maptypes;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author korotkov
 * @version $Id$
 */
public interface GoogleTileUrlCallback extends IsSerializable {
    String getTileUrl(double x, double y, int zoomLevel);
}
