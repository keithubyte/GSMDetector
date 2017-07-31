package com.bluedon.gsm.detector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GSMData {
    private static final String TAG = GSMData.class.getName();

    public List<GSMCellInfo> cells;
    public final String gps;
    public final String date;
    public final String model;

    public GSMData(Context context) {
        this.cells = getCells(context);
        this.gps = getGPS(context);
        this.date = getDate();
        this.model = getModel();
    }

    private List<GSMCellInfo> getCells(Context context) {
        List<GSMCellInfo> cells = new ArrayList<>();
        Context c = context.getApplicationContext();
        TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        for (CellInfo cellInfo : tm.getAllCellInfo()) {
            Log.e(TAG, "cell info : " + cellInfo.toString());
            cells.add(new GSMCellInfo(cellInfo));
        }
        return cells;
    }

    private String getGPS(Context context) {
        Context c = context.getApplicationContext();
        LocationManager lm = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return location.getLongitude() + ", " + location.getLatitude();
        }
        return null;
    }

    private String getDate() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
        return sdf.format(now);
    }

    private String getModel() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }
}
