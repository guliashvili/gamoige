package com.gamoige.a.gamoige.DrawableCanvas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.gamoige.a.gamoige.R;

/**
 * Created by Donsky on 7/13/2017.
 */

public class CanvasEditorTestActivity extends AppCompatActivity {
    private CanvasPreview preview0, preview1;

    private static final String PREVIEW_0_KEY = "PREVIEW_0_KEY";
    private static final String PREVIEW_1_KEY = "PREVIEW_1_KEY";

    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        preview0.save(bundle, PREVIEW_0_KEY);
        preview1.save(bundle, PREVIEW_1_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_editor_test_activity);
        Log.d("MAIN_ACTIVITY", "onCreate");
        preview0 = (CanvasPreview)findViewById(R.id.canvas_editor_test_preview_0);
        preview1 = (CanvasPreview)findViewById(R.id.canvas_editor_test_preview_1);
        preview0.restore(savedInstanceState, PREVIEW_0_KEY);
        preview1.restore(savedInstanceState, PREVIEW_1_KEY);
        ((CanvasEditorFragment) getSupportFragmentManager().findFragmentById(R.id.canvas_editor_test_canvas_fragment))
                .addListener(preview0).addListener(preview1);
    }
}

