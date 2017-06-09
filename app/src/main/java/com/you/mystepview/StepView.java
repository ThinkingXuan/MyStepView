package com.you.mystepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.sax.StartElementListener;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by youxuan on 2017/6/9 0009.
 * http://mp.weixin.qq.com/s/665GBpFPHqvXva7ylmcg3g
 */

public class StepView extends View {
    private static final int START_STEP = 1;
    private final List<String> mSteps = new ArrayList<>();
    private int mCurrentStep = START_STEP;

    private int mCircleColor;
    private int mTextColor;
    private int mSelectedColor;
    private int mFillRadius;
    private int mStrokeWidth;
    private int mLineWidth;
    private int mDrawablePadding;


    private Paint mPaint;

    public StepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StepView, 0, R.style.StepView);
        mCircleColor = ta.getColor(R.styleable.StepView_svCircleColor, 0);
        mSelectedColor = ta.getColor(R.styleable.StepView_svSelectedColor, 0);
        mTextColor = ta.getColor(R.styleable.StepView_svTextColor, 0);
        mFillRadius = ta.getDimensionPixelSize(R.styleable.StepView_svFillRadius, 0);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.StepView_svStrokeWidth, 0);
        mLineWidth = ta.getDimensionPixelSize(R.styleable.StepView_svLineWidth, 0);
        mDrawablePadding = ta.getDimensionPixelSize(R.styleable.StepView_svDrawablePadding, 0);
        final int textSize = ta.getDimensionPixelSize(R.styleable.StepView_svTextSize, 0);
        ta.recycle();

        //anti_alias_flag dither_flag 抗锯齿，抗抖动。是文字变得更加圆润
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

        //预览布局显示，运行是不显示
        if (isInEditMode()) {
            String[] steps = {"Step 1", "Step 2", "Step 3"};
            setSteps(Arrays.asList(steps));
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //当布局的height为Wrap_content时，计算View的高度
        if (heightMode == MeasureSpec.AT_MOST) {
            final int fontHeight = (int) Math.ceil(mPaint.descent() - mPaint.ascent());
            height = getPaddingTop() + getPaddingBottom() +
                    (mFillRadius + mStrokeWidth) * 2 + mDrawablePadding + fontHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int stepSize = mSteps.size();
        if (stepSize == 0) {
            return;
        }

        final int width = getWidth();

        final float ascent = mPaint.ascent();
        final float descent = mPaint.descent();

        final int fontHeight = (int) Math.ceil(descent - ascent);       //获取字体高度
        final int halfFontHeightOffset = -(int) (ascent + descent) / 2;
        final int bigRadius = mFillRadius + mStrokeWidth;
        final int startCircleY = getPaddingTop() + bigRadius;
        final int childWidth = width / stepSize;
        for (int i = 1; i <= stepSize; i++) {
            drawableStep(canvas, i, halfFontHeightOffset, fontHeight, bigRadius, childWidth * i - childWidth / 2, startCircleY);
        }

        final int halfLineLength = childWidth / 2 - bigRadius;
        for (int i = 1; i < stepSize; i++) {
            final int lineCenterX = childWidth * i;
            drawableLine(canvas, lineCenterX - halfLineLength, lineCenterX + halfLineLength, startCircleY);
        }
    }

    private void drawableStep(Canvas canvas, int step, int halfFontHeightOffset, int fontHeight, int bigRadius, int circleCenterX, int circleCenterY) {

        final String text = mSteps.get(step - 1);
        final boolean isSelected = step == mCurrentStep;
        if (isSelected) {
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mCircleColor);
            canvas.drawCircle(circleCenterX, circleCenterY, mFillRadius + mStrokeWidth / 2, mPaint);

            mPaint.setColor(mSelectedColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(circleCenterX, circleCenterY, mFillRadius, mPaint);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mCircleColor);
            canvas.drawCircle(circleCenterX, circleCenterY, bigRadius, mPaint);
        }
        mPaint.setFakeBoldText(true);
        mPaint.setColor(Color.WHITE);
        String number = String.valueOf(step);
        canvas.drawText(number, circleCenterX, circleCenterY + halfFontHeightOffset, mPaint);

        mPaint.setFakeBoldText(false);
        mPaint.setColor(isSelected ? mSelectedColor : mTextColor);
        canvas.drawText(text, circleCenterX, circleCenterY + bigRadius + mDrawablePadding + fontHeight / 2, mPaint);

    }

    private void drawableLine(Canvas canvas, int startX, int endX, int centerY) {
        mPaint.setColor(mCircleColor);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(startX, centerY, endX, centerY, mPaint);
    }

    public void setSteps(List<String> steps) {
        mSteps.clear();
        if (steps != null) {
            mSteps.addAll(steps);
        }

        selectedStep(START_STEP);
    }

    public void selectedStep(int step) {

        //屌屌,用三元表达式,代替If语句
        final int selected = step < START_STEP ?
                START_STEP : (step > mSteps.size() ? mSteps.size() : step);

        mCurrentStep = selected;
        invalidate();
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    public int getStepCount() {
        return mSteps.size();
    }
}
