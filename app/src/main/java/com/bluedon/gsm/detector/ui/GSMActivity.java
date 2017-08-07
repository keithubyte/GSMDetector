package com.bluedon.gsm.detector.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bluedon.gsm.detector.R;
import com.bluedon.gsm.detector.data.BSInfo;

import java.util.List;

public class GSMActivity extends AppCompatActivity
        implements GlobalInfoFragment.OnBSListUpdatedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsm);
    }

    @Override
    public void onBSListUpdated(List<BSInfo> list) {
        BaiduMapFragment bmf = (BaiduMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.baidu_map_fragment);
        if (bmf != null) {
            bmf.markCells(list);
        }
    }
}
