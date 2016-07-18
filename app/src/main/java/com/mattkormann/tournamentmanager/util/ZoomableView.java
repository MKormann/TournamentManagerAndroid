package com.mattkormann.tournamentmanager.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Matt on 7/13/2016.
 */
public class ZoomableView extends FrameLayout {

    private static float MIN_ZOOM = 1.f;
    private static float MAX_ZOOM = 5.f;

    private float factor = 1.f;
    private ScaleGestureDetector detector;

    public ZoomableView(Context context) {
        this(context, null);
    }

    public ZoomableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(factor, factor);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            factor *= detector.getScaleFactor();
            factor = Math.max(MIN_ZOOM, Math.min(factor, MAX_ZOOM));
            invalidate();
            return true;
        }
    }
}
