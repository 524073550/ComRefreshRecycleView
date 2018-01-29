package com.zhuke.comrefreshrecycleview.test;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhuke.comrefreshrecycleview.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Apple on 2016/9/28.
 * 上拉下拉加载数据的RecyclerView
 */
public class XRefreshRecyclerView extends RecyclerView {

    private static final String TAG = "RefreshRecyclerView";

    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.default_header)
    LinearLayout defaultHeader;

    private int mFooterMeasuredHeight;

    //头的状态
    private int mHeaderState = DOWN_REFRESH_STATE;
    //下拉刷新
    private final static int DOWN_REFRESH_STATE = 0;
    //释放刷新
    private final static int RELEASE_REFRESH_STATE = 1;
    //正在加载
    private final static int REFRESHING_STATE = 2;

    private Animation animtion1;
    private Animation animtion2;


    private ViewGroup mHeaderView;
    private View mFooterView;
    //轮播图
    private View mSwitchImageView;
    private int mHeaderMeasuredHeight;
    private int disY;
    //记录按下的位置
    private int downY;
    //布局管理器
    private LinearLayoutManager lm;
    private boolean hasLoadMoreData=false;

    public XRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public XRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //初始化
    private void init() {
        initHeaderView();
        initFooterView();
        initAnimation();
    }

    //初始化头
    private void initHeaderView() {
        mHeaderView = (ViewGroup) inflate(getContext(), R.layout.header, null);
        ButterKnife.bind(this, mHeaderView);
        //隐藏进度条
        pb.setVisibility(View.INVISIBLE);
        //测量默认头的高度
        defaultHeader.measure(0,0);
        //获取测量后的高度
        mHeaderMeasuredHeight = defaultHeader.getMeasuredHeight();
        //隐藏头
        defaultHeader.setPadding(0,-mHeaderMeasuredHeight,0,0);
    }

    //初始化脚
    private void initFooterView() {
        mFooterView = inflate(getContext(), R.layout.footer, null);
        //测量
        mFooterView.measure(0,0);
        mFooterMeasuredHeight = mFooterView.getMeasuredHeight();
        //隐藏
        mFooterView.setPadding(0,-mFooterMeasuredHeight,0,0);
    }

    //初始化动画
    private void initAnimation() {
        animtion1 = createAnimation1();
        animtion2 = createAnimation2();
    }

    //从下拉刷新切换到释放刷新的动画
    private Animation createAnimation1() {
        Animation animation = new RotateAnimation(0,-180, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        return animation;
    }

    //从释放刷新切换到下拉刷新的动画
    private Animation createAnimation2() {
        Animation animation = new RotateAnimation(-180,-360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        return animation;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        //包装成XWrapAdapter
        adapter = new XWrapAdapter(mHeaderView, mFooterView, adapter);
        super.setAdapter(adapter);
    }


    //添加轮播图的方法
    public void addSwitchImageView(View view) {
        if(mHeaderView.getChildCount() == 2){
            //移除原来的轮播图
            mHeaderView.removeViewAt(1);
        }
        this.mSwitchImageView = view;
        mHeaderView.addView(view);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        lm = (LinearLayoutManager) layout;
    }



    //分发事件
    //原因：没有使用onTouchEvent()是因为dispatchTouchEvent()方法回调的频率高一些
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN://按下
                //MyLogger.i(TAG,"按下");
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE://移动
                //MyLogger.i(TAG,"移动");
                int moveY = (int) ev.getY();

                //获取RecyclerView在窗体上的位置
                int[] rvLocation = new int[2];
                getLocationInWindow(rvLocation);

                //获取轮播图在窗体上的位置
                int[] location = new int[2];
                mSwitchImageView.getLocationInWindow(location);

                //对比RecyclerView和轮播图的Y轴的值
                //如果轮播图的y坐标 < recyclerView的y坐标（也就是轮播图，没有完全显示出来）
                //此时，我们不处理头布局状态
                if(location[1] < rvLocation[1]){
                    //不处理
                    return super.dispatchTouchEvent(ev);
                }

                //条件 RecyclerView的第一个条目的下标是0 && 往下拽的行为
                disY = moveY - downY;
                int firstVisibleItemPosition = lm.findFirstVisibleItemPosition();
                int top = -mHeaderMeasuredHeight + disY;
                if(firstVisibleItemPosition == 0 && disY > 0){

                    //头布局为下拉刷新并且top>=0（头布局完全显示出来）
                    //更改头布局状为：释放刷新状态
                    if(mHeaderState == DOWN_REFRESH_STATE && top >= 0){
                        //由下拉刷新变为释放刷新
                        mHeaderState = RELEASE_REFRESH_STATE;
                        tvState.setText("释放刷新");
                        //执行动画
                        ivArrow.startAnimation(animtion1);
                        //头布局为释放刷新并且top < 0（头布局开始隐藏）
                        //更改头布局状为：下拉刷新状态
                    }else if(mHeaderState == RELEASE_REFRESH_STATE && top < 0){
                        mHeaderState = DOWN_REFRESH_STATE;
                        tvState.setText("下拉刷新");
                        //执行动画
                        ivArrow.startAnimation(animtion2);
                    }


                    //执行头的显示和隐藏操作
                    defaultHeader.setPadding(0,top,0,0);
                }
                break;
            case MotionEvent.ACTION_UP://弹起
            case MotionEvent.ACTION_CANCEL://事件取消
            case MotionEvent.ACTION_OUTSIDE://外部点击
                //MyLogger.i(TAG,"弹起");
                int mFirstVisibleItemPosition = lm.findFirstVisibleItemPosition();
                if(mFirstVisibleItemPosition == 0 && disY > 0){
                    if(mHeaderState == DOWN_REFRESH_STATE){
                        //隐藏头
                        defaultHeader.setPadding(0,-mHeaderMeasuredHeight,0,0);
                    }else if(mHeaderState == RELEASE_REFRESH_STATE){
                        //把状态切换为正在加载
                        //把头缩回置本身头的高度
                        //隐藏箭头 显示进度条
                        mHeaderState = REFRESHING_STATE;
                        tvState.setText("正在加载");
                        defaultHeader.setPadding(0,0,0,0);
                        //清除动画后控件才可以隐藏
                        ivArrow.clearAnimation();
                        ivArrow.setVisibility(View.INVISIBLE);
                        pb.setVisibility(View.VISIBLE);
                        //加载最新的数据
                        mOnRefreshListener.onRefresh();
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    //加载最新数据的方法
    public interface OnRefreshListener{
        void onRefresh();
    }

    private OnRefreshListener mOnRefreshListener;
    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener){
        this.mOnRefreshListener = mOnRefreshListener;
    }
    //隐藏头
    public void hideHeaderView(boolean loadState){
        //隐藏进度条，显示箭头，修改状态，修改文字内容，通过数据加载成功的状态去判断是否更改上次加载数据的实现
        pb.setVisibility(View.INVISIBLE);
        ivArrow.setVisibility(View.VISIBLE);
        mHeaderState = DOWN_REFRESH_STATE;
        tvState.setText("下拉刷新");
        defaultHeader.setPadding(0,-mHeaderMeasuredHeight,0,0);
        if(loadState){
            String dateStr = DateFormat.getDateFormat(getContext()).format(System.currentTimeMillis());
            String timeStr = DateFormat.getTimeFormat(getContext()).format(System.currentTimeMillis());
            tvTime.setText(dateStr+" " + timeStr);
        }
        //刷新数据
        getAdapter().notifyDataSetChanged();
    }

    //隐藏脚
    public void hideFooterView(){
        hasLoadMoreData = false;
        mFooterView.setPadding(0,-mFooterMeasuredHeight,0,0);
        //刷新数据
        getAdapter().notifyDataSetChanged();
    }

    //滑动状态改变
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state){
            case RecyclerView.SCROLL_STATE_DRAGGING://滑动|拖拽
                MyLogger.i(TAG,"DRAGGING");
                break;
            case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动|飞行
                MyLogger.i(TAG,"SETTLING");
                break;
            case RecyclerView.SCROLL_STATE_IDLE://静止
                MyLogger.i(TAG,"IDLE");
                break;
        }

        //在静止的状态下   && 必须是最后显示的条目就是RecyclerView的最后一个条目 && 没有在加载更多的数据
        boolean isState = state == RecyclerView.SCROLL_STATE_IDLE;
        //最后一个条目显示的下标
        int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
        boolean isLastVisibleItem = lastVisibleItemPosition == getAdapter().getItemCount() - 1;
        if(isState && isLastVisibleItem && !hasLoadMoreData && mOnLoadMoreListener != null){
            hasLoadMoreData = true;
            //显示脚
            mFooterView.setPadding(0,0,0,0);
            //滑动到显示的脚的位置
            smoothScrollToPosition(lastVisibleItemPosition);
            //加载数据
            mOnLoadMoreListener.onLoadMore();
        }
    }


    //加载更多的接口
    public interface OnLoadMoreListener{
        void onLoadMore();
    }
    private OnLoadMoreListener mOnLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener){
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}
