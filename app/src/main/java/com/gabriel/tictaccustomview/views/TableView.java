package com.gabriel.tictaccustomview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.gabriel.tictaccustomview.R;

import java.util.ArrayList;

/**
 * Created by Gabriel on 09-11-2017.
 */

public class TableView extends View {
    private float X_PARTITION_RATIO, Y_PARTITION_RATIO;
    private int COLUMN_SIZE = 2, ROW_SIZE = 1;
    private float ROW_HEIGHT = 60, TABLE_HEIGHT;
    int width;
    private Rect[][] rects;
    private String[][] rectData;

    private Paint paint = new Paint();
    private Paint textPaint = new Paint();

    private ArrayList<String[]> rowContents;

    public TableView(Context context) {
        super(context);
        init(null);
    }

    public TableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        initRects();
    }

    private void init(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TableView, 0, 0);
            try {
                COLUMN_SIZE = ta.getInteger(R.styleable.TableView_columnSize, 2);
                ROW_HEIGHT = ta.getFloat(R.styleable.TableView_rowHeight, 60f);
            } finally {
                ta.recycle();
            }
        }

        rowContents = new ArrayList<>();

        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        textPaint = new Paint();
        textPaint.setColor(paint.getColor());
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextSize(getResources().getDisplayMetrics().density * 15);


        TABLE_HEIGHT = ROW_HEIGHT * ROW_SIZE;
        rectData = new String[ROW_SIZE][COLUMN_SIZE];
        initRects();
    }

    private void initRects() {
        rects = new Rect[ROW_SIZE][COLUMN_SIZE];
        X_PARTITION_RATIO = 1f / (float) COLUMN_SIZE;
        Y_PARTITION_RATIO = 1f / (float) ROW_SIZE;

        int x_unit = (int) (getWidth() * X_PARTITION_RATIO);
        int y_unit = (int) (TABLE_HEIGHT * Y_PARTITION_RATIO);

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COLUMN_SIZE; j++) {
                rects[i][j] = new Rect(j * x_unit, i * y_unit,
                        (j + 1) * x_unit, (i + 1) * y_unit);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHorizontalLines(canvas);
        drawVerticalLines(canvas);
        drawRectStates(canvas);
    }

    private void drawVerticalLines(Canvas canvas) {
        for (int i = 0; i <= COLUMN_SIZE; i++) {
            canvas.drawLine(i * getWidth() * X_PARTITION_RATIO, 0f,
                    i * getWidth() * X_PARTITION_RATIO, TABLE_HEIGHT, paint);
        }
    }

    private void drawHorizontalLines(Canvas canvas) {
        for (int i = 0; i <= ROW_SIZE; i++) {
            canvas.drawLine(0f, i * TABLE_HEIGHT * Y_PARTITION_RATIO,
                    getWidth(), i * TABLE_HEIGHT * Y_PARTITION_RATIO, paint);
        }
    }

    private void drawRectStates(Canvas canvas) {
        int i = 0;
        for (String[] rectDatum : rectData) {
            int j = 0;
            rectDatum = rectData[i];
            for (String s : rectDatum) {
                if (s != null)
                    drawTextInsideRect(canvas, rects[i][j], s);
                j++;
            }
            i++;
        }
    }

    private void drawTextInsideRect(Canvas canvas, Rect rect, String text) {
        float xOffset = textPaint.measureText(text) * 0.5f;
        float yOffset = textPaint.getFontMetrics().ascent * -0.4f;
        float textX = (rect.exactCenterX()) - xOffset;
        float textY = (rect.exactCenterY()) + yOffset;
        canvas.drawText(text, textX, textY, textPaint);
    }


    public void insertRow(String... content) {
        ROW_SIZE++;
        TABLE_HEIGHT += ROW_HEIGHT;

        rowContents.add(content);
        invalidate();
        resetData();
        initRects();
    }

    private void resetData() {
        rectData = new String[ROW_SIZE][COLUMN_SIZE];
        int j = 0;
        for (String[] rowContent : rowContents) {
            for (int i = 0; i < COLUMN_SIZE && i < rowContent.length; i++) {
                if (rowContent[i] != null)
                    rectData[j][i] = rowContent[i];
            }
            j++;
        }
    }

    public void setColumnTitle(String... title){
        rowContents.add(title);
    }


}
