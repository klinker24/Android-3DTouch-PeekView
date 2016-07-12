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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        longClickHandler = new Handler();

        MOVE_THRESHOLD = DensityUtils.toDp(this, 8);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (peekView != null && event.getAction() == MotionEvent.ACTION_UP) {

            // the user lifted their finger, so we are going to remove the peek view

            peekView.hide();
            peekView = null;

            return false;
        } else if (peekView != null) {

            // we don't want to pass along the touch event or else it will just scroll under the PeekView

            return false;
        } else if (preparing && event.getAction() == MotionEvent.ACTION_UP) {
            // the user lifted their finger before the view had been shown, so we don't want to show it

            preparing = false;
            longClickHandler.removeCallbacksAndMessages(null);
        } else if (preparing && event.getAction() == MotionEvent.ACTION_MOVE &&
                (Math.abs(startX - event.getRawX()) > MOVE_THRESHOLD && Math.abs(startY - event.getRawY()) > MOVE_THRESHOLD)) {

            // if the user has long clicked, and this is a move event, we want to use an 8 dip threshold
            // before we cancel the long click.

            // this means that they must move their finger more than 8 dip before the handler is cancelled.
            // we are required to do this since the activity will receive every touch event, there is no
            // threshold built in.

            preparing = false;
            longClickHandler.removeCallbacksAndMessages(null);
        }

        return super.dispatchTouchEvent(event);
    }

    public void preparePeek(final PeekView view, MotionEvent startEvent) {
        preparing = true;

        // we need to save these so that we can get an accurate MOVE_THRESHOLD before cancelling
        // the long click handler from an ACTION_MOVE event
        startX = (int) startEvent.getRawX();
        startY = (int) startEvent.getRawY();

        // show the peek view after the long press timeout
        longClickHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                peekView = view;
                peekView.show();
            }
        }, ViewConfiguration.getLongPressTimeout());
    }

    public int getMoveThreshold() {
        return MOVE_THRESHOLD;
    }
}
