package com.bluedon.gsm.detector;

public class GSMData {

    public final int bsss;
    public final int lac;
    public final String gps;
    public final String date;
    public final String model;

    public GSMData(int bsss, int lac, String gps, String date, String model) {
        this.bsss = bsss;
        this.lac = lac;
        this.gps = gps;
        this.date = date;
        this.model = model;
    }
}
