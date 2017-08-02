package com.gamoige.a.gamoige.Listeners;

import android.util.Log;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by a on 8/2/17.
 */

public class InvitationListn implements OnInvitationReceivedListener {
    private ConnectionFragment connectionFragment;
    private Map<String, Invitation> invitations = new HashMap<>();

    public InvitationListn(ConnectionFragment connectionFragment) {
        this.connectionFragment = connectionFragment;
    }


    @Override
    public void onInvitationReceived(Invitation invitation) {
        Log.e("givorgi", "invitation");
        invitations.put(invitation.getInvitationId(), invitation);
    }

    @Override
    public void onInvitationRemoved(String s) {
        invitations.remove(s);
    }
}
