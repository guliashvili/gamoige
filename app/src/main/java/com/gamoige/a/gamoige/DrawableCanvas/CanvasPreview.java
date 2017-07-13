package com.gamoige.a.gamoige.DrawableCanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Donsky on 7/12/2017.
 */

public class CanvasPreview extends View implements CanvasListener {
    private class PaintedPath {
        public Path path = new Path();
        public Paint paint;
        public PaintedPath(Paint base) {
            paint = base;
        }
        public PaintedPath(PaintedPath paintedPath) {
            path = new Path(paintedPath.path);
            paint = new Paint(paintedPath.paint);
        }
    }
    private ArrayList<Action> actions = new ArrayList<>();
    private List<PaintedPath> paths = new ArrayList<>();
    private Stack<PaintedPath> redoStack = new Stack<>();
    private PaintedPath current = null;
    private PointF lastPoint = null;
    private Paint paint;
    private Bitmap bitmap = null;
    private int drawn = 0;
    private PointF lastMasterSize = null;

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
    }

    public CanvasPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void clear() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawn = 0;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        paths.clear();
        redoStack.clear();
        current = null;
        lastPoint = null;
        initPaint();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawn = 0;
        for (Action action : actions) applyAction(action);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            Canvas bitmapCanvas = new Canvas(bitmap);
            while (drawn < paths.size()) {
                PaintedPath path = paths.get(drawn);
                bitmapCanvas.drawPath(path.path, path.paint);
                drawn++;
            }
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        if (current != null) canvas.drawPath(current.path, current.paint);
    }

    private float translate(float w0, float h0, float w1, float h1, PointF p) {
        float scaleFactor;
        float srcAspect = (w0 / h0);
        float aspect = (w1 / h1);
        if (aspect > srcAspect) scaleFactor = (h1 / h0);
        else scaleFactor = (w1 / w0);
        p.x = ((p.x - w0 / 2.0f) * scaleFactor + w1 / 2.0f);
        p.y = ((p.y - h0 / 2.0f) * scaleFactor + h1 / 2.0f);
        return scaleFactor;
    }

    private void applyAction(Action action) {
        if (action.getType() == Action.Type.DRAW) {
            PointF point = action.point();
            float scaleFactor = translate(
                    action.width(), action.height(),
                    getWidth(), getHeight(),
                    point);
            paint.setColor(action.color());
            paint.setStrokeWidth(action.size() * scaleFactor);
            if (action.pathEnd()) {
                if (current != null) {
                    current.path.lineTo(point.x, point.y);
                    paths.add(new PaintedPath(current));
                    redoStack.clear();
                }
                current = null;
                lastPoint = null;
            } else if (current == null) {
                paint.setColor(action.color());
                current = new PaintedPath(paint);
                current.path.moveTo(point.x, point.y);
                lastPoint = point;
            } else {
                current.path.quadTo(lastPoint.x, lastPoint.y,
                        (lastPoint.x + point.x)/2.0f, (lastPoint.y + point.y)/2.0f);
                lastPoint = point;
            }
        } else {
            if (current != null) {
                paths.add(current);
                redoStack.clear();
            }
            current = null;
            lastPoint = null;
            if (action.getType() == Action.Type.UNDO) {
                if (paths.size() > 0) {
                    redoStack.push(paths.get(paths.size() - 1));
                    paths.remove(paths.size() - 1);
                    clear();
                }
            } else if (action.getType() == Action.Type.REDO) {
                if (!redoStack.empty()) paths.add(redoStack.pop());
            }
        }
    }

    private void addAction(Action action) {
        if (action.getType() == Action.Type.DRAW) {
            PointF size = new PointF(action.width(), action.height());
            if (lastMasterSize != null)
                if (lastMasterSize.x != size.x || lastMasterSize.y != size.y) {
                    for (int i = 0; i < actions.size(); i++)
                        if (actions.get(i).getType() == Action.Type.DRAW) {
                            Action current = actions.get(i);
                            PointF point = current.point();
                            float scaleFactor = translate(
                                    current.width(), current.height(),
                                    size.x, size.y,
                                    point);
                            actions.set(i, new Action(point, size.x, size.y, current.color(),
                                    current.size() * scaleFactor, current.pathEnd()));
                        }
                    clear();
                }
            lastMasterSize = size;
        }
        actions.add(action);
    }

    @Override
    public void actionPerformed(Action action) {
        addAction(action);
        applyAction(action);
        invalidate();
    }

    private static final String ACTIONS_KEY = "::ACTIONS_KEY";
    private static String key(String canvasName) {
        return (canvasName + ACTIONS_KEY);
    }
    public void save(Bundle bundle, String canvasName) {
        if (bundle == null) return;
        bundle.putSerializable(key(canvasName), actions);
    }
    public void restore(Bundle bundle, String canvasName) {
        if (bundle == null) return;
        ArrayList<Action> saved = (ArrayList<Action>) bundle.getSerializable(key(canvasName));
        if (saved != null) for(Action action : saved) addAction(action);
    }
}
