package com.gamoige.a.gamoige.packages;

import android.util.Log;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;

/**
 * Created by a on 7/31/17.
 */

public class YleMessage implements Package{
    private String msg;

    public YleMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public void doit(ConnectionFragment fragment, String senderId) {
        Log.e("givorgi", "gaajvi message");
    }
}
