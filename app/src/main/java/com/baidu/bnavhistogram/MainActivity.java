package com.baidu.bnavhistogram;

import java.util.ArrayList;
import java.util.List;

import com.baidu.bnavhistogram.data.HistogramItem;
import com.baidu.histogram.utils.VibrateHelper;
import com.baidu.histogram.view.BaseSizeDefiner;
import com.baidu.histogram.view.HistogramBaseAdapter;
import com.baidu.histogram.view.HistogramBottomHighLightView;
import com.baidu.histogram.view.HistogramBottomViewBaseAdapter;
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

    private void init() {
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
        mSizeDefiner = new HistogramSizeDefiner(this);

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
            public HistogramBaseAdapter getHistogramAdapter() {
                return mHistogramAdapter;
            }

            @Override
            public HistogramBottomHighLightView getHistogramBottomHighLightView() {
                return mTimeView;
            }

            @Override
            public HistogramBottomViewBaseAdapter getHistogramBottomHighLightViewAdapter() {
                return mTimeAdapter;
            }

            @Override
            public BaseSizeDefiner getSizeDefiner() {
                return mSizeDefiner;
            }

            @Override
            public boolean enableSelectItemMoreWidth() {
                return true;
            }


        }, new HistogramViewHelper.DataCallback() {
            @Override
            public boolean isEmptyItem(int index) {
                return false;
            }

            @Override
            public int getTotalItemCount() {
                if(mDataList == null){
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
