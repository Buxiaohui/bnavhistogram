package com.baidu.bnavhistogram;

import java.util.List;

import com.baidu.bnavhistogram.data.HistogramItem;
import com.baidu.bnavhistogram.utils.TimeUtils;
import com.baidu.histogram.view.HistogramBottomViewBaseAdapter;

import android.text.TextUtils;
import android.widget.TextView;

public class HistogramTimeAdapter extends HistogramBottomViewBaseAdapter<HistogramItem> {
    private static final String TAG = "HistogramBottomTimeAdapter";

    public HistogramTimeAdapter(List<HistogramItem> dataList) {
        super(dataList);
    }

    @Override
    public void onBindViewHolder(HistogramBottomViewBaseAdapter<HistogramItem>.ViewHolder holder,
                                 HistogramItem data, int position) {
        TextView timeTv = ((ViewHolder) holder).getView(R.id.time_tx);
        timeTv.setTag(position);
        if (data == null || TextUtils.isEmpty(data.getStr())) {
            timeTv.setText("空");
        } else {
            timeTv.setText(TimeUtils.getTimeStamp(position) + "出发");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.nsdk_layout_future_trip_main_panel_time_select_item;
    }
}