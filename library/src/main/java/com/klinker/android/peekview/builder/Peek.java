/*
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.klinker.android.peekview.builder;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.klinker.android.peekview.PeekView;
import com.klinker.android.peekview.PeekViewActivity;
import com.klinker.android.peekview.callback.OnPeek;

/**
 * This is a builder class to facilitate the creation of the PeekView.
 */
public class Peek {

    public static Peek into(@LayoutRes int layoutRes, @Nullable OnPeek onPeek) {
        return new Peek(layoutRes, onPeek);
    }

    public static Peek into(View layout, @Nullable OnPeek onPeek) {
        return new Peek(layout, onPeek);
    }

    /**
     * Used to clear the peeking ability. This could be useful for a RecyclerView/ListView, where a recycled item
     * shouldn't use the PeekView, but the original item did.
     *
     * @param view the view we want to stop peeking into
     */
    public static void clear(View view) {
        view.setOnTouchListener(null);
    }

    private int layoutRes = 0;
    private View layout = null;

    private PeekViewOptions options = new PeekViewOptions();
    private OnPeek callbacks;

    private int downImpactPointX;
    private int downImpactPointY;

    private Peek(@LayoutRes int layoutRes, @Nullable OnPeek callbacks) {
        this.layoutRes = layoutRes;
        this.callbacks = callbacks;
    }

    private Peek(View layout, @Nullable OnPeek callbacks) {
        this.layout = layout;
        this.callbacks = callbacks;
    }

    /**
     * Apply the options to the PeekView, when it is shown.
     *
     * @param options
     */
    public Peek with(PeekViewOptions options) {
        this.options = options;
        return this;
    }

    /**
     * Finish the builder by selecting the base view that you want to show the PeekView from.
     *
     * @param activity the PeekViewActivity that you are on.
     * @param base the view that you want to touch to apply the peek to.
     */
    public void applyTo(final PeekViewActivity activity, final View base) {
        base.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, final MotionEvent motionEvent) {
                Log.v("peek_action", "motion event: " + motionEvent.getAction());
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        // the user has touched the PeekView base view, so prepare the activity
                        // to show the PeekView after a long click.

                        PeekView peek;

                        if (layout == null) {
                            peek = new PeekView(activity, options, layoutRes, callbacks);
                        } else {
                            peek = new PeekView(activity, options, layout, callbacks);
                        }

                        peek.setOffsetByMotionEvent(motionEvent);
                        activity.preparePeek(peek, motionEvent);

                        downImpactPointX = (int) motionEvent.getRawX();
                        downImpactPointY = (int) motionEvent.getRawY();

                        forceRippleAnimation(base, motionEvent);

                        // we want to consume this touch event.
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(downImpactPointX - motionEvent.getRawX()) < activity.getMoveThreshold() &&
                                Math.abs(downImpactPointY - motionEvent.getRawY()) < activity.getMoveThreshold()) {
                            // manually click the view if it is less than the move threshold
                            base.performClick();
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        // we don't need to consume any other touch events.
                        return false;
                }
            }
        });
    }

    private void forceRippleAnimation(View view, MotionEvent event) {
        Drawable background = view.getBackground();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && background instanceof RippleDrawable) {
            final RippleDrawable rippleDrawable = (RippleDrawable) background;

            rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
            rippleDrawable.setHotspot(event.getX(), event.getY());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rippleDrawable.setState(new int[]{});
                }
            }, 300);
        }
    }
}
