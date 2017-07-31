package com.gamoige.a.gamoige.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasEditorFragment;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasPreview;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;
import com.gamoige.a.gamoige.packages.GuessMessage;
import com.gamoige.a.gamoige.packages.IAmDrawer;
import com.gamoige.a.gamoige.packages.MasterDraw;
import com.google.android.gms.vision.text.Text;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Donsky on 7/17/2017.
 */

public class PlayScreen extends Fragment implements CanvasListener{
    private enum State implements Serializable {
        UNDEFINED,
        DRAWER,
        PREVIEW,
        NONE_STATE
    }
    private State state = State.NONE_STATE;
    private View view, drawerView, previewView, drawButton;
    private ConnectionFragment connectionFragment;
    private CanvasEditorFragment canvasEditorFragment;
    private CanvasPreview canvasPreview;

    private static final String STATE_KEY = "PLAY_SCREEN_STATE_KEY";
    private static final String PREVIEW_KEY = "PLAY_SCREEN_PREVIEW_KEY";
    private static final String QUEUE_KEY = "QUEUE_KEY";

    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(STATE_KEY, state);
        bundle.putSerializable(PREVIEW_KEY, queue);
        canvasPreview.save(bundle, PREVIEW_KEY);
    }
    private ArrayDeque<GuessMessage> queue;

    public void addElementQueue(GuessMessage guessMessage){
        queue.add(guessMessage);

        if(queue.size() == 1){
            ((TextView)previewView.findViewById(R.id.submitted_word)).setText(guessMessage.getMsg());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.play_screen_fragment, container, false);
        view.setVisibility(View.GONE);
        drawerView = view.findViewById(R.id.play_screen_fragment_drawer);
        previewView = view.findViewById(R.id.play_screen_fragment_preview);
        drawButton = previewView.findViewById(R.id.make_me_drawer);
        canvasEditorFragment = (CanvasEditorFragment) getChildFragmentManager().findFragmentById(R.id.canvas_editor_fragment);
        canvasPreview = (CanvasPreview) previewView.findViewById(R.id.canvas_preview);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(true);
                connectionFragment.setPower(new Random().nextInt());
                connectionFragment.sendAll(new IAmDrawer(connectionFragment.getPower()), true);
            }
        });
        canvasEditorFragment.addListener(new CanvasListener() {
            @Override
            public void actionPerformed(Action action) {
                if (state == State.DRAWER) {
                    connectionFragment.sendAll(new MasterDraw(action), true);
                } else {
                    Log.e("Donsky", "UNEXPECTED ACTION...");
                }
            }
        });
        previewView.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionFragment.send(new GuessMessage(
                        ((TextView)previewView.findViewById(R.id.input_layout_guessed_word)).getText().toString()),
                        true, connectionFragment.getLeader());

            }
        });
        if(savedInstanceState == null)
            queue = new ArrayDeque<>();
        else {
            queue = (ArrayDeque<GuessMessage>) savedInstanceState.getSerializable(QUEUE_KEY);
            if(queue == null) queue = new ArrayDeque<>();
        }
        drawerView.findViewById(R.id.submitted_reject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue.peek();
                if(queue.size() > 0){
                    ((TextView)previewView.findViewById(R.id.submitted_word)).setText(queue.poll().getMsg());
                }
            }
        });

        drawerView.findViewById(R.id.submitted_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionFragment.won(queue.poll().getSender(),queue.poll().getMsg());
                Log.e("givorgi", "won: " + queue.poll().getSender() + " " + queue.poll().getMsg());
                
            }
        });

        State savedState;
        if (savedInstanceState != null)
            savedState = (State) savedInstanceState.getSerializable(STATE_KEY);
        else savedState = State.UNDEFINED;
        if (savedState == State.PREVIEW) canvasPreview.restore(savedInstanceState, PREVIEW_KEY);
        setState(savedState);
        return view;
    }

    @Override
    public void actionPerformed(Action action) {
        canvasPreview.actionPerformed(action);
    }

    private void setState(State state) {
        if (this.state != state) {
            if (state == State.UNDEFINED) {
                drawerView.setVisibility(View.GONE);
                previewView.setVisibility(View.VISIBLE);
                drawButton.setVisibility(View.VISIBLE);
                canvasPreview.reset();
                canvasEditorFragment.clear();
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


    public void setActiveVisual(boolean active){
        view.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    public void setActive(boolean active){
        setActiveVisual(active);
    }
}
