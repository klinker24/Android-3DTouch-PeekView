package com.klinker.android.peekview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.klinker.android.peekview.util.DensityUtils;

public class PeekViewActivity extends AppCompatActivity {

    private PeekView peekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (peekView != null) {
            peekView.hide();
            peekView = null;
        }
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
        }

        return super.dispatchTouchEvent(event);
    }

    public void showPeek(final PeekView view) {
        peekView = view;
        peekView.show();
    }
}
