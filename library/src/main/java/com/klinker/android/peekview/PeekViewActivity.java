package com.klinker.android.peekview;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public class PeekViewActivity extends AppCompatActivity {
    private PeekView peekView;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (peekView != null && event.getAction() == MotionEvent.ACTION_UP) {
            peekView.hide();
            peekView = null;
            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    public void showPeekView(PeekView view) {
        this.peekView = view;
        this.peekView.show();
    }
}
