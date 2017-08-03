package com.gamoige.a.gamoige;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasEditorTestActivity;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.Fragments.HomeScreen;
import com.gamoige.a.gamoige.Fragments.PlayScreen;
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

public class MainActivity  extends AppCompatActivity {
    public enum Mode implements Serializable{
        HOME_SCREEN,
        PLAY_SCREEN
    }

    private static final String MAIN_ACTIVITY_SAVED_MODE = "MAIN_ACTIVITY_SAVED_MODE";

    private ConnectionFragment connectionFragment;
    private HomeScreen homeScreen;
    private PlayScreen playScreen;
    private Mode activeMode;


    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle == null) return;
        bundle.putSerializable(MAIN_ACTIVITY_SAVED_MODE, activeMode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        connectionFragment = (ConnectionFragment) fragmentManager.findFragmentById(R.id.connection_fragment);
        homeScreen = (HomeScreen) fragmentManager.findFragmentById(R.id.home_screen_fragment);
        playScreen = (PlayScreen) fragmentManager.findFragmentById(R.id.play_screen_fragment);
        homeScreen.setConnectionFragment(connectionFragment);
        playScreen.setConnectionFragment(connectionFragment);
        if (savedInstanceState != null)
            activeMode = (Mode) savedInstanceState.getSerializable(MAIN_ACTIVITY_SAVED_MODE);
        else activeMode = Mode.HOME_SCREEN;
        setVisual(activeMode);
    }


    private void setVisual(Mode mode) {
        homeScreen.setActiveVisual(mode == Mode.HOME_SCREEN);
        playScreen.setActiveVisual(mode == Mode.PLAY_SCREEN);
    }

    public void set(Mode mode) {
        setVisual(mode);
        homeScreen.setActive(mode == Mode.HOME_SCREEN);
        playScreen.setActive(mode == Mode.PLAY_SCREEN);
        activeMode = mode;
    }

    public PlayScreen getPlayScreen() { return playScreen; }

    public HomeScreen getHomeScreen() {
        return homeScreen;
    }

    @Override
    public void onBackPressed() {
        if (activeMode == Mode.PLAY_SCREEN) {
            QuitGameDialog dialog = new QuitGameDialog().setParent(this);
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        } else super.onBackPressed();
    }

    public static class QuitGameDialog extends DialogFragment {
        private MainActivity parent;

        public QuitGameDialog setParent(MainActivity parent) {
            this.parent = parent;
            return this;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.quit_game_check_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            parent.set(Mode.HOME_SCREEN);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });

            return builder.create();
        }
    }
}
