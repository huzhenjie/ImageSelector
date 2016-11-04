package com.scrat.app.imageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.scrat.app.selectorlibrary.ImageSelector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMG = 1;
    private static final int MAX_SELECT_COUNT = 9;

    private TextView mContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mContentTv = (TextView) findViewById(R.id.content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_IMG) {
            showContent(data);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showContent(Intent data) {
        List<String> paths = ImageSelector.getImagePaths(data);
        if (paths.isEmpty()) {
            mContentTv.setText(R.string.image_selector_select_none);
            return;
        }

        mContentTv.setText(paths.toString());
    }

    public void selectImg(View v) {
        ImageSelector.show(this, REQUEST_CODE_SELECT_IMG, MAX_SELECT_COUNT);
    }
}
