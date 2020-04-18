package com.baidu.histogram.view;

import java.util.List;

import com.baidu.histogram.callback.OnItemClickListener;
import com.baidu.histogram.data.BaseItemData;
import com.baidu.histogram.utils.LogUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class HistogramBottomViewBaseAdapter<T extends BaseItemData> extends BaseAdapter {
    private static final String TAG = "HistogramBottomTimeAdapter";
    protected List<T> mDataList;
    protected BaseSizeDefiner mSizeDefiner;

    public HistogramBottomViewBaseAdapter(List<T> dataList) {
        this.mDataList = dataList;
    }

    public void updateData(List<T> dataList) {
        if (mDataList == null) {
            mDataList = dataList;
        } else {
            mDataList.clear();
            mDataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    public void setSizeDefiner(BaseSizeDefiner sizeHolder) {
        this.mSizeDefiner = sizeHolder;
    }

    public abstract void onBindViewHolder(ViewHolder holder, T data, int position);

    @Override
    public T getItem(int position) {
        if (mDataList == null || mDataList.size() == 0 || mDataList.size() <= position) {
            return null;
        }
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(getLayoutId(), parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        onBindViewHolder(holder, position);
        return convertView;
    }

    protected abstract int getLayoutId();

    private void onBindViewHolder(final ViewHolder holder, final int position) {
        View itemView = holder.itemView;
        final T data = getItem(position);
        if (LogUtil.LOGGABLE) {
            LogUtil.e(TAG, "onBindViewHolder,index:" + position + ",data:" + data);
        }
        ViewGroup.LayoutParams itemViewLayoutParams = itemView.getLayoutParams();
        itemViewLayoutParams.width = mSizeDefiner.getItemSelectWidthPx();
        onBindViewHolder(holder, data, position);
    }

    @Override
    public int getCount() {
        if (mDataList == null) {
            return 0;
        }
        return mDataList.size();
    }

    public class ViewHolder {
        protected View itemView;

        public ViewHolder(View view) {
            this.itemView = view;
        }

        public <T extends View> T getView(int viewId) {
            return (T) itemView.findViewById(viewId);
        }
    }
}