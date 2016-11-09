package com.scrat.app.selectorlibrary.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.scrat.app.selectorlibrary.R;
import com.scrat.app.selectorlibrary.adapter.SelectorAdapter;
import com.scrat.app.selectorlibrary.model.ISelectImageItem;
import com.scrat.app.selectorlibrary.model.Img;
import com.scrat.app.selectorlibrary.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yixuanxuan on 16/10/12.
 */

public class ImageSelectorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_CODE_PREVIEW = 1;
    private static final int READ_EXTERNAL_STORAGE_CODE = 2;
    private static final String EXTRA_KEY_MAX = "max";
    private static final String EXTRA_KEY_DATA = "data";

    private RecyclerView mRecyclerView;
    private SelectorAdapter mAdapter;
    private List<Integer> mSelectSortPosList;
    private TextView mFinishTv;
    private TextView mPreviewTv;
    private TextView mFinishTipTv;
    private View mFinishTipView;
    private int mMaxImgCount;

    public static void show(Activity activity, int requestCode, int maxCount) {
        Intent i = new Intent(activity, ImageSelectorActivity.class);
        i.putExtra(EXTRA_KEY_MAX, maxCount);
        activity.startActivityForResult(i, requestCode);
    }

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
        mSelectSortPosList = new ArrayList<>();
        mAdapter = new SelectorAdapter(onItemClickListener);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 10, false, mFinishTv.getMeasuredHeight(), mFinishTv.getMeasuredHeight() + 10));
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
        mMaxImgCount = getIntent().getIntExtra(EXTRA_KEY_MAX, 0);
    }

    private SelectorAdapter.OnItemClickListener onItemClickListener = new SelectorAdapter.OnItemClickListener() {
        @Override
        public int onItemClick(ISelectImageItem item, int pos) {
            if (item.isChecked()) {
                mSelectSortPosList.remove((Integer) pos);
            } else {
                if (mMaxImgCount > 0 && mSelectSortPosList.size() >= mMaxImgCount) {
                    Toast.makeText(ImageSelectorActivity.this, String.format(getString(R.string.image_selector_limit_of_img_error), mMaxImgCount), Toast.LENGTH_LONG).show();
                    return -1;
                }
                mSelectSortPosList.add(pos);
            }
            refreshFinishBtn();
            return mSelectSortPosList.size();
        }
    };

    private void refreshFinishBtn() {
        int totalSelect = mSelectSortPosList.size();
        if (totalSelect > 0) {
            mFinishTipView.setVisibility(View.VISIBLE);
            mFinishTipTv.setText(String.valueOf(totalSelect));
            int white = ContextCompat.getColor(this, android.R.color.white);
            mFinishTv.setTextColor(white);
            mPreviewTv.setVisibility(View.VISIBLE);
        } else {
            int darkerGray = ContextCompat.getColor(this, android.R.color.darker_gray);
            mFinishTv.setTextColor(darkerGray);
            mPreviewTv.setVisibility(View.GONE);
            mFinishTipView.setVisibility(View.GONE);
        }
    }

    public void cancelSelected(View v) {
        finish();
    }

    public void finishSelected(View v) {
        int totalSelect = mSelectSortPosList.size();
        if (totalSelect == 0)
            return;

        ArrayList<String> paths = mAdapter.getPathByPosList(mSelectSortPosList);
        finishActivity(paths);
    }

    private void finishActivity(ArrayList<String> paths) {
        Intent i = new Intent();
        i.putStringArrayListExtra(EXTRA_KEY_DATA, paths);
        setResult(RESULT_OK, i);
        finish();
    }

    public static List<String> getImagePaths(Intent data) {
        return data == null ? Collections.<String>emptyList() : data.getStringArrayListExtra(EXTRA_KEY_DATA);
    }

    public void preview(View v) {
        int totalSelect = mSelectSortPosList.size();
        if (totalSelect == 0)
            return;

        ArrayList<String> paths = mAdapter.getPathByPosList(mSelectSortPosList);
        ImagePreviewActivity.show(this, REQUEST_CODE_PREVIEW, paths);
    }

    private void initData() {
        if (permissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE)) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_CODE:
                onRequestReadExternalStorageResult(permissions, grantResults);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void onRequestReadExternalStorageResult(String[] permissions, int[] grantResults) {
        if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initData();
        } else {
            Toast.makeText(this, R.string.image_selector_authorization_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean permissionGranted(String permission, int requestCode) {
        int permissionCode = ContextCompat.checkSelfPermission(this, permission);
        if (permissionCode != PackageManager.PERMISSION_GRANTED) {
            try {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        return true;
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
        resetSelector();
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

    private void resetSelector() {
        mSelectSortPosList.clear();
        refreshFinishBtn();
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
                mAdapter.replaceCheckDatas(paths, mSelectSortPosList);
                refreshFinishBtn();
            }
        }
    }
}
