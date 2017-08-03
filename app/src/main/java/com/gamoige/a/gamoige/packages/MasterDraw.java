package com.gamoige.a.gamoige.packages;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;

/**
 * Created by a on 7/16/17.
 */

public class MasterDraw implements Package{
    private CanvasListener.Action action;
    private int id;

    public MasterDraw(CanvasListener.Action action, int id) {
        this.action = action;
        this.id = id;
    }

    @Override
    public void doit(ConnectionFragment fragment, String senderId) {
        if(id <= fragment.lastId){
            fragment.packages.clear();
            fragment.lastId = id - 1;
        }


        fragment.packages.put(id, this);

        while(fragment.packages.containsKey(fragment.lastId + 1)){
            fragment.lastId++;
            ((MainActivity) fragment.getActivity()).getPlayScreen().actionPerformed(fragment.packages.get(fragment.lastId).action);
            fragment.packages.remove(fragment.lastId);
        }
    }
}
