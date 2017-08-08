package com.bluedon.gsm.detector.utils;

import com.bluedon.gsm.detector.data.BSLocation;

import java.util.List;

/**
 * Author: Keith
 * Date: 2017/8/7
 */

public interface BSResponseCallBack {
    void onSuccess(List<BSLocation> locations);
    void onFailure(Throwable throwable);
}
