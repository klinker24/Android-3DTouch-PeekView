package com.klinker.android.peekview.builder;

import android.support.annotation.FloatRange;

public class PeekViewOptions {

    @FloatRange(from=.1,to=.9)
    private float widthPercent = .6f;

    @FloatRange(from=.1,to=.9)
    private float heightPercent = .5f;

    // 0.0 = fully transparent background dim
    // 1.0 = fully opaque (black) background dim
    @FloatRange(from=0,to=1)
    private float backgroundDim = 0.6f;

    private boolean hapticFeedback = true;


    // region setters
    public void setWidthPercent(@FloatRange(from=.1,to=.9) float widthPercent) {
        this.widthPercent = widthPercent;
    }

    public void setHeightPercent(@FloatRange(from=.1,to=.9) float heightPercent) {
        this.heightPercent = heightPercent;
    }

    public void setBackgroundDim(@FloatRange(from=0,to=1) float backgroundDim) {
        this.backgroundDim = backgroundDim;
    }

    public void setHapticFeedback(boolean useFeedback) {
        this.hapticFeedback = useFeedback;
    }
    //endregion

    // region getters
    public float getWidthPercent() {
        return widthPercent;
    }

    public float getHeightPercent() {
        return heightPercent;
    }

    public float getBackgroundDim() {
        return backgroundDim;
    }

    public boolean getHapticFeedback() {
        return hapticFeedback;
    }
    // endregion
}
