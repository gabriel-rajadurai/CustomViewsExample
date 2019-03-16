package com.gabriel.tictaccustomview.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gabriel.tictaccustomview.R;

import java.util.ArrayList;

/**
 * Created by Gabriel on 10-11-2017.
 */

public class PaintView extends View {

    private Paint strokePaint, pointerPaint;
    private boolean paintStarted;
    private ArrayList<Coordinates> coordinates;
    private Path path = new Path();
    private BrushPointer pointer;

    private ArrayList<Paths> paths = new ArrayList<>();
    private ArrayList<Paths> allPaths = new ArrayList<>();
    private ArrayList<Paths> erasePaths = new ArrayList<>();
    private ArrayList<Paths> allErasePaths = new ArrayList<>();
    private int drawIndex = 0;
    private boolean isLastOpErase = false;

    //Attributes
    private int strokeColor = R.color.colorPrimary;
    private float radiusX, radiusY;
    private int strokeSize;

    // Paint Type
    private String PAINT_TYPE = "0";
    public static String PAINT_TYPE_PENCIL = "0";
    public static String PAINT_TYPE_CIRCLE = "1";
    public static String PAINT_TYPE_RECTANGLE = "2";
    public static String PAINT_TYPE_OVAL = "3";
    public static String PAINT_TYPE_ROUNDED_RECTANGLE = "4";
    public static String PAINT_TYPE_ERASE = "5";

    //Constructors
    public PaintView(Context context) {
        super(context);
        init(null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    //Initialize
    @SuppressLint("ResourceAsColor")
    private void init(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.PaintLayout,
                    0, 0);

            try {
                strokeColor = a.getColor(R.styleable.PaintLayout_strokeColor, R.color.colorPrimary);
                radiusX = a.getFloat(R.styleable.PaintLayout_radiusX, 20f);
                radiusY = a.getFloat(R.styleable.PaintLayout_radiusY, 20f);
                strokeSize = a.getInt(R.styleable.PaintLayout_strokeSize, 5);
            } finally {
                a.recycle();
            }
        }

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeWidth(strokeSize);

        pointerPaint = new Paint();
        pointerPaint.setAntiAlias(true);
        pointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pointerPaint.setColor(strokeColor);
        pointerPaint.setAlpha(50);
        pointerPaint.setStrokeWidth(5);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        coordinates = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        pointer = new BrushPointer(w / 2, h / 2);
    }

    /*Override Methods****/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPointer(canvas);
        startPaint(canvas);
        if (PAINT_TYPE.equals(PAINT_TYPE_ERASE)) {
            erase(canvas);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            drawIndex++;
            coordinates.clear();
            paths.clear();
            erasePaths.clear();
            pointer = new BrushPointer(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            paintStarted = true;
            pointer = new BrushPointer(event.getX(), event.getY());
            coordinates.add(new Coordinates(event.getX(), event.getY(), drawIndex));
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!paths.isEmpty()) {
                Paths p = paths.get(0);
                if (!allPaths.contains(p))
                    allPaths.add(p);
            }
            if (!erasePaths.isEmpty()) {
                Paths e = erasePaths.get(0);
                if (!allErasePaths.contains(e))
                    allErasePaths.add(e);
            }
        }

        if (PAINT_TYPE.equals(PAINT_TYPE_PENCIL))
            getPathsFromCoordinates();
        else if (PAINT_TYPE.equals(PAINT_TYPE_CIRCLE))
            getCircleFromCoordinates();
        else if (PAINT_TYPE.equals(PAINT_TYPE_RECTANGLE))
            getRectFromCoordinates();
        else if (PAINT_TYPE.equals(PAINT_TYPE_OVAL))
            getOvalFromCoordinates();
        else if (PAINT_TYPE.equals(PAINT_TYPE_ROUNDED_RECTANGLE))
            getRoundedRectFromCoordinates();
        else if (PAINT_TYPE.equals(PAINT_TYPE_ERASE))
            getErasePaths();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    /* Canvas Methods *****/
    @SuppressLint("ResourceAsColor")
    private void startPaint(Canvas canvas) {
        if (paintStarted) {
            strokePaint.setColor(strokeColor);
            strokePaint.setXfermode(null);
            if (!paths.containsAll(allPaths))
                paths.addAll(allPaths);
            for (Paths path1 : paths) {
                canvas.drawPath(path1.path, strokePaint);
            }
            paintStarted = false;
            isLastOpErase = false;
        }
    }

    private void drawPointer(Canvas canvas) {
        if (pointer == null) pointer = new BrushPointer(getWidth() / 2, getHeight() / 2);
        canvas.drawCircle(pointer.x, pointer.y, strokeSize * 3, pointerPaint);
    }

    private void erase(Canvas canvas) {
        strokePaint.setColor(Color.parseColor("#f4f4f4"));
        strokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        if (!erasePaths.containsAll(allErasePaths))
            erasePaths.addAll(allErasePaths);
        for (Paths erasePath : erasePaths) {
            canvas.drawPath(erasePath.path, strokePaint);
        }
        isLastOpErase = true;
    }

    private void getErasePaths() {
        int currIndex = 0;
        Path prevPath = null;
        erasePaths = new ArrayList<>();

        for (Coordinates coordinate : coordinates) {
            float pointX = coordinate.x;
            float pointY = coordinate.y;
            if (currIndex != coordinate.drawIndex) {
                if (prevPath != null)
                    erasePaths.add(new Paths(prevPath, PAINT_TYPE));
                path = new Path();
                currIndex = coordinate.drawIndex;
                path.moveTo(pointX, pointY);
            } else {
                path.lineTo(pointX, pointY);
                prevPath = path;
            }

        }
        if (prevPath != null)
            erasePaths.add(new Paths(prevPath, PAINT_TYPE));
    }

    // Get paths(lines)
    private void getPathsFromCoordinates() {
        int currIndex = 0;
        Path prevPath = null;
        paths = new ArrayList<>();
        for (Coordinates coordinate : coordinates) {

            float pointX = coordinate.x;
            float pointY = coordinate.y;
            if (currIndex != coordinate.drawIndex) {
                if (prevPath != null)
                    paths.add(new Paths(prevPath, PAINT_TYPE));
                path = new Path();
                currIndex = coordinate.drawIndex;
                path.moveTo(pointX, pointY);
            } else {
                path.lineTo(pointX, pointY);
                prevPath = path;
            }

        }
        if (prevPath != null)
            paths.add(new Paths(prevPath, PAINT_TYPE));
    }

    //Get paths(circles)
    private void getCircleFromCoordinates() {
        int currIndex = 0;
        Path prevPath = null;
        float startX = 0;
        float startY = 0;
        paths = new ArrayList<>();
        for (Coordinates coordinate : coordinates) {

            float pointX = coordinate.x;
            float pointY = coordinate.y;
            if (currIndex != coordinate.drawIndex) {
                if (prevPath != null)
                    paths.add(new Paths(prevPath, PAINT_TYPE));
                startX = pointX;
                startY = pointY;
                path = new Path();
                currIndex = coordinate.drawIndex;
                path.moveTo(pointX, pointY);
            } else {
                float radius = getDia(startX, startY, pointX, pointY) / 2;
                path.reset();
                path.addCircle((startX + pointX) / 2, (startY + pointY) / 2, radius, Path.Direction.CW);
                prevPath = path;
            }

        }

        if (prevPath != null) {
            paths.add(new Paths(prevPath, PAINT_TYPE));
        }
    }

    //Get paths(rectangles)
    private void getRectFromCoordinates() {
        int currIndex = 0;
        Path prevPath = null;
        float startX = 0;
        float startY = 0;

        paths = new ArrayList<>();
        for (Coordinates coordinate : coordinates) {
            float pointX = coordinate.x;
            float pointY = coordinate.y;
            if (currIndex != coordinate.drawIndex) {
                if (prevPath != null) {
                    paths.add(new Paths(prevPath, PAINT_TYPE));
                }
                startX = pointX;
                startY = pointY;
                path = new Path();
                currIndex = coordinate.drawIndex;
                path.moveTo(pointX, pointY);
            } else {
                path.reset();
                Rect rect = getRect(startX, startY, pointX, pointY);
                RectF rectF = new RectF(rect);
                path.addRect(rectF, Path.Direction.CW);
                prevPath = path;
            }

        }

        if (prevPath != null) {
            paths.add(new Paths(prevPath, PAINT_TYPE));
        }
    }

    private void getOvalFromCoordinates() {
        int currIndex = 0;
        Path prevPath = null;
        float startX = 0;
        float startY = 0;

        paths = new ArrayList<>();
        for (Coordinates coordinate : coordinates) {
            float pointX = coordinate.x;
            float pointY = coordinate.y;
            if (currIndex != coordinate.drawIndex) {
                if (prevPath != null) {
                    paths.add(new Paths(prevPath, PAINT_TYPE));
                }
                startX = pointX;
                startY = pointY;
                path = new Path();
                currIndex = coordinate.drawIndex;
                path.moveTo(pointX, pointY);
            } else {
                path.reset();
                Rect rect = getRect(startX, startY, pointX, pointY);
                RectF rectF = new RectF(rect);
                path.addOval(rectF, Path.Direction.CW);
                prevPath = path;
            }
        }

        if (prevPath != null) {
            paths.add(new Paths(prevPath, PAINT_TYPE));
        }
    }

    private void getRoundedRectFromCoordinates() {
        int currIndex = 0;
        Path prevPath = null;
        float startX = 0;
        float startY = 0;

        paths = new ArrayList<>();
        for (Coordinates coordinate : coordinates) {
            float pointX = coordinate.x;
            float pointY = coordinate.y;
            if (currIndex != coordinate.drawIndex) {
                if (prevPath != null) {
                    paths.add(new Paths(prevPath, PAINT_TYPE));
                }
                startX = pointX;
                startY = pointY;
                path = new Path();
                currIndex = coordinate.drawIndex;
                path.moveTo(pointX, pointY);
            } else {
                path.reset();
                Rect rect = getRect(startX, startY, pointX, pointY);
                RectF rectF = new RectF(rect);
                path.addRoundRect(rectF, radiusX, radiusY, Path.Direction.CW);
                prevPath = path;
            }
        }

        if (prevPath != null) {
            paths.add(new Paths(prevPath, PAINT_TYPE));
        }
    }

    //Get Rect from points
    private Rect getRect(float startX, float startY, float endX, float endY) {
        int sX = Math.round(startX);
        int sY = Math.round(startY);
        int eX = Math.round(endX);
        int eY = Math.round(endY);
        if (sX > eX && sY > eY) {
            return new Rect(eX, eY, sX, sY);
        } else if (sX > eX) {
            return new Rect(eX, sY, sX, eY);
        } else if (sY > eY) {
            return new Rect(sX, eY, eX, sY);
        }
        return new Rect(sX, sY, eX, eY);
    }

    //Get Diameter from points
    private float getDia(float sX, float sY, float eX, float eY) {
        float x = (float) Math.pow(sX - eX, 2);
        float y = (float) Math.pow(sY - eY, 2);
        return (float) Math.sqrt(x + y);
    }


    /* Public Methods *****/

    /**
     * Method to set Paint Type (Lines, Circles or Rectangles)
     *
     * @param type is the type of shape to be drawn
     */
    public void setType(String type) {
        PAINT_TYPE = type;
    }

    public void setRadius(float radiusX, float radiusY) {
        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void undoOperation() {
        if (!isLastOpErase) {
            if (allPaths.size() >= 1) {
                paths.clear();
                coordinates.clear();
                allPaths.remove(allPaths.size() - 1);
                invalidate();
                paintStarted = true;
            }
        } else {
            if (allErasePaths.size() >= 1) {
                erasePaths.clear();
                coordinates.clear();
                allErasePaths.remove(allErasePaths.size() - 1);
                invalidate();
                paintStarted = true;
                setType(PAINT_TYPE_ERASE);
            } else {
                isLastOpErase = false;
            }
        }
    }

    /**
     * Method to clear and reset the canvas.
     */
    public void resetPaintSpace() {
        coordinates.clear();
        allPaths.clear();
        allErasePaths.clear();
        invalidate();
    }

    /**
     * Class that holds the points of a user's touch
     */
    class Coordinates {
        float x;
        float y;
        int drawIndex;

        Coordinates(float x, float y, int drawIndex) {
            this.x = x;
            this.y = y;
            this.drawIndex = drawIndex;
        }
    }

    /**
     * Class that holds all the paths drawn on screen
     */
    class Paths {
        Path path;
        String PAINT_TYPE;

        Paths(Path path, String PAINT_TYPE) {
            this.path = path;
            this.PAINT_TYPE = PAINT_TYPE;
        }
    }

    class BrushPointer {
        float x;
        float y;

        public BrushPointer(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
