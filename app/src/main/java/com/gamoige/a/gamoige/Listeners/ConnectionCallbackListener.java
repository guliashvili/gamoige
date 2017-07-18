package com.gamoige.a.gamoige.Listeners;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;

/**
 * Created by Donsky on 7/18/2017.
 */

public class ConnectionCallbackListener implements GoogleApiClient.ConnectionCallbacks {
    private ConnectionFragment connectionFragment;

    public ConnectionCallbackListener(ConnectionFragment fragment) {
        connectionFragment = fragment;
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        // TODO
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
        if (connectionHint != null) {
            Invitation inv = connectionHint.getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null) {
                // accept invitation
                RoomConfig.Builder roomConfigBuilder = connectionFragment.makeBasicRoomConfigBuilder();
                roomConfigBuilder.setInvitationIdToAccept(inv.getInvitationId());
                Games.RealTimeMultiplayer.join(connectionFragment.getConnection(), roomConfigBuilder.build());
                // prevent screen from sleeping during handshake
                connectionFragment.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                // go to game screen
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        connectionFragment.getConnection().connect();
    }
}
