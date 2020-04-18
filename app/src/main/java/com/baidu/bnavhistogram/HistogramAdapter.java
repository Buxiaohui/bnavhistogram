package com.baidu.bnavhistogram;

import java.util.List;

import com.baidu.bnavhistogram.data.HistogramItem;
import com.baidu.bnavhistogram.utils.TimeUtils;
import com.baidu.histogram.view.HistogramBaseAdapter;

import android.view.View;
import android.widget.TextView;

public class HistogramAdapter extends HistogramBaseAdapter<HistogramItem> {
    public HistogramAdapter(List<HistogramItem> dataList) {
        super(dataList);
    }

    @Override
    public int getLayoutId() {
        return R.layout.nsdk_layout_future_trip_main_panel_time_item;
    }

    @Override
    protected View getTagView(ViewHolder holder) {
        return holder.getView(R.id.eta_tag_tx);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, HistogramItem itemData, int position) {
        View itemView = holder.itemView;
        TextView topTv = holder.getView(R.id.duration_tx);
        TextView bottomTv = holder.getView(R.id.time_tx);
        topTv.setText("" + position);
        bottomTv.setText(TimeUtils.getTimeStamp(position));
    }

}
