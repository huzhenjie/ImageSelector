package com.scrat.app.selectorlibrary;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.scrat.app.selectorlibrary.activity.ImageSelectorActivity;

/**
 * Created by yixuanxuan on 16/10/12.
 */

public class ImageSelector {
    private ImageSelector() {
    }

    public static void show(@NonNull Activity activity, int resquestCode) {
        show(activity, resquestCode, 0);
    }

    public static void show(@NonNull Activity activity, int resquestCode, int maxCount) {
        Intent i = new Intent(activity, ImageSelectorActivity.class);
        i.putExtra("max", maxCount);
        activity.startActivityForResult(i, resquestCode);
    }
}
