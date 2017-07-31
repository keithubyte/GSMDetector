package com.bluedon.gsm.detector;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Author: Keith
 * Date: 2017/7/31
 */

public class GSMLocationManager {
    private static final String TAG = GSMLocationManager.class.getName();

    private LocationManager manager;
    private LocationListener listener;

    private Location currentLoc;

    public GSMLocationManager(Context context) {
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        listener = new GSMLocationListener();
    }

    public void registerLocationListener() {
        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void unregisterLocationListener() {
        manager.removeUpdates(listener);
    }

    public Location getCurrentLoc() {
        return currentLoc;
    }

    private final class GSMLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "location info | from : " + location.getProvider()
                    + ", " + location.getLatitude()
                    + ", " + location.getLongitude());
            currentLoc = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }
}
