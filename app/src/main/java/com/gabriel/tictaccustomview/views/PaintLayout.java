package com.gabriel.tictaccustomview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.gabriel.tictaccustomview.R;

/**
 * Created by Gabriel on 20-11-2017.
 */

public class PaintLayout extends FrameLayout {

    private PaintView paintView;
    private LayoutParams layoutParams;
    private Paint paint;

    public PaintLayout(@NonNull Context context) {
        super(context);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        paintView = new PaintView(context);
        initViews(null);
    }

    public PaintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        paintView = new PaintView(context, attrs);
        initViews(attrs);
    }

    public PaintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        paintView = new PaintView(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    public PaintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        paintView = new PaintView(context, attrs, defStyleAttr, defStyleRes);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        paintView.setLayoutParams(layoutParams);

        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.PaintLayout,
                    0, 0);
            try {
                int imageResID = a.getResourceId(R.styleable.PaintLayout_imgSrc, -1);
                if (imageResID != -1)
                    setBackgroundResource(imageResID);
                else
                    setBackgroundColor(Color.TRANSPARENT);
            } finally {
                a.recycle();
            }
        }
        addView(paintView);

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPaintBox(canvas);
    }

    private void drawPaintBox(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }

    /* Public Methods *****/

    /**
     * Method to set Paint Type (Lines, Circles or Rectangles)
     *
     * @param type is the type of shape to be drawn
     */

    public void setType(String type) {
        paintView.setType(type);
    }

    public void setRadius(float radiusX, float radiusY) {
        paintView.setRadius(radiusX, radiusY);
    }

    public void setStrokeColor(int strokeColor) {
        paintView.setStrokeColor(strokeColor);
    }

    /**
     * Method to clear and reset the canvas.
     */
    public void resetPaintSpace() {
        paintView.resetPaintSpace();
    }

    public void undoOperation() {
        paintView.undoOperation();
    }
}
