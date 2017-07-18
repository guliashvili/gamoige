package com.gamoige.a.gamoige;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasEditorTestActivity;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.packages.Package;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends AppCompatActivity implements RealTimeMultiplayer.ReliableMessageSentCallback, RealTimeMessageReceivedListener {
    private static MainActivity mainActivity;
    public static MainActivity getMainActivity(){return mainActivity;}
    private static final int REQUEST_LEADERBOARD = 1;
    public static GoogleApiClient mGoogleApiClient;

    // request code for the "select players" UI
    // can be any number as long as it's unique
    final static int RC_SELECT_PLAYERS = 10000;
    // at least 2 players required for our game
    final static int MIN_PLAYERS = 2;
    // arbitrary request code for the waiting room UI.
    // This can be any integer that's unique in your Activity.
    final static int RC_WAITING_ROOM = 10002;


    // are we already playing?
    private String mRoomId;
    private Room ROOM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, TmpMainActivity.class));
    }




    /****************************************/
    //matchmaking

    /*******************************************/


    public void startLeaderBoard(View view) {
        Log.e("info","startLeaderBoard");
        //TODO remove
        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.LEADERBOARD_ID), 1337);


        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                getString(R.string.LEADERBOARD_ID)), REQUEST_LEADERBOARD);
    }


    public void startQuickGame(View view) {
        Log.e("info","startQuickGame");
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 3, 0);

        // build the room config:
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room:
        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // go to game screen
        //TODO
    }

    public void startSelectFriends(View view) {
        Log.e("info","startSelectFriends");
        // launch the player selection screen
        // minimum: 1 other player; maximum: 3 other players
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }


    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        Log.e("info","onRealTimeMessageReceived " + realTimeMessage.getSenderParticipantId() + " " + realTimeMessage.getMessageData().toString());
        ByteArrayInputStream in = new ByteArrayInputStream(realTimeMessage.getMessageData());
        try{

            ObjectInputStream is = new ObjectInputStream(in);
            Package p = (Package)is.readObject();
            p.doit(this);
        }catch (Exception e){
            Log.e("info", e.getMessage().toString());
            return;
        }
    }

    private void send(Serializable a, boolean relaible, String participantId){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(a);
            out.flush();
            yourBytes = bos.toByteArray();
        }catch (IOException e){

            return;
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        if(relaible)
            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, yourBytes, ROOM.getRoomId(),participantId);
        else
            Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, yourBytes, ROOM.getRoomId(),participantId);

    }
    public void newLine(CanvasListener.Action action){
        for(Participant p : ROOM.getParticipants()){
            if(!Games.Players.getCurrentPlayerId(mGoogleApiClient).equals(p.getParticipantId())){
                send(action,true, p.getParticipantId());
            }
        }
    }
    public void recieveLine(CanvasListener.Action action){
        Log.e("info", "recieveLine");
        previewActivityYelder.getPreviewActivity().doAction(action);
    }

    @Override
    public void onRealTimeMessageSent(int i, int i1, String s) {

    }
}
