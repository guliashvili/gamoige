package com.gamoige.a.gamoige.Fragments;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasEditorFragment;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasPreview;
import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.R;
import com.gamoige.a.gamoige.packages.GuessMessage;
import com.gamoige.a.gamoige.packages.IAmDrawer;
import com.gamoige.a.gamoige.packages.MasterDraw;
import com.gamoige.a.gamoige.packages.YleMessage;
import com.google.android.gms.vision.text.Text;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

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
        bundle.putSerializable(QUEUE_KEY, queue);
        canvasPreview.save(bundle, PREVIEW_KEY);
    }
    private ArrayDeque<GuessMessage> queue;

    public void addElementQueue(GuessMessage guessMessage){
        queue.add(guessMessage);

        if(queue.size() == 1){
            ((TextView)drawerView.findViewById(R.id.submitted_word)).setText(guessMessage.getMsg());
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
        previewView.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionFragment.getLeader() != null) {
                    EditText editText = ((TextInputLayout) previewView.findViewById(R.id.input_layout_guessed_word)).getEditText();
                    if (editText.getText().toString().length() > 0)
                        connectionFragment.send(new GuessMessage(editText.getText().toString()), true, connectionFragment.getLeader());
                    editText.setText("");
                }
            }
        });
        if(savedInstanceState == null)
            queue = new ArrayDeque<>();
        else {
            queue = (ArrayDeque<GuessMessage>) savedInstanceState.getSerializable(QUEUE_KEY);
            if(queue == null) queue = new ArrayDeque<>();
            else{
                if(queue.size()>0)
                    ((TextView)drawerView.findViewById(R.id.submitted_word)).setText(queue.peek().getMsg());
            }
        }
        drawerView.findViewById(R.id.submitted_reject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queue.size() > 0) {
                    connectionFragment.send(new YleMessage(queue.peek().getMsg()), true, queue.peek().getSender());
                    queue.pop();
                    String s = "";

                    if (queue.size() > 0)
                        s = queue.peek().getMsg();

                    ((TextView) drawerView.findViewById(R.id.submitted_word)).setText(s);
                }
            }
        });

        drawerView.findViewById(R.id.submitted_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queue.size() > 0) {
                    GuessMessage guessMessage = queue.poll();
                    connectionFragment.won(guessMessage.getSender(), guessMessage.getMsg());
                    new LovelyInfoDialog(connectionFragment.getContext())
                            .setTopColorRes(R.color.gameResultDialogColor)
                            .setIcon(R.drawable.player_won)
                            .setTitle(R.string.game_result)
                            .setMessage(connectionFragment.getRoom().getParticipant(guessMessage.getSender())
                                    .getDisplayName() + " " + getString(R.string.someone_won))
                            .show();
                    Log.e("givorgi", "won: " + guessMessage.getSender() + " " + guessMessage.getMsg());
                    ((MainActivity) getActivity()).set(MainActivity.Mode.HOME_SCREEN);
                }
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
                if (queue != null) queue.clear();
                ((TextView) drawerView.findViewById(R.id.submitted_word)).setText("");
                if (connectionFragment != null) {
                    connectionFragment.lastId = -1;
                    connectionFragment.canvasListenerSender.reset();
                }
                if (getActivity().getCurrentFocus() != null)
                    ((InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            } else if (state == State.DRAWER) {
                drawerView.setVisibility(View.VISIBLE);
                previewView.setVisibility(View.GONE);
                drawButton.setVisibility(View.GONE);
                if (getActivity().getCurrentFocus() != null)
                    ((InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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
        canvasEditorFragment.addListener(connectionFragment.canvasListenerSender);
        if (state == State.UNDEFINED) {
            connectionFragment.lastId = -1;
            connectionFragment.canvasListenerSender.reset();
        }
    }


    public void setActiveVisual(boolean active){
        view.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    public void setActive(boolean active){
        setActiveVisual(active);
        if (!active) reset();
        else connectionFragment.resetParticipants();
    }

    public boolean isDrawer() { return (state == State.DRAWER); }
}
