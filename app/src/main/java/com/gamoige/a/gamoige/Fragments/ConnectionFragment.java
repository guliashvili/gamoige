package com.gamoige.a.gamoige.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.gamoige.a.gamoige.Listeners.ConnectionCallbackListener;
import com.gamoige.a.gamoige.Listeners.ConnectionFailedListener;
import com.gamoige.a.gamoige.Listeners.InvitationListn;
import com.gamoige.a.gamoige.Listeners.RealTimeMessageReceivedListeningThing;
import com.gamoige.a.gamoige.Listeners.RelMsgSentCallBack;
import com.gamoige.a.gamoige.Listeners.RoomStatusUpdateListeningThing;
import com.gamoige.a.gamoige.Listeners.RoomUpdateListeningThing;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;
import com.gamoige.a.gamoige.packages.AccMessage;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Donsky on 7/18/2017.
 */

public class ConnectionFragment extends Fragment {
    private static final String PREFS_NAME = "score";
    public ConnectionFragment connectionFragment;

    private int onCreteId = 0;
    private int onCreateViewId = 0;
    private GoogleApiClient googleApiClient;

    private ConnectionCallbackListener connectionCallbackListener = new ConnectionCallbackListener(this);
    private ConnectionFailedListener connectionFailedListener = new ConnectionFailedListener(this);
    private RoomUpdateListeningThing roomUpdateListeningThing = new RoomUpdateListeningThing(this);
    private RealTimeMessageReceivedListeningThing realTimeMessageRecivedListeningThing = new RealTimeMessageReceivedListeningThing(this);
    private RoomStatusUpdateListeningThing roomStatusUpdateListeningThing = new RoomStatusUpdateListeningThing(this);
    private RelMsgSentCallBack relMsgSentCallBack = new RelMsgSentCallBack(this);
    private InvitationListn invitationListner = new InvitationListn(this);
    public static final String QE_RO_DAGVENZREVA_IS_TEGI = "CHVEIN_TEGI";
    public static final int REQUEST_LEADERBOARD = 1;
    public static final int RC_SIGN_IN = 9001;
    public final static int RC_WAITING_ROOM = 10002;
    public final static int MIN_PLAYERS = 4;
    public final static int RC_SELECT_PLAYERS = 10000;

    private Room room;
    private boolean isPlayingValue;
    private int power;
    private String leader;

    public boolean isPlaying() {
        return isPlayingValue;
    }

    public void setPlaying(boolean playing) {
        isPlayingValue = playing;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public GoogleApiClient getConnection() {
        if(!googleApiClient.isConnected()) googleApiClient.connect();

        if(googleApiClient.isConnected()) Games.Invitations.registerInvitationListener(googleApiClient, invitationListner);

        return googleApiClient;
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setRetainInstance(true);
        Log.d("CONNECTION_FRAGMENT", "OnCreate " + onCreteId);
        onCreteId++;

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(connectionCallbackListener)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();

        googleApiClient.connect();

    }

    public int getCurrentScore(){
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);

        return settings.getInt("score", 0);

    }
    public void setCurrentScore(int score){
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);

        settings.edit().putInt("score", score).commit();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CONNECTION_FRAGMENT", "OnCreateView " + onCreateViewId);
        onCreateViewId++;
        return new View(getActivity());
    }


    public RoomConfig.Builder makeBasicRoomConfigBuilder() {
        Log.e("info","makeBasicRoomConfigBuilder");
        return RoomConfig.builder(roomUpdateListeningThing)
                .setMessageReceivedListener(realTimeMessageRecivedListeningThing)
                .setRoomStatusUpdateListener(roomStatusUpdateListeningThing);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Intent data = intent;
        int request = requestCode;
        int response = resultCode;
        if (requestCode == RC_SIGN_IN) {
            Log.e("info","onActiityResult RC_SIGN_IN");
            if (resultCode == RESULT_OK) {
                googleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(getActivity(),
                        requestCode, resultCode, R.string.signin_failure);
            }
        } else if (request == RC_SELECT_PLAYERS) {

            Log.e("info","onActiityResult RC_SELECT_PLAYERS");
            if (response != RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            Bundle extras = data.getExtras();
            final ArrayList<String> invitees =
                    data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get auto-match criteria
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        3, 5, 0);
            } else {
                autoMatchCriteria = null;
            }

            // create the room and specify a variant if appropriate
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitees);
            if (autoMatchCriteria != null) {
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            RoomConfig roomConfig = roomConfigBuilder.build();
            Games.RealTimeMultiplayer.create(getConnection(), roomConfig);

            // prevent screen from sleeping during handshake
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }else if (request == RC_WAITING_ROOM) {

            Log.e("info","onActiityResult RC_WAITING_ROOM");
            if (response == RESULT_OK) {
                // (start game)
                startGame();

            }
            else if (response == Activity.RESULT_CANCELED) {
                // Waiting room was dismissed with the back button. The meaning of this
                // action is up to the game. You may choose to leave the room and cancel the
                // match, or do something else like minimize the waiting room and
                // continue to connect in the background.

                // in this example, we take the simple approach and just leave the room:
                Games.RealTimeMultiplayer.leave(googleApiClient, roomUpdateListeningThing, room.getRoomId());
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player wants to leave the room.
                Games.RealTimeMultiplayer.leave(googleApiClient, roomUpdateListeningThing, room.getRoomId());
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    public void sendAll(Serializable a, boolean relaible){
        String myId = getRoom().getParticipantId(Games.Players.getCurrentPlayer(getConnection()).getPlayerId());

        for(Participant p : room.getParticipants()){

            if(!myId.equals(p.getParticipantId())){
                send(a,true, p.getParticipantId());
            }
        }
    }
    public void send(Serializable a, boolean relaible, String participantId){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(a);
            out.flush();
            yourBytes = bos.toByteArray();
        }catch (IOException e){
            Log.e("givorgi", "send: ioex");
            return;
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {

                // ignore close exception
            }
        }

        if(relaible)
            Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, relMsgSentCallBack, yourBytes, room.getRoomId(),participantId);
        else
            Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, yourBytes, room.getRoomId(),participantId);

    }

    public void startGame() {

        Log.e("givorgi", "My id : " + Games.Players.getCurrentPlayerId(googleApiClient));
        power = Integer.MIN_VALUE;

        ((MainActivity)getActivity()).set(MainActivity.Mode.PLAY_SCREEN);
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getLeader() {
        return leader;
    }

    public void won(String sender, String msg) {
        sendAll(new AccMessage(sender, msg), true);
    }

    public InvitationListn getInvitationListner() {
        return invitationListner;
    }

    public void leaveRoom(){
        if (room != null) {
            Games.RealTimeMultiplayer.leave(googleApiClient, roomUpdateListeningThing, room.getRoomId());
            room = null;
        }
    }
}
