package com.gamoige.a.gamoige.packages;

import android.util.Log;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.Fragments.PlayScreen;

/**
 * Created by Donsky on 8/3/2017.
 */

public class CanvasListenerSender implements CanvasListener {
    private int id = 0;
    private ConnectionFragment connectionFragment;

    public CanvasListenerSender(ConnectionFragment connectionFragment) {
        this.connectionFragment = connectionFragment;
    }

    public void reset(){
        id = 0;
    }

    @Override
    public void actionPerformed(Action action) {
        connectionFragment.sendAll(new MasterDraw(action, id++), true);

    }
}
