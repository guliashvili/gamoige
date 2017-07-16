package com.gamoige.a.gamoige.packages;

import com.gamoige.a.gamoige.MainActivity;
import com.gamoige.a.gamoige.PreviewActivity;

import java.io.Serializable;

/**
 * Created by Donsky on 7/15/2017.
 */

public interface Package extends Serializable {
    void doit(MainActivity mainActivity, PreviewActivity previewActivity);
}
