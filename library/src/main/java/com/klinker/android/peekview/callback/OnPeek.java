package com.klinker.android.peekview.callback;

import android.view.View;

/**
 * Provides callbacks for the lifecycle events of the PeekView
 */
public interface OnPeek {

    void onInflation(View rootView);
    void shown();
    void dismissed();

}
