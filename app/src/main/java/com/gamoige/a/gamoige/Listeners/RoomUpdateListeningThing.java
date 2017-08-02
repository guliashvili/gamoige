package com.gamoige.a.gamoige.Listeners;

import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;


/**
 * Created by Donsky on 7/18/2017.
 */

public class RoomUpdateListeningThing implements RoomUpdateListener {
    private ConnectionFragment connectionFragment;
    public RoomUpdateListeningThing(ConnectionFragment fragment) {
        connectionFragment = fragment;
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        connectionFragment.setRoom(room);
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            ((MainActivity) connectionFragment.getActivity()).getHomeScreen().hide();
            // let screen go to sleep
            connectionFragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            new LovelyInfoDialog(connectionFragment.getContext())
                    .setTopColorRes(R.color.gameResultDialogColor)
                    .setIcon(R.drawable.error)
                    .setTitle(R.string.problem)
                    .setMessage(R.string.no_internet)
                    .show();
            // show error message, return to main screen.
            //TODO
            return;
        }
        // get waiting room intent
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(connectionFragment.getConnection(), room, 2);
        connectionFragment.startActivityForResult(i, ConnectionFragment.RC_WAITING_ROOM);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.e("info","onJoinedRoom " + statusCode + " " + room.getRoomId());
        connectionFragment.setRoom(room);
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            connectionFragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // show error message, return to main screen.
            //TODO
            return;
        }
        // get waiting room intent
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(connectionFragment.getConnection(), room, 2);
        connectionFragment.startActivityForResult(i, ConnectionFragment.RC_WAITING_ROOM);
    }

    @Override
    public void onLeftRoom(int i, String s) {
        Log.e("info","onLeftRoom " + i + " " + s);
        connectionFragment.setRoom(null);
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.e("info","onRoomConnected " + statusCode + " " + room.getRoomId());
        connectionFragment.setRoom(room);
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            connectionFragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // show error message, return to main screen.
        }
    }
}
