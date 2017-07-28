package com.bluedon.gsm.detector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GSMDataCreator {

    public static GSMData newGSMData(Context context) {
        CellInfoGsm cig = getCellInfoGsm(context);
        int bsss = getBsss(cig);
        int lac = getLAC(cig);
        String gps = getGPS(context);
        String date = getDate();
        String model = getModel();
        return new GSMData(bsss, lac, gps, date, model);
    }

    private static CellInfoGsm getCellInfoGsm(Context context) {
        Context c = context.getApplicationContext();
        TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            CellInfo ci = tm.getAllCellInfo().get(0);
            if (ci != null) return (CellInfoGsm) ci;
        }
        return null;
    }

    private static int getBsss(CellInfoGsm cig) {
        if (cig == null) return -1;
        int rssi = cig.getCellSignalStrength().getDbm();
        return rssi * 2 - 113;
    }

    private static String getGPS(Context context) {
        Context c = context.getApplicationContext();
        LocationManager lm = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return location.getLongitude() + ", " + location.getLatitude();
        }
        return null;
    }

    private static int getLAC(CellInfoGsm cig) {
        if (cig == null) return -1;
        return cig.getCellIdentity().getLac();
    }

    private static String getDate() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
        return sdf.format(now);
    }

    private static String getModel() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

}
