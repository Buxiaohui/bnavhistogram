package com.baidu.bnavhistogram;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.baidu.bnavhistogram.data.HistogramItem;
import com.baidu.histogram.utils.VibrateHelper;
import com.baidu.histogram.view.BaseSizeDefiner;
import com.baidu.histogram.view.HistogramBottomHighLightView;
import com.baidu.histogram.view.HistogramViewHelper;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "HistogramActivity";
    private HistogramTimeAdapter mTimeAdapter;
    private HistogramBottomHighLightView mTimeView;

    private View mHistogramMidLineView;

    private RecyclerView mHistogramView;
    private HistogramAdapter mHistogramAdapter;
    private MyLinearLayoutManager mLinearLayoutManager;

    private List<HistogramItem> mDataList;

    private HistogramSizeDefiner mSizeDefiner;
    private HistogramViewHelper mHistogramViewHelper;

    private VibrateHelper mVibrateHelper;

    private void init() {
        mSizeDefiner = new HistogramSizeDefiner(this);
        mVibrateHelper = new VibrateHelper(this);
        constructData();
        findViewById(R.id.scroll_to_pos_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((EditText) findViewById(R.id.scroll_to_pos_edit)).getText().toString();
                try {
                    mHistogramViewHelper.setSelectedItemCenterHorizontal(Integer.parseInt(s));
                } catch (Exception e) {

                }
            }
        });

        mHistogramView = findViewById(R.id.histogram);
        mHistogramMidLineView = findViewById(R.id.mid_line);

        mLinearLayoutManager = new MyLinearLayoutManager(this);

        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHistogramView.setLayoutManager(mLinearLayoutManager);
        mHistogramAdapter = new HistogramAdapter(mDataList);
        mHistogramAdapter.setSizeDefiner(mSizeDefiner);
        mHistogramView.setAdapter(mHistogramAdapter);

        mTimeView = findViewById(R.id.histogram_select);
        mTimeView.setRect(true);
        mTimeAdapter = new HistogramTimeAdapter(mDataList);
        mTimeAdapter.setSizeDefiner(mSizeDefiner);
        mTimeView.setAdapter(mTimeAdapter);
        mHistogramViewHelper = new HistogramViewHelper(new HistogramViewHelper.ViewCallback() {
            @Override
            public LinearLayoutManager getLayoutManager() {
                return mLinearLayoutManager;
            }

            @Override
            public Context getContext() {
                return MainActivity.this;
            }

            @Override
            public void onCenterItemClick(int index) {

            }

            @Override
            public void onItemScrollToCenter(int index) {

            }

            @Override
            public RecyclerView getHistogramView() {
                return mHistogramView;
            }

            @Override
            public HistogramBottomHighLightView getHistogramBottomHighLightView() {
                return mTimeView;
            }

            @Override
            public BaseSizeDefiner getSizeDefiner() {
                return mSizeDefiner;
            }

            @Override
            public void onScroll(int index, View view, int midX) {
                int curIndex = index;
                int mid = midX;
                // 柱子背景图
                if (mid > mSizeDefiner.getPilllarAnimLeft() && mid < mSizeDefiner
                        .getPilllarAnimRight()) {
                    view.findViewById(R.id.eta_tag_tx)
                            .setBackgroundDrawable(getDrawable(R.drawable.item_select_bg));
                } else {
                    view.findViewById(R.id.eta_tag_tx)
                            .setBackgroundDrawable(getDrawable(R.drawable.item_unselect_bg));
                }
                // 震动
                if (mid > mSizeDefiner.getPilllarAnimLeft() && mid < mSizeDefiner.getPilllarAnimRight()) {
                    if (mDataList.get(curIndex).isHighLight()) {
                        mDataList.get(curIndex).setHighLight(false);
                        mVibrateHelper.mobileVibration(15);
                    }
                } else {
                    mDataList.get(curIndex).setHighLight(true);
                }
            }

        }, new HistogramViewHelper.DataCallback() {
            @Override
            public boolean isEmptyItem(int index) {
                return false;
            }

            @Override
            public int getTotalItemCount() {
                if (mDataList == null) {
                    return 0;
                }
                return mDataList.size();
            }

            @Override
            public List getDataList() {
                return mDataList;
            }

            @Override
            public boolean isDataReady() {
                return mDataList != null;
            }

            @Override
            public void onClickHistogramItem(int lastCenterIndex, int index) {
                for (int i = 0; i < mDataList.size(); i++) {
                    mDataList.get(i).setSelect(i == index);
                }
                mHistogramAdapter.notifyDataSetChanged();
            }
        });
        // 初始化的时使得目标item居中
        mHistogramViewHelper.setTargetItemCenterHorizontalImmediately(3);
    }

    private void constructData() {
        HistogramItem itemData = null;
        mDataList = new ArrayList<>();
        for (int i = 0; i < 102; i++) {
            itemData = new HistogramItem();
            itemData.setHeight(30 + new Random().nextInt(mSizeDefiner.dip2px(42)));
            mDataList.add(itemData);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);
        checkPermission();
        init();
    }

    private void checkPermission() {
        if (!VibrateHelper.isVibratePermissionEnabled(this)) {
            ActivityCompat
                    .requestPermissions(this, new String[] {Manifest.permission.VIBRATE}, 10086);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
