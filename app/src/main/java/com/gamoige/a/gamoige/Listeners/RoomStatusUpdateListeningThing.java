package com.gamoige.a.gamoige.Listeners;

import android.util.Log;
import android.view.WindowManager;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donsky on 7/18/2017.
 */

public class RoomStatusUpdateListeningThing implements RoomStatusUpdateListener {
    private ConnectionFragment connectionFragment;
    public RoomStatusUpdateListeningThing(ConnectionFragment fragment) {
        connectionFragment = fragment;
    }

    @Override
    public void onRoomConnecting(Room room) {
        Log.e("info","onRoomConnecting " + room.getRoomId());
        connectionFragment.setRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        Log.e("info","onRoomAutoMatching " + room.getRoomId());
        connectionFragment.setRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {
        Log.e("info","onPeerInvitedToRoom " + room.getRoomId() + " " + list.toString());
        connectionFragment.setRoom(room);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> peers) {
        Log.e("info","onPeerDeclined " + room.getRoomId() + " " + peers.toString());
        connectionFragment.setRoom(room);
        // peer declined invitation -- see if game should be canceled
        if (!connectionFragment.isPlaying() && shouldCancelGame(room)) {
            Games.RealTimeMultiplayer.leave(connectionFragment.getConnection(), null, connectionFragment.getRoom().getRoomId());
            connectionFragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {
        Log.e("info","onPeerJoined " + room.getRoomId() + " " + list.toString());
        connectionFragment.setRoom(room);
    }

    private void quitIfRoomNotValid(Room room, List<String> peersThatLeft) {
        if (peersThatLeft == null) peersThatLeft = new ArrayList<>();
        connectionFragment.participantsLeft(peersThatLeft.size());
        if (connectionFragment.activeParticipants(room) <= 1) {
            new LovelyInfoDialog(connectionFragment.getContext())
                    .setTopColorRes(R.color.gameResultDialogColor)
                    .setIcon(R.drawable.error)
                    .setTitle(R.string.unable_to_continue)
                    .setMessage(R.string.not_enough_players_left)
                    .show();

            ((MainActivity) connectionFragment.getActivity()).set(MainActivity.Mode.HOME_SCREEN);
        }
        else for(String id : peersThatLeft){
            if(id.equals(connectionFragment.getLeader())){
                new LovelyInfoDialog(connectionFragment.getContext())
                        .setTopColorRes(R.color.gameResultDialogColor)
                        .setIcon(R.drawable.error)
                        .setTitle(R.string.unable_to_continue)
                        .setMessage(R.string.drawer_left_game_message)
                        .show();

                ((MainActivity) connectionFragment.getActivity()).set(MainActivity.Mode.HOME_SCREEN);
            }
        }
    }

    @Override
    public void onPeerLeft(Room room, List<String> peers) {
        Log.e("info","onPeerLeft " + room.getRoomId() + " " + peers.toString());
        connectionFragment.setRoom(room);

        // peer left -- see if game should be canceled
        if (!connectionFragment.isPlaying() && shouldCancelGame(room)) {
            quitIfRoomNotValid(room, peers);

            Games.RealTimeMultiplayer.leave(connectionFragment.getConnection(), null, connectionFragment.getRoom().getRoomId());
            connectionFragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onConnectedToRoom(Room room) {
        Log.e("info","onConnectedToRoom " + room.getRoomId());
        connectionFragment.setRoom(room);
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        Log.e("info","onDisconnectedFromRoom " + room.getRoomId());
        connectionFragment.setRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        Log.e("info","onPeersConnected " + room.getRoomId() + " " + peers.toString());
        connectionFragment.setRoom(room);
        if (connectionFragment.isPlaying()) {
            // add new player to an ongoing game
        } else if (shouldStartGame(room)) {
            // start game!
            //connectionFragment.startGame();


        }
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        Log.e("info","onPeersDisconnected " + room.getRoomId() + " " + peers.toString());
        connectionFragment.setRoom(room);
        quitIfRoomNotValid(room, peers);

        if (connectionFragment.isPlaying()) {
            // do game-specific handling of this -- remove player's avatar
            // from the screen, etc. If not enough players are left for
            // the game to go on, end the game and leave the room.
        } else if (shouldCancelGame(room)) {
            // cancel the game
            Games.RealTimeMultiplayer.leave(connectionFragment.getConnection(), null, room.getRoomId());
            connectionFragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onP2PConnected(String s) {
        Log.e("info","onP2PConnected " + s);

    }

    @Override
    public void onP2PDisconnected(String s) {
        Log.e("info","onP2PDisconnected " + s);

    }

    private  boolean shouldCancelGame(Room room) {
        Log.e("info","shouldCancelGame " + room.getRoomId());
        connectionFragment.setRoom(room);
        if(room.getParticipantIds().size() <= 1) {
            new LovelyInfoDialog(connectionFragment.getContext())
                    .setTopColorRes(R.color.gameResultDialogColor)
                    .setIcon(R.drawable.error)
                    .setTitle(R.string.unable_to_continue)
                    .setMessage(R.string.not_enough_players_left)
                    .show();

            ((MainActivity) connectionFragment.getActivity()).set(MainActivity.Mode.HOME_SCREEN);

            return true;
        }

        // TODO: Your game-specific cancellation logic here. For example, you might decide to
        // cancel the game if enough people have declined the invitation or left the room.
        // You can check a participant's status with Participant.getStatus().
        // (Also, your UI should have a Cancel button that cancels the game too)
        return false;
    }


    // returns whether there are enough players to start the game
    boolean shouldStartGame(Room room) {
        Log.e("info","shouldStartGame " + room.getRoomId());

        int connectedPlayers = 0;
        for (Participant p : room.getParticipants()) {
            if (p.isConnectedToRoom()) ++connectedPlayers;
        }
        return connectedPlayers >= connectionFragment.MIN_PLAYERS;
    }
}
