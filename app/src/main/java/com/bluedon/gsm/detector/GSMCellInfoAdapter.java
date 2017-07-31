package com.bluedon.gsm.detector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Keith
 * Date: 2017/7/31
 */

public class GSMCellInfoAdapter extends BaseAdapter {
    private final Context context;
    private final List<GSMCellInfo> cells;

    public GSMCellInfoAdapter(Context context, List<GSMCellInfo> cells) {
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
        GSMCellInfo info = cells.get(position);
        holder.typeView.setText(info.getType());
        holder.bsssView.setText("" + info.getBsss());
        holder.lacView.setText("" + info.getLac());
        return view;
    }

    static final class ViewHolder {
        @BindView(R.id.cell_type)
        TextView typeView;
        @BindView(R.id.cell_bsss)
        TextView bsssView;
        @BindView(R.id.cell_lac)
        TextView lacView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
