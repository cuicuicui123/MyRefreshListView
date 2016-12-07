package com.example.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Cui on 2016/12/5.
 */


public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    private Context mContext;
    private View mHeaderView;
    private TextView mHeaderTv;
    private CustomDripView mCustomDripView;
    private ProgressBar mProgressBar;

    private int mHeaderHeight;
    private int mState;
    private static final int PULL_TO_REFRESH = 0;
    private static final int RELEASE_TO_REFRESH = 1;
    private static final int DONE = 2;
    private static final int REFRESHING = 3;

    private boolean mIsRefreshable;
    private int mScale = 4;//距离缩放倍数，手指移动距离大于4倍headerView高度代表可以进行刷新
    private float mStartY;
    private OnRefreshListener mOnRefreshListener;


    public RefreshListView(Context context) {
        super(context);
        init(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.header_view, null);
        mHeaderTv = (TextView) mHeaderView.findViewById(R.id.tv_header);
        mCustomDripView = (CustomDripView) mHeaderView.findViewById(R.id.drip_view);
        mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(GONE);
        measureView(mHeaderView);
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -1 * mHeaderHeight, 0, 0);//通过设置padding达到将头部隐藏的效果
        mHeaderView.invalidate();
        addHeaderView(mHeaderView, null, false);
        mIsRefreshable = true;
    }


    //测量View宽高
    private void measureView(View child){
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if(params == null){
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if(lpHeight > 0){
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        }else{
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsRefreshable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartY = ev.getY();
                    mState = PULL_TO_REFRESH;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float distance = (ev.getY() - mStartY) / mScale;
                    if (distance > 0) {
                        if (distance < mHeaderHeight) {
                            mHeaderTv.setText("下拉刷新");
                            mState = PULL_TO_REFRESH;
                            mHeaderView.setPadding(0, (int) (distance - mHeaderHeight), 0, 0);//下拉过程中慢慢将头部展示出来
                            changeHeaderViewHeight((int) distance);//改变头部高度，用于展示水滴效果
                        } else {
                            mState = RELEASE_TO_REFRESH;
                            mHeaderTv.setText("松开刷新");
                            mHeaderView.setPadding(0, 0, 0, 0);
                            changeHeaderViewHeight((int) distance);//随着下拉增加头部高度，用于展示水滴
                        }
                        mCustomDripView.setDistance(distance);
                        mHeaderView.invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mState == RELEASE_TO_REFRESH) {
                        mState = REFRESHING;
                    } else {
                        if (mState == PULL_TO_REFRESH) {
                            mState = DONE;
                        }
                    }
                    handleHeaderViewByState();
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void handleHeaderViewByState(){
        switch (mState) {
            case REFRESHING:
                mHeaderTv.setText("正在刷新");
                changeHeaderViewHeight(mHeaderHeight);
                mCustomDripView.setRefreshing(true);
                mCustomDripView.setVisibility(GONE);
                mProgressBar.setVisibility(VISIBLE);
                mHeaderView.invalidate();
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
                break;
            case DONE:
                mHeaderView.setPadding(0, -1 * mHeaderHeight, 0, 0);
                changeHeaderViewHeight(0);
                mCustomDripView.initDistance();
                mHeaderView.invalidate();
                break;
        }
    }

    private void changeHeaderViewHeight(int height){
        ViewGroup.LayoutParams lp = mHeaderView.getLayoutParams();
        lp.height = height;
        mHeaderView.setLayoutParams(lp);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            mIsRefreshable = true;
        } else {
            mIsRefreshable = false;
        }
    }

    public void refreshComplete(){
        mHeaderTv.setText("下拉刷新");
        mHeaderView.setPadding(0, -1 * mHeaderHeight, 0, 0);
        changeHeaderViewHeight(0);
        mCustomDripView.initDistance();
        mCustomDripView.setRefreshing(false);
        mCustomDripView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mHeaderView.invalidate();
    }

    public interface OnRefreshListener{
        void onRefresh();
    }

    /**
     * 设置刷新操作回调方法
     * @param onRefreshListener
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        mOnRefreshListener = onRefreshListener;
    }

}
