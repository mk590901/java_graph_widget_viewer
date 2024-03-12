package com.example.graphwidgetsviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class GraphWidget extends View {

    private boolean startStop = false;
    private int currentCounter = 0;
    private
        StoreWrapper storeWrapper;
    private final Obtain obtain;

    private final Paint paintLine;
    private final Paint paintLineAfter;
    private final Paint paintRectPrev;
    private final Paint paintCircle;

    private final int canvasColor;
    private final int markerRadius = 12;

    private final Path path;
    private Bitmap bitmap;
    private Canvas canvas;

    private int shiftH;
    private Size size;

    public GraphWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        canvasColor = ContextCompat.getColor(context, /*R.color.dodger_blue*/R.color.blue_2);
        int canvasPrevColor = ContextCompat.getColor(context, /*R.color.columbia_blue*/R.color.blue_3);

        obtain = new Obtain(this, 24);

        paintLine = new Paint();
        paintLine.setColor(Color.WHITE);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(2);

        paintLineAfter = new Paint();
        paintLineAfter.setColor(Color.DKGRAY);
        paintLineAfter.setStyle(Paint.Style.STROKE);
        paintLineAfter.setStrokeWidth(2);

        paintRectPrev = new Paint();
        paintRectPrev.setColor(canvasPrevColor);
        paintRectPrev.setStyle(Paint.Style.FILL);

        paintCircle = new Paint();
        paintCircle.setColor(Color.RED); // Set color as per your requirement
        paintCircle.setStyle(Paint.Style.FILL);

        path = new Path();
    }

    public void setMode(final int seriesLength, final StoreWrapper.GraphMode mode) {
        storeWrapper = new StoreWrapper(seriesLength, 5, mode);
    }

    public void setMode(final StoreWrapper.GraphMode mode) {
        storeWrapper.setMode( mode);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        size = new Size(w, h);
        shiftH  = h/6;

    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(canvasColor);

        if (currentCounter > 0) {
            storeWrapper.prepareDrawing(size, shiftH);
            drawProcedure(size, canvas);
        }

        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawPath(path, paintLine);

    }

    void drawProcedure(Size size, Canvas canvas) {
        if (storeWrapper.mode() == StoreWrapper.GraphMode.overlay) {
            drawOverlayGraph(canvas, size);
        }
        else {
            drawFlowingGraph(canvas);
        }
    }

    void drawFlowingGraph(Canvas canvas) {
        canvas.drawPath(storeWrapper.pathBefore, paintLine);
        canvas.drawPath(storeWrapper.pathAfter, paintLine);
        if (!storeWrapper.isFull()) {
            canvas.drawCircle(storeWrapper.point.x, storeWrapper.point.y, markerRadius, paintCircle);
        }
    }

    void drawOverlayGraph(Canvas canvas, Size size) {
        canvas.drawRect(storeWrapper.point.x, 0, size.getWidth(), size.getHeight(), paintRectPrev);
        canvas.drawPath(storeWrapper.pathBefore, paintLine);
        canvas.drawPath(storeWrapper.pathAfter, paintLineAfter);
        canvas.drawCircle(storeWrapper.point.x, storeWrapper.point.y, markerRadius, paintCircle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle tap event
        if (event.getAction() == MotionEvent.ACTION_UP) {
            startStop = !startStop;
            if (startStop) {
                obtain.start();
            } else {
                obtain.stop();
            }
        }
        return true;
    }

    public void stop() {
        if (startStop) {
            startStop = false;
            obtain.stop();
        }
    }

    public void start() {
        if (!startStop) {
            startStop = true;
            obtain.start();
        }
    }

    public void clearCanvas() {
        canvas.drawColor(Color.WHITE); // Clear canvas by filling it with white color
        invalidate(); // Redraw the view
    }

    public void update(final int counter) {
        //System.out.println("------- update [" + counter + "] -------");
        currentCounter = counter;
        storeWrapper.updateBuffer(currentCounter);
        obtain.setState(storeWrapper.drawingFrequency());

        postInvalidate(); // Redraw the view
    }
}
