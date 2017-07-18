package com.gamoige.a.gamoige.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamoige.a.gamoige.R;

/**
 * Created by Donsky on 7/17/2017.
 */

public class HomeScreen extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.canvas_editor_fragment, container, false);
        return view;
    }
}
