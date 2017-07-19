package com.gamoige.a.gamoige.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamoige.a.gamoige.R;

/**
 * Created by Donsky on 7/17/2017.
 */

public class PlayScreen extends Fragment {
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.canvas_editor_fragment, container, false);
        view.setVisibility(View.GONE);
        return view;
    }

}
