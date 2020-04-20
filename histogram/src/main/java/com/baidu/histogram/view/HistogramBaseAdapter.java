package com.baidu.histogram.view;

import java.util.List;

import com.baidu.histogram.Config;
import com.baidu.histogram.R;
import com.baidu.histogram.callback.OnItemClickListener;
import com.baidu.histogram.data.BaseItemData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class HistogramBaseAdapter<T extends BaseItemData>
        extends RecyclerView.Adapter<HistogramBaseAdapter.ViewHolder> {
    private static final String TAG = "HistogramAdapter";
    private static final boolean DEBUG_HISTOGRAM_INDEX = false;
    private static boolean ENABLE_ANIM = false;
    protected List<T> mDataList;
    protected OnItemClickListener mOnItemClickListener;
    protected ViewCallback mViewCallback;
    protected BaseSizeDefiner mSizeDefiner;

    public HistogramBaseAdapter(List<T> dataList) {
        this.mDataList = dataList;
    }

    public void setViewCallback(ViewCallback callback) {
        this.mViewCallback = callback;
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

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public HistogramBaseAdapter setOnItemClickListener(@NonNull OnItemClickListener callback) {
        this.mOnItemClickListener = callback;
        return this;
    }

    public HistogramBaseAdapter setSizeDefiner(@NonNull BaseSizeDefiner sizeDefiner) {
        this.mSizeDefiner = sizeDefiner;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(getLayoutId(), parent,
                                false));
        return holder;
    }

    public abstract int getLayoutId();

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        View itemView = holder.itemView;
        itemView.setTag(position);
        ViewGroup.LayoutParams itemViewLayoutParams = itemView.getLayoutParams();
        if (mViewCallback != null && position == mViewCallback.getCenterItemIndex()) {
            itemViewLayoutParams.width = mSizeDefiner.getItemSelectWidthPx();
        } else {
            itemViewLayoutParams.width = mSizeDefiner.getItemWidthPx();
        }
        final View tagView = getTagView(holder);
        setTagTvLayoutParamsDirectly(tagView, mDataList.get(position), position);
        onBindViewHolder(holder, mDataList.get(position), position);
        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, pos, mDataList.get(pos));
                }
            }
        });
    }

    protected abstract View getTagView(final ViewHolder holder);

    protected abstract void onBindViewHolder(final ViewHolder holder, final T itemData,
                                             final int position);

    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        }
        return mDataList.size();
    }

    private void setTagTvLayoutParamsDirectly(final View tagView,
                                              final BaseItemData itemData,
                                              final int index) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tagView.getLayoutParams();
        lp.height = (int) itemData.getHeight();
        tagView.setLayoutParams(lp);
        tagView.setPivotX(tagView.getWidth() / 2);
        tagView.setPivotY((float) lp.height);
    }

    public interface ViewCallback {
        int getCenterItemIndex();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        public <T extends View> T getView(int viewId) {
            return (T) itemView.findViewById(viewId);
        }
    }
}