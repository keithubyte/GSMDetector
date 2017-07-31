package com.bluedon.gsm.detector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class GSMDataFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = GSMDataFragment.class.getName();
    private static final int RC_LOCATION = 0x100;

    private Unbinder unbinder;

    @BindView(R.id.gsm_data_gps)
    TextView gpsView;
    @BindView(R.id.gsm_data_date)
    TextView dateView;
    @BindView(R.id.gsm_data_model)
    TextView modelView;
    @BindView(R.id.gsm_cell_list)
    ListView cellListView;

    @BindString(R.string.gps)
    String g;
    @BindString(R.string.date)
    String d;
    @BindString(R.string.model)
    String m;

    private GSMCellInfoAdapter adapter;
    private GSMData data;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture futures;
    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            Context context = getActivity();
            data = new GSMData(context, mGLM.getCurrentLoc());
            handler.obtainMessage(MSG_UPDATE_DATA).sendToTarget();
        }
    };

    private MHandler handler;
    private static final int MSG_UPDATE_DATA = 0x200;

    private static class MHandler extends Handler {
        private WeakReference<GSMDataFragment> mFragmentRef;

        MHandler(GSMDataFragment fragment) {
            mFragmentRef = new WeakReference<>(fragment);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            GSMDataFragment f = mFragmentRef.get();
            if (f != null) {
                switch (msg.what) {
                    case MSG_UPDATE_DATA:
                        GSMData d = f.data;
                        f.adapter = new GSMCellInfoAdapter(f.getActivity(), d.cells);
                        f.gpsView.setText(f.g + d.gps);
                        f.dateView.setText(f.d + d.date);
                        f.modelView.setText(f.m + d.model);
                        f.cellListView.setAdapter(f.adapter);
                        break;
                    default:
                        // ignored
                }
            }
        }
    }

    GSMLocationManager mGLM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gsmdata, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mGLM = new GSMLocationManager(getActivity());
        checkLocationPerms(getActivity());
        handler = new MHandler(this);
        futures = scheduler.scheduleAtFixedRate(refresh, 1L, 10L, TimeUnit.SECONDS);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (futures != null) {
            futures.cancel(true);
            scheduler.shutdown();
        }
        unregisterLocationListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_LOCATION) {
            registerLocationListener();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void checkLocationPerms(Context context) {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(context, perms)) {
            registerLocationListener();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.location_rationale), RC_LOCATION, perms);
        }
    }

    private void registerLocationListener() {
        if (mGLM != null) {
            mGLM.registerLocationListener();
        }
    }

    private void unregisterLocationListener() {
        if (mGLM != null) {
            mGLM.unregisterLocationListener();
        }
    }
}
