package com.bluedon.gsm.detector.data;

import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

/**
 * Author: Keith
 * Date: 2017/8/3
 */

public class BSIdentity {
    private CellIdentityGsm gsmId;
    private CellIdentityWcdma wcdmaId;
    private CellIdentityLte lteId;

    public BSIdentity(CellInfo info) {
        if (info instanceof CellInfoGsm) {
            gsmId = ((CellInfoGsm) info).getCellIdentity();
        } else if (info instanceof CellInfoWcdma) {
            wcdmaId = ((CellInfoWcdma) info).getCellIdentity();
        } else if (info instanceof CellInfoLte) {
            lteId = ((CellInfoLte) info).getCellIdentity();
        }
    }

    public int getMcc() {
        if (gsmId != null) return gsmId.getMcc();
        if (wcdmaId != null) return wcdmaId.getMcc();
        if (lteId != null) return lteId.getMcc();
        return 0;
    }

    public int getMnc() {
        if (gsmId != null) return gsmId.getMnc();
        if (wcdmaId != null) return wcdmaId.getMnc();
        if (lteId != null) return lteId.getMnc();
        return 0;
    }
}
