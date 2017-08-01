package com.bluedon.gsm.detector;

import android.content.Context;
import android.location.Location;
import android.os.Build;
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

    public GSMData(Context context, Location location) {
        this.cells = getCells(context);
        this.gps = getGPS(location);
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

    private String getGPS(Location location) {
        if (location == null) return null;
        return location.getLongitude() + " / " + location.getLatitude();
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
