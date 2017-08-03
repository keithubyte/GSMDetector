package com.bluedon.gsm.detector;

import android.content.Context;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Keith
 * Date: 2017/8/2
 */

public class GSMCellInfoCollector {
    public static List<GSMCellInfo> acquireCells(Context c) {
        List<GSMCellInfo> cells = new ArrayList<>();
        TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        int phoneType = tm.getPhoneType();
        if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
            List<CellInfo> infoList = tm.getAllCellInfo();
            CellInfo curCellInfo = null;
            for (CellInfo info : infoList) {
                if (info.isRegistered()) {
                    curCellInfo = info;
                    break;
                }
            }
            if (curCellInfo != null) {
                GsmCellLocation gsl = (GsmCellLocation) tm.getCellLocation();
                CellIdentity identity = new CellIdentity(curCellInfo);
                int mcc = identity.getMcc();
                int mnc = identity.getMnc();
                for (CellInfo info : infoList) {
                    GSMCellInfo b = createCell(gsl, info, mcc, mnc);
                    if (b.isValid()) cells.add(b);
                }
            }
        }
        BSRetrofit.getBSLocations(cells);
        return cells;
    }

    private static GSMCellInfo createCell(GsmCellLocation gsl, CellInfo info, int mcc, int mnc) {
        GSMCellInfo bean = new GSMCellInfo();
        if (info instanceof CellInfoGsm) {
            bean = createCell(gsl, (CellInfoGsm) info, mcc, mnc);
        } else if (info instanceof CellInfoWcdma) {
            bean = createCell(gsl, (CellInfoWcdma) info, mcc, mnc);
        } else if (info instanceof CellInfoLte) {
            bean = createCell(gsl, (CellInfoLte) info, mcc, mnc);
        }
        return bean;
    }

    private static GSMCellInfo createCell(GsmCellLocation gsl, CellInfoGsm gsm, int mcc, int mnc) {
        CellIdentityGsm gsmId = gsm.getCellIdentity();
        GSMCellInfo bean = new GSMCellInfo();
        bean.mcc = mcc;
        bean.mnc = mnc;
        bean.type = "GSM";
        bean.bsss = gsm.getCellSignalStrength().getDbm() * 2 - 113;
        bean.lac = gsl.getLac();
        bean.ci = gsm.isRegistered() ? gsl.getCid() : gsmId.getCid();
        return bean;
    }

    private static GSMCellInfo createCell(GsmCellLocation gsl, CellInfoWcdma wcdma, int mcc, int mnc) {
        CellIdentityWcdma wcdmaId = wcdma.getCellIdentity();
        GSMCellInfo bean = new GSMCellInfo();
        bean.mcc = mcc;
        bean.mnc = mnc;
        bean.type = "WCDMA";
        bean.bsss = wcdma.getCellSignalStrength().getDbm() * 2 - 113;
        bean.lac = gsl.getLac();
        bean.ci = wcdma.isRegistered() ? gsl.getCid() : wcdmaId.getCid();
        return bean;
    }

    private static GSMCellInfo createCell(GsmCellLocation gsl, CellInfoLte lte, int mcc, int mnc) {
        CellIdentityLte lteId = lte.getCellIdentity();
        GSMCellInfo bean = new GSMCellInfo();
        bean.mcc = mcc;
        bean.mnc = mnc;
        bean.type = "LTE";
        bean.bsss = lte.getCellSignalStrength().getDbm() * 2 - 113;
        bean.lac = gsl.getLac();
        bean.ci = lte.isRegistered() ? gsl.getCid() : lteId.getCi();
        return bean;
    }
}
