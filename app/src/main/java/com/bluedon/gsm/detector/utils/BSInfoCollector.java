package com.bluedon.gsm.detector.utils;

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

import com.bluedon.gsm.detector.data.BSIdentity;
import com.bluedon.gsm.detector.data.BSInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Keith
 * Date: 2017/8/2
 */

public class BSInfoCollector {
    /**
     * 收集附近的所有基站
     *
     * @param context 上下文
     * @return 附近的参数合法的基站列表
     */
    public static List<BSInfo> collect(Context context) {
        List<BSInfo> cells = new ArrayList<>();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int phoneType = tm.getPhoneType();
        if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
            // 获取附近所有基站的信息，并且记录当前连接的基站
            List<CellInfo> infoList = tm.getAllCellInfo();
            CellInfo curCellInfo = null;
            for (CellInfo info : infoList) {
                if (info.isRegistered()) {
                    curCellInfo = info;
                    break;
                }
            }
            // 因为上述获取所有基站的信息里面，只有连接的那个基站信息比较准确
            // 所以，以当前正在连接的那个基站的参数来作为附近基站的某些参数的值（主要是MMC和MNC）
            // 另外，这个过程中，会把参数非法的基站排除出来
            if (curCellInfo != null) {
                // 采用这个过时的方法，是因为这个方法反而能够获得准确的基站信息
                GsmCellLocation gsl = (GsmCellLocation) tm.getCellLocation();
                BSIdentity identity = new BSIdentity(curCellInfo);
                int mcc = identity.getMcc();
                int mnc = identity.getMnc();
                for (CellInfo info : infoList) {
                    BSInfo b = createBSInfo(gsl, info, mcc, mnc);
                    if (b.isValid()) cells.add(b);
                }
            }
        }
        return cells;
    }

    private static BSInfo createBSInfo(GsmCellLocation gsl, CellInfo info, int mcc, int mnc) {
        BSInfo bs = new BSInfo();
        bs.mcc = mcc;
        bs.mnc = mnc;
        bs.lac = gsl.getLac();
        if (info instanceof CellInfoGsm) {
            createBSInfo(gsl, (CellInfoGsm) info, bs);
        } else if (info instanceof CellInfoWcdma) {
            createBSInfo(gsl, (CellInfoWcdma) info, bs);
        } else if (info instanceof CellInfoLte) {
            createBSInfo(gsl, (CellInfoLte) info, bs);
        }
        return bs;
    }

    private static void createBSInfo(GsmCellLocation gsl, CellInfoGsm gsm, BSInfo bs) {
        CellIdentityGsm gsmId = gsm.getCellIdentity();
        bs.type = "GSM";
        bs.bsss = gsm.getCellSignalStrength().getDbm() * 2 - 113;
        bs.ci = gsm.isRegistered() ? gsl.getCid() : gsmId.getCid();
    }

    private static void createBSInfo(GsmCellLocation gsl, CellInfoWcdma wcdma, BSInfo bs) {
        CellIdentityWcdma wcdmaId = wcdma.getCellIdentity();
        bs.type = "WCDMA";
        bs.bsss = wcdma.getCellSignalStrength().getDbm() * 2 - 113;
        bs.ci = wcdma.isRegistered() ? gsl.getCid() : wcdmaId.getCid();
    }

    private static void createBSInfo(GsmCellLocation gsl, CellInfoLte lte, BSInfo bs) {
        CellIdentityLte lteId = lte.getCellIdentity();
        bs.type = "LTE";
        bs.bsss = lte.getCellSignalStrength().getDbm() * 2 - 113;
        bs.ci = lte.isRegistered() ? gsl.getCid() : lteId.getCi();
    }
}
