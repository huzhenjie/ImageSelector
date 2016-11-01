package com.scrat.app.selectorlibrary.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
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

    public Context getContext() {
        return mConvertView.getContext();
    }

    public View getRootView() {
        return mConvertView;
    }

    public RecyclerViewHolder setText(int viewId, CharSequence content) {
        TextView view = getView(viewId);
        view.setText(content);
        return this;
    }

    public RecyclerViewHolder setText(int viewId, CharSequence content, int maxLength) {
        if (content != null && content.length() > maxLength) {
            content = content.subSequence(0, maxLength) + "...";
        }

        TextView view = getView(viewId);
        view.setText(content);
        return this;
    }

    public RecyclerViewHolder setVisibility(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public RecyclerViewHolder setVisibility(int viewId, int visible) {
        View view = getView(viewId);
        view.setVisibility(visible);
        return this;
    }

    public RecyclerViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public RecyclerViewHolder setBackground(int viewId, Drawable background) {
        View view = getView(viewId);

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(background);
        } else {
            view.setBackground(background);
        }
        return this;
    }

    public RecyclerViewHolder setImageDrawable(ImageView imageView, int drawableId) {
        imageView.setImageDrawable(mConvertView.getContext().getResources().getDrawable(drawableId));
        return this;
    }

    public RecyclerViewHolder setBackground(int viewId, int drawableId) {
        Drawable drawable = mConvertView.getContext().getResources().getDrawable(drawableId);
        return setBackground(viewId, drawable);
    }

    public RecyclerViewHolder setOnClickListener(View.OnClickListener l) {
        mConvertView.setOnClickListener(l);
        return this;
    }

    public RecyclerViewHolder setOnLongClickListener(View.OnLongClickListener l) {
        mConvertView.setOnLongClickListener(l);
        return this;
    }

    public RecyclerViewHolder setOnClickListener(int viewId, View.OnClickListener l) {
        View view = getView(viewId);
        view.setOnClickListener(l);
        return this;
    }

    public RecyclerViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener l) {
        View view = getView(viewId);
        view.setOnLongClickListener(l);
        return this;
    }

}
