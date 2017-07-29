package com.gamoige.a.gamoige.Fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.gamoige.a.gamoige.PreviewActivity;
import com.gamoige.a.gamoige.R;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by Donsky on 7/17/2017.
 */

public class HomeScreen extends Fragment {
    private ConnectionFragment connectionFragment;
    private AVLoadingIndicatorView avi;
    private View view;
    private boolean loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, container, false);
        avi = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        loading = false;
        view.findViewById(R.id.leaderBoardBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avi.show();
                startLeaderBoard(v);
                loading = true;
            }
        });
        view.findViewById(R.id.quickGameBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avi.show();
                startQuickGame(v);
                loading = true;
            }
        });
        view.findViewById(R.id.selectFriendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avi.show();
                startSelectFriends(v);
                loading = true;
            }
        });
        return view;
    }

    public void setConnectionFragment(ConnectionFragment fragment){
        connectionFragment = fragment;
    }


    public void startLeaderBoard(View view) {
        Log.e("info","startLeaderBoard");

        if (!loading) {
            //TODO remove
            Games.Leaderboards.submitScore(connectionFragment.getConnection(), getString(R.string.LEADERBOARD_ID), 1337);


            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(connectionFragment.getConnection(),
                    getString(R.string.LEADERBOARD_ID)), ConnectionFragment.REQUEST_LEADERBOARD);
        }
    }

    public void startQuickGame(View view) {
        Log.e("info","startQuickGame");

        if (!loading) {
            // auto-match criteria to invite one random automatch opponent.
            // You can also specify more opponents (up to 3).
            Bundle am = RoomConfig.createAutoMatchCriteria(1, 3, 0);

            // build the room config:
            RoomConfig.Builder roomConfigBuilder = connectionFragment.makeBasicRoomConfigBuilder();
            roomConfigBuilder.setAutoMatchCriteria(am);
            RoomConfig roomConfig = roomConfigBuilder.build();

            // create room:
            Games.RealTimeMultiplayer.create(connectionFragment.getConnection(), roomConfig);

            // prevent screen from sleeping during handshake
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // go to game screen
            //TODO
        }
    }

    public void startSelectFriends(View view) {
        Log.e("info","startSelectFriends");
        if (!loading) {
            // launch the player selection screen
            // minimum: 1 other player; maximum: 3 other players
            Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(connectionFragment.getConnection(), 1, 3);
            startActivityForResult(intent, ConnectionFragment.RC_SELECT_PLAYERS);
        }
    }

    public void setActiveVisual(boolean active){
        view.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    public void setActive(boolean active){
        setActiveVisual(active);
    }

    @Override
    public void onResume() {
        avi.hide();
        loading = false;
        super.onResume();
    }
}
