package com.gamoige.a.gamoige.packages;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;

/**
 * Created by a on 7/29/17.
 */

public class GuessMessage implements Package {
    private String msg;

    public GuessMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public void doit(ConnectionFragment fragment, String senderId) {

    }
}
