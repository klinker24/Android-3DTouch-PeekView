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

package com.klinker.android.peekview_example;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.klinker.android.peekview.builder.Peek;
import com.klinker.android.peekview.PeekViewActivity;
import com.klinker.android.peekview.builder.PeekViewOptions;
import com.klinker.android.peekview.callback.SimpleOnPeek;


public class MainActivity extends PeekViewActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHelloPeek();
        initImagePreview();
    }

    private void initHelloPeek() {
        Peek.into(R.layout.hello_peek, null)
                .applyTo(this, findViewById(R.id.description));
    }

    private void initImagePreview() {
        PeekViewOptions options = new PeekViewOptions();
        options.setFullScreenPeek(true);
        options.setBackgroundDim(1f);

        ImageView imageView = (ImageView) findViewById(R.id.image_peek);

        Peek.into(R.layout.image_peek, new SimpleOnPeek() {
            @Override
            public void initialized(View rootView) {
                ((ImageView) rootView.findViewById(R.id.image))
                        .setImageDrawable(getResources().getDrawable(R.drawable.klinker_apps));
            }
        }).with(options).applyTo(this, imageView);
    }
}
