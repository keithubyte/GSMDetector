package com.bluedon.gsm.detector;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * Author: Keith
 * Date: 2017/8/1
 */
public class LocationConverter {
    public static LatLng Gps2LatLng(GPSLocation gps) {
        LatLng source = new LatLng(gps.latitude, gps.longitude);
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(source);
        return converter.convert();
    }

    public static LatLng Gps2LatLng(double latitude, double longitude) {
        return Gps2LatLng(new GPSLocation(latitude, longitude));
    }
}
