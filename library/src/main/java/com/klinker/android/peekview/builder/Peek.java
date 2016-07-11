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

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
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

    private int layoutRes = 0;
    private View layout = null;

    private PeekViewOptions options = new PeekViewOptions();
    private OnPeek callbacks;

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

                        // we want to consume this touch event.
                        return true;
                    default:
                        // we don't need to consume any other touch events.
                        return false;
                }
            }
        });
    }
}
