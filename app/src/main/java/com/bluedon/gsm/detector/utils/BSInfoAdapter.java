package com.bluedon.gsm.detector.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluedon.gsm.detector.R;
import com.bluedon.gsm.detector.data.BSInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Keith
 * Date: 2017/7/31
 */

public class BSInfoAdapter extends BaseAdapter {
    private final Context context;
    private final List<BSInfo> cells;

    public BSInfoAdapter(Context context, List<BSInfo> cells) {
        this.context = context;
        this.cells = cells;
    }

    @Override
    public int getCount() {
        return cells.size();
    }

    @Override
    public Object getItem(int i) {
        return cells.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.list_item_fragment_gsmdata_gsm_cell_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        BSInfo info = cells.get(position);
        holder.typeView.setText(info.type);
        holder.bsssView.setText("" + info.bsss);
        holder.lacView.setText("" + info.lac);
        holder.cidView.setText("" + info.ci);
        holder.locationView.setText(info.latitude + "/" + info.longitude);
        return view;
    }

    @SuppressWarnings("WeakerAccess")
    static final class ViewHolder {
        @BindView(R.id.cell_type)
        TextView typeView;
        @BindView(R.id.cell_bsss)
        TextView bsssView;
        @BindView(R.id.cell_lac)
        TextView lacView;
        @BindView(R.id.cell_cid)
        TextView cidView;
        @BindView(R.id.cell_location)
        TextView locationView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
