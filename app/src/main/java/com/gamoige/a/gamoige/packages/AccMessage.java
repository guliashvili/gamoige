package com.gamoige.a.gamoige.packages;

import android.util.Log;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;
import com.google.android.gms.games.Games;

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
        if(Games.Players.getCurrentPlayerId(fragment.getConnection()).equals(winner)){
            Games.Leaderboards.submitScore(fragment.getConnection(), " " + R.string.LEADERBOARD_ID, 4000);
            Log.e("givorgi", "yeeeeeei");
        }
    }

}
