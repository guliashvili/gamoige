package com.gamoige.a.gamoige.packages;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;

/**
 * Created by a on 7/29/17.
 */

public class AccMessage implements Package{
    private String winner;

    public AccMessage(String winner) {
        this.winner = winner;
    }

    @Override
    public void doit(ConnectionFragment fragment, String senderId) {
        
    }
}
