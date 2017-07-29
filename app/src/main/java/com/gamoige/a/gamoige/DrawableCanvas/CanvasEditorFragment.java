package com.gamoige.a.gamoige.DrawableCanvas;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.gamoige.a.gamoige.R;

/**
 * Created by Donsky on 7/13/2017.
 */

public class CanvasEditorFragment extends Fragment {
    private CanvasView canvasView;
    private View undo, redo;
    private SeekBar sizeSlider;
    private View eraser, pencil;
    private boolean drawMode;
    private int activeColor;
    private float currentSize;
    private AlertDialog colorPicker = null;
    private ViewGroup colorPickersParent;

    private static final int BACKGROUND_COLOR = Color.WHITE;
    private static final int MAX_PROGRESS = 1000;
    private static final float MAX_SIZE = 0.025f;
    private static final float ERASER_SIZE_SCALE = 4.0f;
    private static final String CANVAS_KEY = "CANVAS_KEY";
    private static final String DRAW_MODE_KEY = "DRAW_MODE_KEY";
    private static final String ACTIVE_COLOR_KEY = "ACTIVE_COLOR_KEY";
    private static final String CURRENT_SIZE_KEY = "CURRENT_SIZE_KEY";

    private void showBrush() {
        eraser.setVisibility(drawMode ? View.VISIBLE : View.GONE);
        pencil.setVisibility(drawMode ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        canvasView.save(bundle, CANVAS_KEY);
        bundle.putBoolean(DRAW_MODE_KEY, drawMode);
        bundle.putInt(ACTIVE_COLOR_KEY, activeColor);
        bundle.putFloat(CURRENT_SIZE_KEY, currentSize);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.canvas_editor_fragment, container, false);
        canvasView = ((CanvasView) view.findViewById(R.id.canvas_editor_canvas_view));
        canvasView.restore(savedInstanceState, CANVAS_KEY);
        undo = view.findViewById(R.id.canvas_editor_undo);
        redo = view.findViewById(R.id.canvas_editor_redo);
        sizeSlider = ((SeekBar) view.findViewById(R.id.canvas_editor_size_slider));
        eraser = view.findViewById(R.id.canvas_editor_eraser);
        pencil = view.findViewById(R.id.canvas_editor_pencil);
        boolean bundleExists = (savedInstanceState != null);
        drawMode = ((bundleExists && savedInstanceState.containsKey(DRAW_MODE_KEY)) ?
                savedInstanceState.getBoolean(DRAW_MODE_KEY) : true);
        activeColor = ((bundleExists && savedInstanceState.containsKey(ACTIVE_COLOR_KEY)) ?
                savedInstanceState.getInt(ACTIVE_COLOR_KEY) : Color.BLACK);
        currentSize = ((bundleExists && savedInstanceState.containsKey(CURRENT_SIZE_KEY)) ?
                savedInstanceState.getFloat(CURRENT_SIZE_KEY) : 0.0078125f);
        showBrush();
        canvasView.setBackgroundColor(BACKGROUND_COLOR);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasView.undo();
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasView.redo();
            }
        });
        sizeSlider.setMax(MAX_PROGRESS);
        sizeSlider.setProgress((int)(currentSize * MAX_PROGRESS / MAX_SIZE));
        sizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                currentSize = progress * MAX_SIZE / MAX_PROGRESS;
                canvasView.setSize(currentSize * (drawMode ? 1.0f : ERASER_SIZE_SCALE));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawMode = false;
                canvasView.setColor(BACKGROUND_COLOR);
                canvasView.setSize(currentSize * ERASER_SIZE_SCALE);
                showBrush();
            }
        });
        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawMode = true;
                canvasView.setColor(activeColor);
                canvasView.setSize(currentSize);
                showBrush();
            }
        });

        initColorPicker();
        view.findViewById(R.id.canvas_pallete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //colorPickersParent.setVisibility(View.VISIBLE);
                colorPicker.show();
            }
        });
        return view;
    }
    public CanvasEditorFragment addListener(CanvasListener listener) {
        canvasView.addListener(listener);
        return this;
    }
    public void clear() {
        canvasView.reset();
    }



    private void initColorPicker() {
        if (colorPicker != null) return;

        colorPicker = ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(R.color.startPageActionsFontColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Log.e("onColorSelected", Integer.toString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        Log.e("onPositiveButton", Integer.toString(selectedColor));
                        colorPicker.hide();
                        //colorPickersParrent.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("onNegativeButton", Integer.toString(which));
                        colorPicker.hide();
                        //colorPickersParrent.setVisibility(View.GONE);
                    }
                })
                .build();

        colorPicker.hide();
    }
}
