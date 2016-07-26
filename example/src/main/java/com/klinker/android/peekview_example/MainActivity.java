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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.klinker.android.peekview.builder.Peek;
import com.klinker.android.peekview.PeekViewActivity;
import com.klinker.android.peekview.builder.PeekViewOptions;
import com.klinker.android.peekview.callback.OnPeek;
import com.klinker.android.peekview.callback.SimpleOnPeek;
import com.klinker.android.simple_videoview.SimpleVideoView;


public class MainActivity extends PeekViewActivity {

    private static final String TALON_ICON = "https://lh6.ggpht.com/W27xhTGcBY1Bcn1PdlRQeSstMuiBMK3iptcr_DL7b5Hz0sGBezkQIw9pjcLnLEY1cQ=w300-rw";
    private static final String EVOLVE_ICON = "https://lh5.ggpht.com/SH0GFeQzs7w6RZoQ5PIxndvUPvoB1PB8eW_p28oeiRzw8P0MOThX_n_6H0iuJ1LKD9FT=w300-rw";
    private static final String SOURCE_ICON = "https://lh3.ggpht.com/Bgg_cMk8HCEKPqQqBJwG-BUp_YrectAo1pL5DAQ2Bzv4cfKE5ipwWq9QlzegiOoLUyQ=w300-rw";
    private static final String BLUR_ICON = "https://lh4.ggpht.com/YwmoCGxKzgWiSCZ3PzeX6P7hrjJl26dTfFfZLfwckDOBdT5h8CyAXs7x_tttC0RdbVIb=w300-rw";

    private static final String TALON_LINK = "https://play.google.com/store/apps/details?id=com.klinker.android.twitter_l";
    private static final String EVOLVE_LINK = "https://play.google.com/store/apps/details?id=com.klinker.android.evolve_sms";
    private static final String SOURCE_LINK = "https://play.google.com/store/apps/details?id=com.klinker.android.source";
    private static final String BLUR_LINK = "https://play.google.com/store/apps/details?id=com.klinker.android.launcher";

    private View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.coordinator_layout);

        initHelloPeek();
        initImagePreview();
        initGifPeek();
        initVideoPeek();

        initAppPeeks();
    }

    private void initHelloPeek() {
        Peek.into(R.layout.hello_peek, null)
                .applyTo(this, findViewById(R.id.description));
    }

    private void initImagePreview() {
        PeekViewOptions options = new PeekViewOptions();
        options.setFullScreenPeek(true);
        options.setBackgroundDim(1f);
        options.setBlurredView(root);

        ImageView imageView = (ImageView) findViewById(R.id.image_peek);

        Peek.into(R.layout.image_peek, new SimpleOnPeek() {
            @Override
            public void onInflated(View rootView) {
                ((ImageView) rootView.findViewById(R.id.image))
                        .setImageDrawable(getResources().getDrawable(R.drawable.klinker_apps));
            }
        }).with(options).applyTo(this, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "this is a normal click.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initGifPeek() {
        Glide.with(this).load("http://pbs.twimg.com/tweet_video_thumb/CnGhaZGWgAE2aO8.jpg").into((ImageView) findViewById(R.id.gif_iv));

        PeekViewOptions options = new PeekViewOptions()
                .setAbsoluteWidth(200)
                .setAbsoluteHeight(200)
                .setBlurredView(root);

        Peek.into(R.layout.gif_peek, new OnPeek() {
            private SimpleVideoView videoView;

            @Override public void shown() { }
            @Override public void onInflated(View rootView) {
                videoView = (SimpleVideoView) rootView.findViewById(R.id.video);
                videoView.start("https://pbs.twimg.com/tweet_video/CnGhaZGWgAE2aO8.mp4");
            }
            @Override public void dismissed() {
                videoView.release();
            }
        }).with(options).applyTo(this, findViewById(R.id.gif_iv));
    }

    private void initVideoPeek() {
        Glide.with(this).load("http://pbs.twimg.com/tweet_video_thumb/Cm7nySIWYAATnnI.jpg")
                .into((ImageView) findViewById(R.id.video_iv));

        PeekViewOptions options = new PeekViewOptions()
                .setFullScreenPeek(true)
                .setBlurredView(root);

        Peek.into(R.layout.video_peek, new SimpleOnPeek() {
            private SimpleVideoView videoView;

            @Override
            public void onInflated(View rootView) {
                videoView = (SimpleVideoView) rootView.findViewById(R.id.video);
                videoView.start("https://pbs.twimg.com/tweet_video/Cm7nySIWYAATnnI.mp4");
            }

            @Override
            public void dismissed() {
                videoView.release();
            }
        }).with(options).applyTo(this, findViewById(R.id.video_iv));
    }

    private void initAppPeeks() {
        Glide.with(this).load(TALON_ICON).into((ImageView) findViewById(R.id.talon));
        Glide.with(this).load(EVOLVE_ICON).into((ImageView) findViewById(R.id.evolve));
        Glide.with(this).load(SOURCE_ICON).into((ImageView) findViewById(R.id.source));
        Glide.with(this).load(BLUR_ICON).into((ImageView) findViewById(R.id.blur));

        Peek.into(R.layout.web_peek, getWebPeek(TALON_LINK)).applyTo(this, findViewById(R.id.talon_layout));
        Peek.into(R.layout.web_peek, getWebPeek(EVOLVE_LINK)).applyTo(this, findViewById(R.id.evolve));
        Peek.into(R.layout.web_peek, getWebPeek(SOURCE_LINK)).with(new PeekViewOptions().setAbsoluteHeight(100).setAbsoluteWidth(100)).applyTo(this, findViewById(R.id.source));
        Peek.into(R.layout.web_peek, getWebPeek(BLUR_LINK)).with(new PeekViewOptions().setAbsoluteHeight(35).setAbsoluteWidth(35)).applyTo(this, findViewById(R.id.blur));

        findViewById(R.id.talon_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Testing touch feedback", Toast.LENGTH_SHORT).show();
            }
        });

        //Peek.clear(findViewById(R.id.talon_layout));
    }

    private OnPeek getWebPeek(final String url) {
        return new OnPeek() {
            private WebView webView;
            @Override public void shown() { }
            @Override public void onInflated(View rootView) {
                webView = setupWebView(rootView);
                webView.loadUrl(url);
            }

            @Override public void dismissed() {
                webView.loadUrl("");
            }
        };
    }

    private WebView setupWebView(View rootView) {
        WebView webView = (WebView) rootView.findViewById(R.id.web_view);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.loadUrl(url);
                return false;
            }
        });

        return webView;
    }
}
