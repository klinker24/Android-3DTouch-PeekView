package com.klinker.android.peekview.callback;

import android.view.View;

/**
 * Provides callbacks for the lifecycle events of the PeekView
 */
public interface OnPeek {

    void initialized(View rootView);
    void shown();
    void dismissed();

}
