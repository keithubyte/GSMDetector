package com.bluedon.gsm.detector;

/**
 * Author: Keith
 * Date: 2017/8/2
 */
public class GSMCellInfo {
    public int mcc;
    public int mnc;
    public int lac;
    public int ci;
    public int bsss;
    public String type;
    public double latitude;
    public double longitude;

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

    @Override
    public String toString() {
        return "GSMCellInfoBean{" +
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
