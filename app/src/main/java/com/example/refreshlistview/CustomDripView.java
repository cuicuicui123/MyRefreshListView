package com.example.refreshlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Cui on 2016/12/6.
 */

public class CustomDripView extends View {
    private Context mContext;
    private int mGrey;
    private float mDistance;
    private Paint mPaint;
    private Path mPath;
    private boolean mIsRefreshing = false;
    private float mRadios;

    public CustomDripView(Context context) {
        super(context);
        init(context);
    }

    public CustomDripView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomDripView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mGrey = 0xffaaaaaa;
        mPaint = new Paint();
        mPaint.setColor(mGrey);
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);

        mPath = new Path();
    }

    public void setDistance(float distance){
        mDistance = distance;
        invalidate();
    }

    public void setRefreshing(boolean refreshing){
        mIsRefreshing = refreshing;
    }

    public void initDistance(){
        mDistance = 0;
        invalidate();
    }

    //每次布局位置改变之后会调用onLayout重新布局
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mPath.reset();
            mRadios = getWidth() / 3 - mDistance / 30;//最大半径为宽度三分之一
            //使用贝塞尔函数绘制曲线
            float startX = getWidth() / 2;
            float startY = getWidth() / 2;
            float x1 = startX - mRadios;
            float y1 = startY;
            float x4 = startX + mRadios;
            float y4 = startY;

            float x2 = startX - mRadios / 2;
            float y2 = getHeight() - mRadios;
            float x3 = startX + mRadios / 2;
            float y3 = getHeight() - mRadios;
            mPath.moveTo(x1, y1);
            mPath.quadTo(startX, startY * 2, x2 , y2);
            mPath.lineTo(x3 , y3);
            mPath.quadTo(startX, startY * 2, x4, y4);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mIsRefreshing) {
            canvas.drawCircle(getWidth() /  2, getWidth() / 2, mRadios, mPaint);//顶部的圆
            canvas.drawPath(mPath, mPaint);
            canvas.drawCircle(getWidth() / 2, getHeight() - mRadios, mRadios / 2, mPaint);//底部的圆
        } else {
            canvas.drawCircle(getWidth() /  2, getWidth() / 2, getWidth() / 3, mPaint);
        }
    }
}
