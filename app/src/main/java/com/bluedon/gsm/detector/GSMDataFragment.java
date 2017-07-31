package com.bluedon.gsm.detector;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

    private GSMData mGSMData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gsmdata, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                Context context = getActivity();
                mGSMData = new GSMData(context);
                GSMCellInfoAdapter adapter = new GSMCellInfoAdapter(context, mGSMData.cells);
                gpsView.setText(g + mGSMData.gps);
                dateView.setText(d + mGSMData.date);
                modelView.setText(m + mGSMData.model);
                cellListView.setAdapter(adapter);
            }
        }, 1000L);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
