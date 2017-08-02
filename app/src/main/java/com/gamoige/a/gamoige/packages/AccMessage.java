package com.gamoige.a.gamoige.packages;

import android.util.Log;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;
import com.google.android.gms.games.Games;
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
        String myId = fragment.getRoom().getParticipantId(Games.Players.getCurrentPlayer(fragment.getConnection()).getPlayerId());
        boolean win;
        if(myId.equals(winner)){
            Games.Leaderboards.submitScore(fragment.getConnection(), " " + R.string.LEADERBOARD_ID, 100);
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
