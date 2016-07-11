package com.klinker.android.peekview.callback;

import android.view.View;

public interface OnPeek {

    void initialized(View rootView);
    void shown();
    void dismissed();

}
