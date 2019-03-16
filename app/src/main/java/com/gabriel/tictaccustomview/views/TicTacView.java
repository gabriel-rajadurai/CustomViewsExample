package com.gabriel.tictaccustomview.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.gabriel.tictaccustomview.R;

/**
 * Created by Gabriel on 08-11-2017.
 */

public class TicTacView extends View {
    private Paint paint = new Paint();
    private Paint textPaint = new Paint();
    float X_PARTITION_RATIO = 1 / 3f;
    float Y_PARTITION_RATIO = 1 / 3f;


    private Rect[][] squares;
    private String[][] squareData;
    private Pair<Integer, Integer> pair = null;
    private String ticTacText = "X";
    boolean isMatchComplete = false;
    int totalMovesMade = 0;
    int totalMovesPossible;
    int cellSize = 3;
    SquarePressedListener squarePressedListener;

    public TicTacView(Context context) {
        super(context);
    }

    public TicTacView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TicTacView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TicTacView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void registerListener(SquarePressedListener squarePressedListener) {
        this.squarePressedListener = squarePressedListener;
    }

    //Override Methods
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawVerticalLines(canvas);
        drawHorizontalLines(canvas);
        drawSquareStates(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        int xVal = (int) event.getX();
        int yVal = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pair = getTouchinRectangle(xVal, yVal);
                if (pair.first >= 0 && pair.second >= 0) {
                    invalidate(squares[pair.first][pair.second]);
                }
                break;
            case MotionEvent.ACTION_UP:
                Pair<Integer, Integer> pair2 = getTouchinRectangle(xVal, yVal);
                if (pair.first >= 0 && pair.second >= 0) {
                    if ((pair.first.equals(pair2.first)) && (pair.second.equals(pair2.second))) {
                        invalidate(squares[pair.first][pair.second]);
                    }
                }
                break;
        }

        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    // Initialization Methods
    private void init() {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 5);

        textPaint = new Paint();
        textPaint.setColor(paint.getColor());
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextSize(getResources().getDisplayMetrics().density * 70);
        totalMovesPossible = cellSize * cellSize;
        initializeSquares();
    }

    private void initializeSquares() {
        //Squares
        squares = new Rect[cellSize][cellSize];
        squareData = new String[cellSize][cellSize];

        int x_unit = (int) (getWidth() * X_PARTITION_RATIO);
        int y_unit = (int) (getHeight() * X_PARTITION_RATIO);

        for (int i = 0; i < cellSize; i++) {
            for (int j = 0; j < cellSize; j++) {
                squares[i][j] = new Rect(i * x_unit, j * y_unit,
                        (i + 1) * x_unit, (j + 1) * y_unit);
            }
        }
    }


    // View_Draw Methods
    private void drawVerticalLines(Canvas canvas) {
        canvas.drawLine(getWidth() * X_PARTITION_RATIO, 0f, getWidth() * X_PARTITION_RATIO, getHeight(), paint);
        canvas.drawLine(getWidth() * (2 * X_PARTITION_RATIO), 0f, getWidth() * (2 * X_PARTITION_RATIO), getHeight(), paint);
    }

    private void drawHorizontalLines(Canvas canvas) {
        canvas.drawLine(0f, getHeight() * Y_PARTITION_RATIO, getWidth(), getHeight() * Y_PARTITION_RATIO, paint);
        canvas.drawLine(0f, getHeight() * (2 * Y_PARTITION_RATIO), getWidth(), getHeight() * (2 * Y_PARTITION_RATIO), paint);
    }

    private void drawTextInsideRect(Canvas canvas, Rect rect, String text) {
        float xOffset = textPaint.measureText(text) * 0.5f;
        float yOffset = textPaint.getFontMetrics().ascent * -0.4f;
        float textX = (rect.exactCenterX()) - xOffset;
        float textY = (rect.exactCenterY()) + yOffset;
        canvas.drawText(text, textX, textY, textPaint);
    }

    //Game AI
    private void drawSquareStates(Canvas canvas) {
        if (pair == null || pair.first < 0 || pair.second < 0)
            return;
        if (squareData[pair.first][pair.second] == null && !isMatchComplete) {
            squareData[pair.first][pair.second] = ticTacText;
            squarePressedListener.onSquarePressed();
            toggleTicTacText();
            totalMovesMade++;
        }

        int i = 0;
        for (String[] squareDatum : squareData) {
            int j = 0;
            for (String s : squareDatum) {
                if (s != null)
                    drawTextInsideRect(canvas, squares[i][j], s);
                j++;
            }
            i++;
        }
        if (totalMovesMade > 4) {
            checkForWinCondition();
        }

        if (totalMovesMade == totalMovesPossible && !isMatchComplete) {
            isMatchComplete = true;
            squarePressedListener.onMatchComplete("Draw");
        }
    }

    private void checkForWinCondition() {
        int sameCount = 0;
        int j = 0;
        for (int i = 0; i < cellSize; i++) {
            sameCount = 0;
            for (j = 0; j < cellSize; j++) {
                if (squareData[i][j] == null || squareData[i][j + 1] == null) {
                    break;
                }
                if (squareData[i][j].equals(squareData[i][j + 1])) {
                    sameCount++;
                    if (j + 2 == cellSize)
                        break;
                } else {
                    break;
                }
            }
            if (sameCount == cellSize - 1) {
                squarePressedListener.onMatchComplete(squareData[i][j] + " wins");
                isMatchComplete = true;
                break;
            }
        }

        for (int i = 0; i < cellSize; i++) {
            sameCount = 0;
            for (j = 0; j < cellSize; j++) {
                if (squareData[j][i] == null || squareData[j + 1][i] == null) {
                    break;
                }
                if (squareData[j][i].equals(squareData[j + 1][i])) {
                    sameCount++;
                    if (j + 2 == cellSize)
                        break;
                } else {
                    break;
                }
            }
            if (sameCount == cellSize - 1) {
                squarePressedListener.onMatchComplete(squareData[j][i] + " wins");
                isMatchComplete = true;
                break;
            }
        }
        sameCount = 0;
        int id = 0;
        while (id < cellSize) {
            if (id + 1 == cellSize)
                break;
            if (squareData[id][id] == null || squareData[id + 1][id + 1] == null) {
                break;
            }
            if (squareData[id][id].equals(squareData[id + 1][id + 1])) {
                sameCount++;
            } else {
                sameCount = 0;
                break;
            }
            id++;
        }
        if (sameCount == cellSize - 1) {
            squarePressedListener.onMatchComplete(squareData[id][id] + " wins");
            isMatchComplete = true;
            return;
        }

        j = 2;
        id = 0;
        sameCount = 0;
        while (id < cellSize) {
            if (id + 1 == cellSize || j - 1 < 0)
                break;
            if (squareData[id][j] == null || squareData[id + 1][j - 1] == null) {
                break;
            }
            if (squareData[id][j].equals(squareData[++id][--j])) {
                sameCount++;
            } else {
                sameCount = 0;
                break;
            }
        }

        if (sameCount == cellSize - 1) {
            squarePressedListener.onMatchComplete(squareData[id][j] + " wins");
            isMatchComplete = true;
        }


    }

    private void toggleTicTacText() {
        if (ticTacText.equals("X")) {
            ticTacText = "O";
        } else {
            ticTacText = "X";
        }
    }

    private Pair<Integer, Integer> getTouchinRectangle(int x, int y) {
        int i = 0;
        for (Rect[] square : squares) {
            int j = 0;
            for (Rect rect : square) {
                if (rect.contains(x, y)) {
                    return new Pair<>(i, j);
                }
                j++;
            }
            i++;
        }
        return new Pair<>(-1, -1);
    }

    //Public Methods
    public void resetGame() {
        squareData = new String[cellSize][cellSize];
        pair=null;
        totalMovesMade=0;
        isMatchComplete = false;
        ticTacText="X";
        invalidate();
    }

    //Match Listener
    public interface SquarePressedListener {
        void onSquarePressed();

        void onMatchComplete(String result);
    }

}
