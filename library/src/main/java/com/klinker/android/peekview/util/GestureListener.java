package com.klinker.android.peekview.util;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.klinker.android.peekview.PeekViewActivity;
import com.klinker.android.peekview.builder.Peek;
import com.klinker.android.peekview.builder.PeekViewOptions;
import com.klinker.android.peekview.callback.OnPeek;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private PeekViewActivity activity;
    private View base;
    private Peek peek;

    public GestureListener(PeekViewActivity activity, View base, Peek peek) {
        this.activity = activity;
        this.base = base;
        this.peek = peek;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        base.performClick();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        peek.show(activity, event);
    }
}
