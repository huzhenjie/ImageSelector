package com.scrat.app.selectorlibrary.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.scrat.app.selectorlibrary.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by yixuanxuan on 16/10/13.
 */

public class ImagePreviewActivity extends AppCompatActivity {

    private static final String EXTRA_PATHS = "paths";
    private static final String EXTRA_FINISH = "finish";

    public static void show(Activity activity, int requestCode, ArrayList<String> paths) {
        Intent i = new Intent(activity, ImagePreviewActivity.class);
        i.putStringArrayListExtra(EXTRA_PATHS, paths);
        activity.startActivityForResult(i, requestCode);
    }

    private ViewPager mViewPager;
    private ImageButton mCheckedBtn;
    private TextView mTitleTv;
    private TextView mFinishTv;
    private TextView mFinishTipTv;
    private View mFinishTipView;
    private ArrayList<String> mPaths;
    private int mPosition;

    private Set<Integer> mUnCheckPos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);

        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        setActivityResult(false);
        super.onBackPressed();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mCheckedBtn = (ImageButton) findViewById(R.id.ib_check);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mFinishTv = (TextView) findViewById(R.id.tv_finish);
        mFinishTipTv = (TextView) findViewById(R.id.tv_finish_tip);
        mFinishTipView = findViewById(R.id.fl_finish_tip);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                animateRevealColorFromCoordinates(mViewPager, android.R.color.black, 0, mViewPager.getMeasuredHeight());
            }
        });
    }

    private void animateRevealColorFromCoordinates(ViewGroup viewRoot, @ColorRes int color, int x, int y) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP)
            return;

        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        viewRoot.setBackgroundColor(ContextCompat.getColor(this, color));
        anim.setDuration(300L);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    private void initData() {
        mPaths = getIntent().getStringArrayListExtra(EXTRA_PATHS);
        MyPagerAdapter adapter = new MyPagerAdapter(mPaths);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        mUnCheckPos = new HashSet<>();
        updateCheckStatus();
    }

    public void cancelPreview(View v) {
        setActivityResult(false);
        finish();
    }

    public void checked(View v) {
        if (mUnCheckPos.contains(mPosition)) {
            mUnCheckPos.remove(mPosition);
        } else {
            mUnCheckPos.add(mPosition);
        }
        updateCheckStatus();
    }

    public void finishSelected(View v) {
        if (mUnCheckPos.size() == mPaths.size())
            return;

        setActivityResult(true);
        finish();
    }

    private void setActivityResult(boolean isFinish) {
        int i = 0;
        for (Iterator<String> iterator = mPaths.iterator(); iterator.hasNext();) {
            iterator.next();
            if (mUnCheckPos.contains(i)) {
                iterator.remove();
            }
            i ++;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_PATHS, mPaths);
        intent.putExtra(EXTRA_FINISH, isFinish);
        setResult(RESULT_OK, intent);
    }

    public static ArrayList<String> parsePaths(Intent intent) {
        if (intent == null)
            return new ArrayList<>();

        return intent.getStringArrayListExtra(EXTRA_PATHS);
    }

    public static boolean isFinsh(Intent intent) {
        return intent != null && intent.getBooleanExtra(EXTRA_FINISH, false);
    }

    private void updateCheckStatus() {
        int checkedBtnDrawableId = mUnCheckPos.contains(mPosition) ? R.drawable.ic_round_check : R.drawable.ic_round_check_fill;
        mCheckedBtn.setImageDrawable(ContextCompat.getDrawable(this, checkedBtnDrawableId));

        if (mUnCheckPos.size() == mPaths.size()) {
            mFinishTipView.setVisibility(View.GONE);
            mFinishTv.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        } else {
            mFinishTipView.setVisibility(View.VISIBLE);
            int totalSelect = mPaths.size() - mUnCheckPos.size();
            mFinishTipTv.setText(String.valueOf(totalSelect));
            mFinishTv.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
        mTitleTv.setText(String.format(Locale.getDefault(), "%d/%d", mPosition + 1, mPaths.size()));
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mPosition = position;
            updateCheckStatus();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private static class MyPagerAdapter extends PagerAdapter {
        private List<String> mPaths;
        /*package*/ MyPagerAdapter(List<String> paths) {
            mPaths = paths;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View view = inflater.inflate(R.layout.image_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            String path = mPaths.get(position);

            Glide.with(container.getContext())
                    .load(path)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mPaths.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
