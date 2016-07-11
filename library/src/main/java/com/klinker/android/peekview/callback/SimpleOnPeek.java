package com.klinker.android.peekview.callback;

/**
 * Wrapper class for if you only need to implement the initialization method
 */
public abstract class SimpleOnPeek implements OnPeek {

    @Override public void shown() { }
    @Override public void dismissed() { }
}
