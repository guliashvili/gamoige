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

    private class ScoreIncrementer implements ResultCallback<Leaderboards.LoadPlayerScoreResult> {
        ConnectionFragment connectionFragment;

        public ScoreIncrementer(ConnectionFragment fragment) {
            connectionFragment = fragment;
        }

        @Override
        public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
            Log.d("Donsky", "CHANGING SCORE...");
            long score = 100;
            if (loadPlayerScoreResult.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK)
                Log.e("Donsky", "loadPlayerScoreResult.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK");
            if (loadPlayerScoreResult != null) {
                if (loadPlayerScoreResult.getScore() != null) {
                    score += loadPlayerScoreResult.getScore().getRawScore();
                } else Log.e("Donsky", "loadPlayerScoreResult.getScore() == null");
            } else Log.e("Donsky", "loadPlayerScoreResult == null");
            Games.Leaderboards.submitScoreImmediate(connectionFragment.getConnection(),
                    connectionFragment.getContext().getString(R.string.LEADERBOARD_ID), score)
                    .setResultCallback(new ResultCallback<Leaderboards.SubmitScoreResult>() {
                        @Override
                        public void onResult(@NonNull Leaderboards.SubmitScoreResult submitScoreResult) {
                            if (submitScoreResult.getStatus().getStatusCode() == 0) {
                                Log.d("Donsky", "SCORE SHOULD BE UPDATED");
                            } else Log.e("Donsky", "SCORE FAILED TO BE UPDATED");
                        }
                    });
            Log.d("Donsky", "NEW SCORE: " + score);
        }
    }

    @Override
    public void doit(final ConnectionFragment fragment, String senderId) {
        String myId = fragment.getRoom().getParticipantId(Games.Players.getCurrentPlayer(fragment.getConnection()).getPlayerId());
        boolean win;
        if(myId.equals(winner)){
            //*
            Log.d("Donsky", "REQUESTING...");
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(
                    fragment.getConnection(),
                    fragment.getContext().getString(R.string.LEADERBOARD_ID),
                    LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(
                            new ScoreIncrementer(fragment));
            /*/
            Games.Leaderboards.submitScore(fragment.getConnection(),
                    fragment.getContext().getString(R.string.LEADERBOARD_ID), 100);
            //*/
            Log.e("givorgi", "yeeeeeei");
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
