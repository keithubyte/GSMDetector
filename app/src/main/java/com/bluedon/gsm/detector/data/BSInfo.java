package com.bluedon.gsm.detector.data;

import java.util.Locale;

/**
 * Author: Keith
 * Date: 2017/8/2
 */
public class BSInfo {
    public int mcc;
    public int mnc;
    public int lac;
    public int ci;
    public int bsss;
    public double latitude;
    public double longitude;
    public String type;
    public String desc;

    public boolean isValid() {
        if (0 < lac && lac < 65535) {
            switch (type) {
                case "GSM":
                    return (0 < ci && ci < 65535);
                case "WCDMA":
                case "LTE":
                    return (0 < ci && ci < 268435455);
                default:
                    return false;
            }
        }
        return false;
    }

    public String getBSInfo() {
        return mcc + "," + mnc + "," + lac + "," + ci;
    }

    public String id() {
        // format : "460-001-40977-002205409"
        return format("%03d", mcc) + "-" +
                format("%03d", mnc) + "-" +
                format("%05d", lac) + "-" +
                format("%09d", ci);
    }

    private String format(String format, int value) {
        return String.format(Locale.US, format, value);
    }

    @Override
    public String toString() {
        return "BSInfo{" +
                "mcc=" + mcc +
                ", mnc=" + mnc +
                ", lac=" + lac +
                ", ci=" + ci +
                ", bsss=" + bsss +
                ", type='" + type + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
