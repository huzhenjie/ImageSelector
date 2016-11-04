package com.scrat.app.selectorlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yixuanxuan on 16/7/1.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;
    private View mConvertView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getRootView() {
        return mConvertView;
    }

    public RecyclerViewHolder setText(int viewId, CharSequence content) {
        TextView view = getView(viewId);
        view.setText(content);
        return this;
    }

    public RecyclerViewHolder setVisibility(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public RecyclerViewHolder setOnClickListener(View.OnClickListener l) {
        mConvertView.setOnClickListener(l);
        return this;
    }

}
