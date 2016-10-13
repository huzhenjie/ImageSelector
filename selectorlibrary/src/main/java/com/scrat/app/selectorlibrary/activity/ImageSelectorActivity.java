package com.scrat.app.selectorlibrary.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.scrat.app.selectorlibrary.R;
import com.scrat.app.selectorlibrary.adapter.SelectorAdapter;
import com.scrat.app.selectorlibrary.model.ISelectImageItem;
import com.scrat.app.selectorlibrary.model.Img;
import com.scrat.app.selectorlibrary.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yixuanxuan on 16/10/12.
 */

public class ImageSelectorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_CODE_PREVIEW = 1;

    private RecyclerView mRecyclerView;
    private SelectorAdapter mAdapter;
    private List<Integer> selectSortPosList;
    private TextView mFinishTv;
    private TextView mPreviewTv;
    private TextView mFinishTipTv;
    private View mFinishTipView;
    private int mMaxImgCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        initView();
        initData();
    }

    private void initView() {
        mPreviewTv = (TextView) findViewById(R.id.tv_preview);
        mFinishTv = (TextView) findViewById(R.id.tv_finish);
        mFinishTipTv = (TextView) findViewById(R.id.tv_finish_tip);
        mFinishTipView = findViewById(R.id.fl_finish_tip);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        selectSortPosList = new ArrayList<>();
        mAdapter = new SelectorAdapter(onItemClickListener);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        ViewTreeObserver vto = mPreviewTv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 10, false, mPreviewTv.getMeasuredHeight(), mPreviewTv.getMeasuredHeight() + 10));
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mPreviewTv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        mMaxImgCount = getIntent().getIntExtra("max", 0);
    }

    private SelectorAdapter.OnItemClickListener onItemClickListener = new SelectorAdapter.OnItemClickListener() {
        @Override
        public boolean onItemClick(ISelectImageItem item, int pos) {
            if (item.isChecked()) {
                selectSortPosList.remove((Integer) pos);
            } else {
                if (mMaxImgCount > 0 && selectSortPosList.size() >= mMaxImgCount) {
                    Toast.makeText(ImageSelectorActivity.this, "你最多只能选择" + mMaxImgCount + "张照片", Toast.LENGTH_LONG).show();
                    return false;
                }
                selectSortPosList.add(pos);
            }
            refreshFinishBtn();
            return true;
        }
    };

    private void refreshFinishBtn() {
        int totalSelect = selectSortPosList.size();
        if (totalSelect > 0) {
            mFinishTipView.setVisibility(View.VISIBLE);
            mFinishTipTv.setText(String.valueOf(totalSelect));
            mFinishTv.setTextColor(getResources().getColor(android.R.color.white));
            mPreviewTv.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            mFinishTv.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mPreviewTv.setTextColor(getResources().getColor(android.R.color.darker_gray));
            mFinishTipView.setVisibility(View.GONE);
        }
    }

    public void cancelSelected(View v) {

        finish();
    }

    public void finishSelected(View v) {
        int totalSelect = selectSortPosList.size();
        if (totalSelect == 0)
            return;

        ArrayList<String> paths = mAdapter.getPathByPosList(selectSortPosList);
        finishActivity(paths);
    }

    private void finishActivity(ArrayList<String> paths) {
        Intent i = new Intent();
        i.putStringArrayListExtra("data", paths);
        setResult(RESULT_OK, i);
        finish();
    }

    public void preview(View v) {
        int totalSelect = selectSortPosList.size();
        if (totalSelect == 0)
            return;

        ArrayList<String> paths = mAdapter.getPathByPosList(selectSortPosList);
        ImagePreviewActivity.show(this, REQUEST_CODE_PREVIEW, paths);
    }

    private void initData() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private static final String[] IMAGE_PROJECTION = new String[]{
            MediaStore.MediaColumns.DATA
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String select = MediaStore.Images.Media.SIZE + ">0";
        return new CursorLoader(this, uri, IMAGE_PROJECTION, select, null, MediaStore.Images.Media.DISPLAY_NAME + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0)
            return;

        List<Img> imgs = new ArrayList<>();
        while (data.moveToNext()) {
            String path = data.getString(0);
            Img img = new Img().setPath(path);
            imgs.add(img);
        }
        mAdapter.replaceDatas(imgs);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PREVIEW) {
            ArrayList<String> paths = ImagePreviewActivity.parsePaths(data);
            boolean isFinish = ImagePreviewActivity.isFinsh(data);
            if (isFinish) {
                finishActivity(paths);
            } else {
                mAdapter.replaceCheckDatas(paths, selectSortPosList);
                refreshFinishBtn();
            }
        }
    }
}
