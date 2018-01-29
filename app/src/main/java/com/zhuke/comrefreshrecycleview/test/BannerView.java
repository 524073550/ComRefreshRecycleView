package com.zhuke.comrefreshrecycleview.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.zhuke.comrefreshrecycleview.R;
import java.util.List;

/**
 * Created by 15653 on 2018/1/29.
 */

public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener{

    private ViewPager viewPager;

    //网络图片地址
    private List<String> imageUrls;

    //指示点的容器
    private LinearLayout pointLayout;

    //当前页面位置
    private int currentItem;

    //自动播放时间
    private int autoPlayTime = 2000;

    //是否自动播放
    private boolean isAutoPlay;

    //是否是一张图片
    private boolean isOneImage;

    //监听事件
    private OnBannerItemClick onBannerItemClick;


    //这里利用handler实现循环播放
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            currentItem++;
            currentItem = currentItem % (imageUrls.size() + 2);
            viewPager.setCurrentItem(currentItem);
            handler.sendEmptyMessageDelayed(0,autoPlayTime);
            return false;
        }

    });



    public BannerView(@NonNull Context context) {
        this(context,null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,R.styleable.BannerView,0,0);
        //默认自动播放
        isAutoPlay = typedArray.getBoolean(R.styleable.BannerView_isAutoPlay,true);
        typedArray.recycle();

        viewPager = new ViewPager(getContext());
        pointLayout = new LinearLayout(getContext());
        //添加监听事件
        viewPager.addOnPageChangeListener(this);
        //利用布局属性将指示器容器放置底部并居中
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = 60;

        addView(viewPager);
        addView(pointLayout,params);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentItem = position;
        if (!isOneImage) {
            switchToPoint(toRealPosition(position));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //根据滑动松开后的状态，去判断当前的current 并跳转到指定current
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            int current = viewPager.getCurrentItem();
            int lastReal = viewPager.getAdapter().getCount() - 2;
            if (current == 0) {
                viewPager.setCurrentItem(lastReal, false);
            } else if (current == lastReal + 1) {
                viewPager.setCurrentItem(1, false);
            }
        }
    }

    //配置viewpager适配器
    private class BannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageUrls.size() + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBannerItemClick != null) {
                        onBannerItemClick.onItemClick(toRealPosition(position));
                    }
                }
            });
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getContext()).load(imageUrls.get(toRealPosition(position))).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //添加网络图片
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        if (imageUrls.size() <= 1) {
            isOneImage = true;
        }else {
            isOneImage = false;
        }
        initViewPager();
    }

    //加载viewPager
    private void initViewPager() {
        if (!isOneImage) {
            //添加指示点
            addPoints();
        }
        BannerAdapter adapter = new BannerAdapter();
        viewPager.setAdapter(adapter);
        //默认当前图片
        viewPager.setCurrentItem(1);
        //判断是否自动播放和是否是一张图片的情况
        if (isAutoPlay && !isOneImage) {
            handler.sendEmptyMessageDelayed(0,autoPlayTime);
        }
    }

    //添加指示点
    private void addPoints() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);
        ImageView imageView;
        int length = imageUrls.size();
        for (int i = 0; i < length; i++) {
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(lp);
            imageView.setImageResource(R.drawable.select_point_bg);
            pointLayout.addView(imageView);
        }
        switchToPoint(0);
    }

    //切换指示器
    private void switchToPoint(int currentPoint) {
        for (int i = 0; i < pointLayout.getChildCount(); i++) {
            pointLayout.getChildAt(i).setEnabled(false);
        }
        pointLayout.getChildAt(currentPoint).setEnabled(true);
    }

    //返回真实的位置
    private int toRealPosition(int position) {
        int realPosition;
        if (imageUrls.size() > 0) {
            realPosition = (position - 1) % imageUrls.size();
            if (realPosition < 0)
                realPosition += imageUrls.size();
        }else{
            realPosition = 0;
        }
        return realPosition;
    }

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    public void setOnBannerItemClick(OnBannerItemClick onBannerItemClick) {
        this.onBannerItemClick = onBannerItemClick;
    }

    //添加监听事件回调
    public interface OnBannerItemClick{
        void onItemClick(int position);
    }
}

