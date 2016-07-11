package com.klinker.android.peekview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.klinker.android.peekview.util.DensityUtils;

public class PeekViewActivity extends AppCompatActivity {

    private int MOVE_THRESHOLD;

    private PeekView peekView;
    private Handler longClickHandler;

    private boolean preparing = false;
    private int startX;
    private int startY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        longClickHandler = new Handler();

        MOVE_THRESHOLD = DensityUtils.toDp(this, 8);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.v("touch_event", event.getAction() + "");
        if (peekView != null && event.getAction() == MotionEvent.ACTION_UP) {
            peekView.hide();
            peekView = null;
            return true;
        } else if (preparing &&
                (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE) &&
                (Math.abs(startX - event.getRawX()) > MOVE_THRESHOLD && Math.abs(startY - event.getRawY()) > MOVE_THRESHOLD)) {
            preparing = false;
            longClickHandler.removeCallbacksAndMessages(null);
            return false;
        }

        return super.dispatchTouchEvent(event);
    }

    public void preparePeek(final PeekView view, MotionEvent startEvent) {
        preparing = true;

        startX = (int) startEvent.getRawX();
        startY = (int) startEvent.getRawY();

        longClickHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                peekView = view;
                peekView.show();
            }
        }, ViewConfiguration.getLongPressTimeout());
    }
}
