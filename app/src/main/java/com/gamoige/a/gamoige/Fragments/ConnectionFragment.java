package com.gamoige.a.gamoige.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Donsky on 7/18/2017.
 */

public class ConnectionFragment extends Fragment {
    private int onCreteId = 0;
    private int onCreateViewId = 0;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setRetainInstance(true);
        Log.d("CONNECTION_FRAGMENT", "OnCreate " + onCreteId);
        onCreteId++;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CONNECTION_FRAGMENT", "OnCreateView " + onCreateViewId);
        onCreateViewId++;
        return new View(getActivity());
    }
}
