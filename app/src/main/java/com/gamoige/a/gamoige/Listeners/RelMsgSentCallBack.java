package com.gamoige.a.gamoige.Listeners;

import android.util.Log;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;

/**
 * Created by a on 7/26/17.
 */

public class RelMsgSentCallBack implements RealTimeMultiplayer.ReliableMessageSentCallback {
    private ConnectionFragment connectionFragment;

    public RelMsgSentCallBack(ConnectionFragment connectionFragment) {
        this.connectionFragment = connectionFragment;
    }

    @Override
    public void onRealTimeMessageSent(int i, int i1, String s) {
        Log.e("givorgi onRealTimeMessageSent", i + " " + i1 + " " + s);
    }
}
