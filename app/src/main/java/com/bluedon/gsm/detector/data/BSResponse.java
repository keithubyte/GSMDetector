package com.bluedon.gsm.detector.data;

import java.util.List;

/**
 * 表示从服务端返回的基站信息
 * Author: Keith
 * Date: 2017/8/3
 */
public class BSResponse {
    public int status;
    public String msg;
    public int count;
    public List<BSLocation> result;
    public double latitude;
    public double longitude;
    public int match;
}
