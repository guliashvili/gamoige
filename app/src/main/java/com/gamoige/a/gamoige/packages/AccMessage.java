package com.gamoige.a.gamoige.packages;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

/**
 * Created by a on 7/29/17.
 */

public class AccMessage implements Package{
    private String winner;
    private String msg;
    public AccMessage(String winner, String msg) {
        this.winner = winner;
        this.msg = msg;
    }



    @Override
    public void doit(ConnectionFragment fragment, String senderId) {
        ConnectionFragment connectionFragment = fragment;
        
        String myId = fragment.getRoom().getParticipantId(Games.Players.getCurrentPlayer(fragment.getConnection()).getPlayerId());
        boolean win;
        if(myId.equals(winner)){
            int score = connectionFragment.getCurrentScore() + 100;
            connectionFragment.setCurrentScore(score);

            Games.Leaderboards.submitScore(fragment.getConnection(),
                    fragment.getContext().getString(R.string.LEADERBOARD_ID), score);

            win = true;
        } else win = false;
        // now just for test. this code below will be placed in game over part.
        // win will be replaced with game result for the player
        new LovelyInfoDialog(fragment.getContext())
                .setTopColorRes(R.color.gameResultDialogColor)
                .setIcon(win ? R.drawable.player_won : R.drawable.player_lost)
                .setTitle(R.string.game_result)
                .setMessage(win ? R.string.you_win : R.string.you_lost)
                .show();
        ((MainActivity) fragment.getActivity()).set(MainActivity.Mode.HOME_SCREEN);
    }

}
