package com.scrat.app.selectorlibrary.model;

/**
 * Created by yixuanxuan on 16/10/12.
 */

public class Img implements ISelectImageItem {
    private String path;
    private boolean isChecked;

    public Img setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String getImgPath() {
        return path;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

}
