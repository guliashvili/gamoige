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
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener {
    private static final int REQUEST_LEADERBOARD = 1;
    public static GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    // request code for the "select players" UI
    // can be any number as long as it's unique
    final static int RC_SELECT_PLAYERS = 10000;
    // at least 2 players required for our game
    final static int MIN_PLAYERS = 2;
    // arbitrary request code for the waiting room UI.
    // This can be any integer that's unique in your Activity.
    final static int RC_WAITING_ROOM = 10002;


    // are we already playing?
    boolean mPlaying = false;
    private String mRoomId;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, R.string.signin_other_error)) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //TODO
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.


        if (connectionHint != null) {
            Invitation inv =
                    connectionHint.getParcelable(Multiplayer.EXTRA_INVITATION);

            if (inv != null) {
                // accept invitation
                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
                roomConfigBuilder.setInvitationIdToAccept(inv.getInvitationId());
                Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());

                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // go to game screen
            }
        }


    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Intent data = intent;
        int request = requestCode;
        int response = resultCode;
        if (requestCode == RC_SIGN_IN) {
            Log.e("info","onActiityResult RC_SIGN_IN");
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        } else if (request == RC_SELECT_PLAYERS) {

            Log.e("info","onActiityResult RC_SELECT_PLAYERS");
            if (response != Activity.RESULT_OK) {
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
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
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
            Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

            // prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }else if (request == RC_WAITING_ROOM) {

            Log.e("info","onActiityResult RC_WAITING_ROOM");
            if (response == Activity.RESULT_OK) {
                // (start game)
            }
            else if (response == Activity.RESULT_CANCELED) {
                // Waiting room was dismissed with the back button. The meaning of this
                // action is up to the game. You may choose to leave the room and cancel the
                // match, or do something else like minimize the waiting room and
                // continue to connect in the background.

                // in this example, we take the simple approach and just leave the room:
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player wants to leave the room.
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    // Call when the sign-in button is clicked
    private void signInClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    // Call when the sign-out button is clicked
    private void signOutclicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
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

    @Override
    public void onRoomConnecting(Room room) {
        Log.e("info","onRoomConnecting " + room.getRoomId());
        mRoomId = room.getRoomId();

    }

    @Override
    public void onRoomAutoMatching(Room room) {
        Log.e("info","onRoomAutoMatching " + room.getRoomId());
        mRoomId = room.getRoomId();


    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {
        Log.e("info","onPeerInvitedToRoom " + room.getRoomId() + " " + list.toString());
        mRoomId = room.getRoomId();


    }

    @Override
    public void onPeerDeclined(Room room, List<String> peers) {
        Log.e("info","onPeerDeclined " + room.getRoomId() + " " + peers.toString());
        mRoomId = room.getRoomId();

        // peer declined invitation -- see if game should be canceled
        if (!mPlaying && shouldCancelGame(room)) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {
        Log.e("info","onPeerJoined " + room.getRoomId() + " " + list.toString());
        mRoomId = room.getRoomId();


    }

    @Override
    public void onPeerLeft(Room room, List<String> peers) {
        Log.e("info","onPeerLeft " + room.getRoomId() + " " + peers.toString());
        mRoomId = room.getRoomId();

        // peer left -- see if game should be canceled
        if (!mPlaying && shouldCancelGame(room)) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onConnectedToRoom(Room room) {
        Log.e("info","onConnectedToRoom " + room.getRoomId());
        mRoomId = room.getRoomId();


    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        Log.e("info","onDisconnectedFromRoom " + room.getRoomId());
        mRoomId = room.getRoomId();


    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        Log.e("info","onPeersConnected " + room.getRoomId() + " " + peers.toString());
        mRoomId = room.getRoomId();

        if (mPlaying) {
            // add new player to an ongoing game
        } else if (shouldStartGame(room)) {
            // start game!
        }
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        Log.e("info","onPeersDisconnected " + room.getRoomId() + " " + peers.toString());
        mRoomId = room.getRoomId();

        if (mPlaying) {
            // do game-specific handling of this -- remove player's avatar
            // from the screen, etc. If not enough players are left for
            // the game to go on, end the game and leave the room.
        } else if (shouldCancelGame(room)) {
            // cancel the game
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, room.getRoomId());
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        Log.e("info","onRealTimeMessageReceived " + realTimeMessage.getSenderParticipantId() + " " + realTimeMessage.getMessageData().toString());

    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.e("info","onRoomCreated " + statusCode + " " + room.getRoomId());
        mRoomId = room.getRoomId();

        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.
            //TODO
            return;
        }

        // get waiting room intent
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, 2);
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.e("info","onJoinedRoom " + statusCode + " " + room.getRoomId());
        mRoomId = room.getRoomId();

        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.
            //TODO
            return;
        }


        // get waiting room intent
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, 2);
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    @Override
    public void onLeftRoom(int i, String s) {
        Log.e("info","onLeftRoom " + i + " " + s);

    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.e("info","onRoomConnected " + statusCode + " " + room.getRoomId());
        mRoomId = room.getRoomId();

        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.
        }
    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        Log.e("info","makeBasicRoomConfigBuilder");
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
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

    // returns whether there are enough players to start the game
    boolean shouldStartGame(Room room) {
        Log.e("info","shouldStartGame " + room.getRoomId());
        mRoomId = room.getRoomId();

        int connectedPlayers = 0;
        for (Participant p : room.getParticipants()) {
            if (p.isConnectedToRoom()) ++connectedPlayers;
        }
        return connectedPlayers >= MIN_PLAYERS;
    }

    // Returns whether the room is in a state where the game should be canceled.
    boolean shouldCancelGame(Room room) {
        Log.e("info","shouldCancelGame " + room.getRoomId());
        mRoomId = room.getRoomId();

        // TODO: Your game-specific cancellation logic here. For example, you might decide to
        // cancel the game if enough people have declined the invitation or left the room.
        // You can check a participant's status with Participant.getStatus().
        // (Also, your UI should have a Cancel button that cancels the game too)
        return false;
    }


    public void startdonsk(View view) {
        startActivity(new Intent(this, CanvasEditorTestActivity.class));
    }
}
