package com.gamoige.a.gamoige.packages;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.MainActivity;

/**
 * Created by a on 7/29/17.
 */

public class IAmDrawer implements Package {
    private int power;

    public IAmDrawer(int power) {
        this.power = power;
    }

    @Override
    public void doit(ConnectionFragment fragment, String senderId) {
        if(fragment.getPower() < power) {
            ((MainActivity) fragment.getActivity()).getPlayScreen().setMode(false);
            fragment.setLeader(senderId);

        }
    }
}
