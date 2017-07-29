package com.gamoige.a.gamoige.Listeners;

import android.util.Log;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.packages.Package;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

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
        Log.e("info","onRealTimeMessageReceived " + realTimeMessage.getSenderParticipantId() + " " + realTimeMessage.getMessageData().toString());
        ByteArrayInputStream in = new ByteArrayInputStream(realTimeMessage.getMessageData());
        try{

            ObjectInputStream is = new ObjectInputStream(in);
            Package p = (Package)is.readObject();
            p.doit(connectionFragment,  realTimeMessage.getSenderParticipantId());
        }catch (Exception e){
            Log.e("info", e.getMessage().toString());
            return;
        }
    }
}
