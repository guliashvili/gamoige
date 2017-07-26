package com.gamoige.a.gamoige.packages;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;

/**
 * Created by a on 7/16/17.
 */

public class MasterDraw implements Package{
    private CanvasListener.Action action;

    public MasterDraw(CanvasListener.Action action) {
        this.action = action;
    }

    @Override
    public void doit(ConnectionFragment fragment) {

    }
}
