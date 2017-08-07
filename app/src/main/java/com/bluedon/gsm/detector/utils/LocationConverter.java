package com.bluedon.gsm.detector.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bluedon.gsm.detector.data.GPSInfo;

/**
 * Author: Keith
 * Date: 2017/8/1
 */
public class LocationConverter {
    public static LatLng Gps2LatLng(double latitude, double longitude) {
        return Gps2LatLng(new GPSInfo(latitude, longitude));
    }

    public static LatLng Gps2LatLng(GPSInfo gps) {
        LatLng source = new LatLng(gps.latitude, gps.longitude);
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(source);
        return converter.convert();
    }
}
