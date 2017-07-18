package com.gamoige.a.gamoige.Listeners;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

/**
 * Created by Donsky on 7/18/2017.
 */

public class RealTimeMessageReceivedListeningThing implements RealTimeMessageReceivedListener {
    private ConnectionFragment connectionFragment;
    public RealTimeMessageReceivedListeningThing(ConnectionFragment fragment) {
        connectionFragment = fragment;
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {

    }
}
