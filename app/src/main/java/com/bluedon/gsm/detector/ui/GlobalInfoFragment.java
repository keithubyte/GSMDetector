package com.bluedon.gsm.detector.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bluedon.gsm.detector.R;
import com.bluedon.gsm.detector.data.BSInfo;
import com.bluedon.gsm.detector.data.BSLocation;
import com.bluedon.gsm.detector.data.GlobalInfo;
import com.bluedon.gsm.detector.utils.BSInfoAdapter;
import com.bluedon.gsm.detector.utils.BSResponseCallBack;
import com.bluedon.gsm.detector.utils.BSRetrofit;
import com.bluedon.gsm.detector.utils.LocationManagerEx;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class GlobalInfoFragment extends Fragment
        implements EasyPermissions.PermissionCallbacks, BSResponseCallBack {
    private static final String TAG = GlobalInfoFragment.class.getSimpleName();
    private static final int RC_LOCATION = 0x100;

    private Unbinder unbinder;
    @BindView(R.id.update_global_info)
    Button updateButton;
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

    private OnBSListUpdatedListener mBSListUpdatedListener;

    private BSInfoAdapter adapter;
    private GlobalInfo data;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture futures;
    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            Context context = getActivity();
            data = new GlobalInfo(context, mLm.getCurrentLoc());
            handler.obtainMessage(MSG_UPDATE_DATA).sendToTarget();
            BSRetrofit.queryBSLocations(data.cells, GlobalInfoFragment.this);
        }
    };

    private MHandler handler;
    private static final int MSG_UPDATE_DATA = 0x200;
    private static final int MSG_UPDATE_CELLS = 0x201;

    private static class MHandler extends Handler {
        private WeakReference<GlobalInfoFragment> mFragmentRef;

        MHandler(GlobalInfoFragment fragment) {
            mFragmentRef = new WeakReference<>(fragment);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            GlobalInfoFragment f = mFragmentRef.get();
            if (f != null) {
                switch (msg.what) {
                    case MSG_UPDATE_DATA:
                        GlobalInfo d = f.data;
                        f.adapter = new BSInfoAdapter(f.getActivity(), d.cells);
                        f.gpsView.setText(f.g + d.gps);
                        f.dateView.setText(f.d + d.date);
                        f.modelView.setText(f.m + d.model);
                        f.cellListView.setAdapter(f.adapter);
                        break;
                    case MSG_UPDATE_CELLS:
                        f.adapter.notifyDataSetChanged();
                        break;
                    default:
                        // ignored
                }
            }
        }
    }

    LocationManagerEx mLm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_global_info, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        cellListView.addHeaderView(View.inflate(getActivity(), R.layout.list_view_header, null));
        mLm = new LocationManagerEx(getActivity());
        checkLocationPerms(getActivity());
        handler = new MHandler(this);
        // futures = scheduler.scheduleAtFixedRate(refresh, 1L, 10L, TimeUnit.SECONDS);
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

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnBSListUpdatedListener) {
            mBSListUpdatedListener = (OnBSListUpdatedListener) activity;
        } else {
            throw new IllegalArgumentException(activity.getClass().getSimpleName() +
                    " must implement OnBSListUpdatedListener");
        }
    }

    @Override
    public void onSuccess(List<BSLocation> locations) {
        ArrayMap<String, BSInfo> cells = new ArrayMap<>();
        for (BSInfo cell : data.cells) {
            cells.put(cell.id(), cell);
        }
        for (BSLocation location : locations) {
            BSInfo cell = cells.get(location.id);
            if (cell != null) {
                cell.latitude = location.lat;
                cell.longitude = location.lng;
                cell.desc = location.roads;
            }
        }
        handler.sendEmptyMessage(MSG_UPDATE_CELLS);
        mBSListUpdatedListener.onBSListUpdated(data.cells);
        updateButton.setEnabled(true);
    }

    @Override
    public void onFailure(Throwable throwable) {
        Log.e(TAG, "fail by " + throwable.getMessage());
        updateButton.setEnabled(true);
    }

    @OnClick(R.id.update_global_info)
    public void updateGlobalInfo() {
        updateButton.setEnabled(false);
        scheduler.execute(refresh);
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
        if (mLm != null) {
            mLm.registerLocationListener();
        }
    }

    private void unregisterLocationListener() {
        if (mLm != null) {
            mLm.unregisterLocationListener();
        }
    }

    public interface OnBSListUpdatedListener {
        void onBSListUpdated(List<BSInfo> list);
    }
}
