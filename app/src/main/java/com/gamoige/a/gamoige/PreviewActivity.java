package com.gamoige.a.gamoige;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gamoige.a.gamoige.DrawableCanvas.CanvasEditorTestActivity;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isDrawer = false;

    private static final String IS_DRAWER_KEY = "IS_DRAWER_KEY";

    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(IS_DRAWER_KEY, isDrawer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean(IS_DRAWER_KEY, false)) {
            isDrawer = true;
            setContentView(R.layout.activity_drawer);
        } else {
            setContentView(R.layout.canvas_preview);
            findViewById(R.id.make_me_drawer).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        // make me drawer
        isDrawer = true;
        setContentView(R.layout.activity_drawer);
        //startActivity(new Intent(this, CanvasEditorTestActivity.class));
    }
}
