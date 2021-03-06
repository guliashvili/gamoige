package com.gamoige.a.gamoige;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donsky on 7/15/2017.
 */

public class PreviewActivityYelder implements Serializable{
    private List<PreviewActivityYeldListener> listeners = new ArrayList<>();
    public void takeThings(CanvasListener.Action... actions){
        for(PreviewActivityYeldListener listener : listeners)
            for(CanvasListener.Action action : actions)
                listener.doAction(action);
    }

    public void register(PreviewActivityYeldListener listener){
        listeners.add(listener);
        if(listeners.size() > 1){
            throw new RuntimeException("More then one listener?");
        }
    }
    public PreviewActivity getPreviewActivity(){
        if(listeners.size() != 1){
            throw new RuntimeException("No litener? " + listeners.size());
        }
        return (PreviewActivity)listeners.get(0);
    }
}
