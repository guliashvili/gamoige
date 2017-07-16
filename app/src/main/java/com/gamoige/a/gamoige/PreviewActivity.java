package com.gamoige.a.gamoige;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasEditorFragment;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasEditorTestActivity;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasListener;
import com.gamoige.a.gamoige.DrawableCanvas.CanvasPreview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity
        implements View.OnClickListener, CanvasListener, PreviewActivityYeldListener {
    private boolean isDrawer = false;
    private ArrayList<Action> actions = null;
    private MainActivity parent = null;
    private CanvasPreview canvasPreview = null;

    private static final String IS_DRAWER_KEY = "PreviewActivity::IS_DRAWER_KEY";
    private static final String ACTIONS_KEY = "PreviewActivity::ACTIONS_KEY";

    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(IS_DRAWER_KEY, isDrawer);
        if (actions != null) bundle.putSerializable(ACTIONS_KEY, actions);
    }

    private void setDrawer() {
        isDrawer = true;
        setContentView(R.layout.activity_drawer);
        actions = new ArrayList<>();
        ((CanvasEditorFragment) getSupportFragmentManager().
                findFragmentById(R.id.canvas_editor_fragment)).addListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean(IS_DRAWER_KEY, false)) {
            setDrawer();
            Serializable saved = savedInstanceState.getSerializable(ACTIONS_KEY);
            if(saved != null) actions = (ArrayList<Action>) saved;
        } else {
            setContentView(R.layout.canvas_preview);
            findViewById(R.id.make_me_drawer).setOnClickListener(this);
            canvasPreview = (CanvasPreview) findViewById(R.id.canvas_preview);
        }
        parent = MainActivity.getMainActivity();
        ((PreviewActivityYelder)getIntent().getExtras().get("Yelder")).register(this);
    }

    @Override
    public void onClick(View v) {
        // make me drawer
        setDrawer();
        //startActivity(new Intent(this, CanvasEditorTestActivity.class));
    }

    public List<Action> getActions(){
        return actions;
    }

    @Override
    public void actionPerformed(Action action) {
        Log.d("PREVIEW_ACTIVITY", "SOME ACTION PERFORMED");
        actions.add(action);
        parent.newLine(action);
    }

    @Override
    public void doAction(Action action) {
        canvasPreview.actionPerformed(action);
    }
}
