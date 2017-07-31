package com.bluedon.gsm.detector;

import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

/**
 * Author: Keith
 * Date: 2017/7/31
 */

public class GSMCellInfo {

    private enum CellType {
        GSM("GSM"), WCDMA("WCDMA"), LTE("LTE"), NONE("NONE");

        CellType(String name) {
        }
    }

    private CellType type;
    private CellInfoGsm gsm;
    private CellInfoWcdma wcdma;
    private CellInfoLte lte;

    public GSMCellInfo(CellInfo cellInfo) {
        if (cellInfo == null) {
            type = CellType.NONE;
        } else {
            if (cellInfo instanceof CellInfoGsm) {
                gsm = (CellInfoGsm) cellInfo;
                type = CellType.GSM;
            } else if (cellInfo instanceof CellInfoWcdma) {
                wcdma = (CellInfoWcdma) cellInfo;
                type = CellType.WCDMA;
            } else if (cellInfo instanceof CellInfoLte) {
                lte = (CellInfoLte) cellInfo;
                type = CellType.LTE;
            }
        }
    }

    public String getType() {
        return type.name();
    }

    public int getLac() {
        switch (type) {
            case GSM:
                return gsm.getCellIdentity().getLac();
            case WCDMA:
                return wcdma.getCellIdentity().getLac();
            case LTE:
                return lte.getCellIdentity().getTac();
            default:
                return Integer.MIN_VALUE;
        }
    }

    public int getBsss() {
        int rssi = Integer.MIN_VALUE;
        switch (type) {
            case GSM:
                rssi = gsm.getCellSignalStrength().getDbm();
                break;
            case WCDMA:
                rssi = wcdma.getCellSignalStrength().getDbm();
                break;
            case LTE:
                rssi = lte.getCellSignalStrength().getDbm();
                break;
            default:
                // ignored
        }
        return rssi == Integer.MIN_VALUE ? Integer.MIN_VALUE : rssi * 2 - 113;
    }
}
