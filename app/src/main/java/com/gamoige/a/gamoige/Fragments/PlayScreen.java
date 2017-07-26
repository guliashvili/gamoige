package com.gamoige.a.gamoige.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasEditorFragment;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;

/**
 * Created by Donsky on 7/17/2017.
 */

public class PlayScreen extends Fragment {
    private enum State {
        UNDEFINED,
        DRAWER,
        PREVIEW
    }

    private State state = State.UNDEFINED;
    private View view, drawerView, previewView, drawButton;
    private ConnectionFragment connectionFragment;
    private CanvasEditorFragment canvasEditorFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.play_screen_fragment, container, false);
        view.setVisibility(View.GONE);
        drawerView = view.findViewById(R.id.play_screen_fragment_drawer);
        previewView = view.findViewById(R.id.play_screen_fragment_preview);
        drawButton = previewView.findViewById(R.id.make_me_drawer);
        canvasEditorFragment = (CanvasEditorFragment) getFragmentManager().findFragmentById(R.id.canvas_editor_fragment);
        reset();
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(true);
            }
        });
        canvasEditorFragment.addListener(new CanvasListener() {
            @Override
            public void actionPerformed(Action action) {
                if (state == State.DRAWER) {
                    Log.e("Donsky", "COULD SEND, BUT...");
                } else {
                    Log.e("Donsky", "UNEXPECTED ACTION...");
                }
            }
        });
        return view;
    }

    private void setState(State state) {
        if (state == State.UNDEFINED) {
            drawerView.setVisibility(View.GONE);
            previewView.setVisibility(View.VISIBLE);
            drawButton.setVisibility(View.VISIBLE);
        } else if (state == State.DRAWER) {
            drawerView.setVisibility(View.VISIBLE);
            previewView.setVisibility(View.GONE);
            drawButton.setVisibility(View.GONE);
        } else if (state == State.PREVIEW) {
            drawerView.setVisibility(View.GONE);
            previewView.setVisibility(View.VISIBLE);
            drawButton.setVisibility(View.GONE);
        }
        this.state = state;
    }

    public void reset() {
        setState(State.UNDEFINED);
    }

    public void setMode(boolean drawer) {
        setState(drawer ? State.DRAWER : State.PREVIEW);
    }

    public void setConnectionFragment(ConnectionFragment fragment) {
        connectionFragment = fragment;
    }


    public void setActive(boolean active){
        view.setVisibility(active ? View.VISIBLE : View.GONE);
    }
}
