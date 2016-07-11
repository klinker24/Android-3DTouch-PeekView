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

import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.klinker.android.peekview.PeekView;
import com.klinker.android.peekview.PeekViewActivity;
import com.klinker.android.peekview.callback.OnPeek;

/**
 * This is a builder class to facilitate the creation of the PeekView.
 */
public class Peek {

    public static Peek into(@LayoutRes int layoutRes, OnPeek onPeek) {
        return new Peek(layoutRes, onPeek);
    }

    private int layoutRes;
    private OnPeek onPeek;
    private View base;
    private PeekViewOptions options;

    private Peek(@LayoutRes int layoutRes, @Nullable OnPeek onPeek) {
        this.layoutRes = layoutRes;
        this.onPeek = onPeek;
        this.options = new PeekViewOptions();
    }

    public Peek with(PeekViewOptions options) {
        this.options = options;
        return this;
    }

    public Peek applyTo(final PeekViewActivity activity, final View base) {
        this.base = base;

        this.base.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, final MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        PeekView peek = new PeekView(activity, options, layoutRes, onPeek);
                        peek.setOverMotionEvent(motionEvent);
                        activity.preparePeek(peek, motionEvent);

                        return true;
                    default:
                        return false;
                }
            }
        });

        return this;
    }
}
