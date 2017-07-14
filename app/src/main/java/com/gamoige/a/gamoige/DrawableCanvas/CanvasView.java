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
    private float virtualWidth = -1.0f;
    private float virtualHeight = -1.0f;

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

    private boolean fixVirtualDimensions(float width, float height) {
        if (width <= 0.0f || height <= 0.0f) return false;
        else if (virtualWidth < 0.0f || virtualHeight < 0.0f) {
            virtualWidth = Math.min(width, height);
            virtualHeight = virtualWidth;
            setActionResolution(virtualWidth, virtualHeight);
        }
        else if (virtualWidth != width && virtualHeight != height) {
            float aspect = (width / height);
            float virtualAspect = (virtualWidth / virtualHeight);
            if (aspect > virtualAspect) {
                virtualHeight = height;
                virtualWidth = (virtualHeight * virtualAspect);
            } else {
                virtualWidth = height;
                virtualHeight = (virtualWidth / virtualAspect);
            }
            setActionResolution(virtualWidth, virtualHeight);
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        fixVirtualDimensions(width, height);
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }

    private void send(PointF point, boolean endPath) {
        if (!fixVirtualDimensions(getWidth(), getHeight())) return;
        float deltaX = Math.abs((point.x * 2.0f) - getWidth());
        if (deltaX <= getWidth() && deltaX > virtualWidth) virtualWidth = deltaX;
        float deltaY = Math.abs((point.y * 2.0f) - getHeight());
        if (deltaY <= getHeight() && deltaY > virtualHeight) virtualHeight = deltaY;
        send(new Action(new PointF(
                (point.x - (getWidth() / 2.0f)) + (virtualWidth / 2.0f),
                (point.y - (getHeight() / 2.0f)) + (virtualHeight / 2.0f)),
                virtualWidth, virtualHeight,
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
    private static final String VIRTUAL_WIDTH_KEY = "::VIRTUAL_WIDTH_KEY";
    private static final String VIRTUAL_HEIGHT_KEY = "::VIRTUAL_HEIGHT_KEY";

    @Override
    public void save(Bundle bundle, String canvasName) {
        super.save(bundle, canvasName);
        if (bundle == null) return;
        bundle.putBoolean(canvasName + ENTRY_KEY + DRAWING_KEY, drawing);
        bundle.putInt(canvasName + ENTRY_KEY + COLOR_KEY, color);
        bundle.putFloat(canvasName + ENTRY_KEY + SIZE_KEY, size);
        bundle.putFloat(canvasName + ENTRY_KEY + VIRTUAL_WIDTH_KEY, virtualWidth);
        bundle.putFloat(canvasName + ENTRY_KEY + VIRTUAL_HEIGHT_KEY, virtualHeight);
    }
    @Override
    public void restore(Bundle bundle, String canvasName) {
        super.restore(bundle, canvasName);
        if (bundle == null) return;
        drawing = bundle.getBoolean(canvasName + ENTRY_KEY + DRAWING_KEY, drawing);
        color = bundle.getInt(canvasName + ENTRY_KEY + COLOR_KEY, color);
        size = bundle.getFloat(canvasName + ENTRY_KEY + SIZE_KEY, size);
        virtualWidth = bundle.getFloat(canvasName + ENTRY_KEY + VIRTUAL_WIDTH_KEY, virtualWidth);
        virtualWidth = bundle.getFloat(canvasName + ENTRY_KEY + VIRTUAL_HEIGHT_KEY, virtualHeight);
        fixVirtualDimensions(getWidth(), getHeight());
    }
}
