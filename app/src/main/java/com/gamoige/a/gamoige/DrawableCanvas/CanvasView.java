package com.gamoige.a.gamoige.DrawableCanvas;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Donsky on 7/12/2017.
 */

public class CanvasView extends CanvasPreview {
    private Set<CanvasListener> listeners = new HashSet<>();
    private boolean drawing = false;
    private int color = Color.BLACK;
    private float size = 0.0078125f;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addListener(this);
    }

    public CanvasView addListener(CanvasListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
        return this;
    }

    private void send(Action action) {
        for (CanvasListener listener : listeners)
            listener.actionPerformed(action);
    }

    private void send(PointF point, boolean endPath) {
        send(new Action(point, getWidth(), getHeight(),
                color, size * Math.min(getWidth(), getHeight()), endPath));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        boolean shouldSend = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (drawing) send(point, true);
                drawing = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!drawing) shouldSend = false;
                break;
            case MotionEvent.ACTION_UP:
                if (!drawing) shouldSend = false;
                drawing = false;
                break;
            default:
                break;
        }
        if (shouldSend) send(point, !drawing);
        return true;
    }

    public void setColor(int newColor) {
        color = newColor;
    }
    public void setSize(float newSize) {
        size = newSize;
    }
    public void undo() {
        send(new Action(Action.Type.UNDO));
    }
    public void redo() {
        send(new Action(Action.Type.REDO));
    }

    private static final String ENTRY_KEY = "::CANVAS_VIEW";
    private static final String DRAWING_KEY = "::DRAWING_KEY";
    private static final String COLOR_KEY = "::COLOR_KEY";
    private static final String SIZE_KEY = "::SIZE_KEY";

    @Override
    public void save(Bundle bundle, String canvasName) {
        super.save(bundle, canvasName);
        if (bundle == null) return;
        bundle.putBoolean(canvasName + ENTRY_KEY + DRAWING_KEY, drawing);
        bundle.putInt(canvasName + ENTRY_KEY + COLOR_KEY, color);
        bundle.putFloat(canvasName + ENTRY_KEY + SIZE_KEY, size);
    }
    @Override
    public void restore(Bundle bundle, String canvasName) {
        super.restore(bundle, canvasName);
        if (bundle == null) return;
        drawing = bundle.getBoolean(canvasName + ENTRY_KEY + DRAWING_KEY, drawing);
        color = bundle.getInt(canvasName + ENTRY_KEY + COLOR_KEY, color);
        size = bundle.getFloat(canvasName + ENTRY_KEY + SIZE_KEY, size);
    }
}
