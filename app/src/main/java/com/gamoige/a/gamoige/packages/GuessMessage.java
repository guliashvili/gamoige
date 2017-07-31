package com.gamoige.a.gamoige.packages;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;

/**
 * Created by a on 7/29/17.
 */

public class GuessMessage implements Package {
    private String msg;
    private String sender;
    public GuessMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public void doit(ConnectionFragment fragment, String senderId) {
        this.sender = senderId;
        ((MainActivity)fragment.getActivity()).getPlayScreen().addElementQueue(this);
    }

    public String getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }
}
