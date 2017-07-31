package com.bluedon.gsm.detector;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GSMDataFragment extends Fragment {

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
            data = new GSMData(context);
            handler.obtainMessage(MSG_UPDATE_DATA).sendToTarget();
        }
    };

    private MHandler handler;
    private static final int MSG_UPDATE_DATA = 0x100;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gsmdata, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        handler = new MHandler(this);
        futures = scheduler.scheduleAtFixedRate(refresh, 1L, 10L, TimeUnit.SECONDS);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (futures != null) futures.cancel(true);
        scheduler.shutdown();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
