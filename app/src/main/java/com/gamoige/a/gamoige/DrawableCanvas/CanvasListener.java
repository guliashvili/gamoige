package com.gamoige.a.gamoige.DrawableCanvas;

import android.graphics.PointF;

import java.io.Serializable;

/**
 * Created by Donsky on 7/12/2017.
 */

public interface CanvasListener  {
    class Action implements Serializable{
        public enum Type implements Serializable {
            DRAW, UNDO, REDO
        }
        public static class PointData implements Serializable {
            public float x, y;
            public float width, height;
            public int color;
            public float size;
            public boolean pathEnd;
            public PointData(float posX, float posY, float w, float h, int col, float lineSize, boolean end) {
                x = posX;
                y = posY;
                width = w;
                height = h;
                color = col;
                size = lineSize;
                pathEnd = end;
            }
        }
        private Type type;
        private PointData pointData;
        public Action(Type action) {
            if (action == Type.DRAW)
                throw new NullPointerException("ACTION \"DRAW\" HAS TO CONTAIN DATA");
            type = action;
            pointData = null;
        }
        public Action(PointF point, float width, float height, int color, float size, boolean pathEnd) {
            type = Type.DRAW;
            pointData = new PointData(point.x, point.y, width, height, color, size, pathEnd);
        }
        public Type getType() {
            return type;
        }
        public PointF point() {
            return new PointF(pointData.x, pointData.y);
        }
        public int color() {
            return pointData.color;
        }
        public float size() {
            return pointData.size;
        }
        public boolean pathEnd() {
            return pointData.pathEnd;
        }
        public float width() {
            return pointData.width;
        }
        public float height() {
            return pointData.height;
        }
    }
    void actionPerformed(Action action);
}
