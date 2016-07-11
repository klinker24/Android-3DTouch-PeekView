package com.klinker.android.peekview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.klinker.android.peekview.builder.PeekViewOptions;
import com.klinker.android.peekview.callback.OnPeek;
import com.klinker.android.peekview.util.DensityUtils;
import com.klinker.android.peekview.util.NavigationUtils;

public class PeekView extends FrameLayout {

    private static final int ANIMATION_TIME = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private static final int FINGER_SIZE_DP = 75;

    private final int FINGER_SIZE;

    private View content;
    private ViewGroup.LayoutParams contentParams;

    private View dim;

    private PeekViewOptions options;
    private int distanceFromTop;
    private int distanceFromLeft;
    private int screenWidth;
    private int screenHeight;
    private ViewGroup androidContentView = null;
    private OnPeek callback;

    public PeekView(Activity context, PeekViewOptions options, @LayoutRes int layoutRes, @Nullable OnPeek callback) {
        super(context);
        this.options = options;
        this.callback = callback;

        FINGER_SIZE = DensityUtils.toDp(context, FINGER_SIZE_DP);

        // get the main content view of the display
        androidContentView = (FrameLayout) context.findViewById(android.R.id.content);

        // initialize the display size
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenHeight = size.y;
        screenWidth = size.x;

        // set up the content we want to show
        content = LayoutInflater.from(context).inflate(layoutRes, this, false);
        contentParams = content.getLayoutParams();

        setWidthByPercent(options.getWidthPercent());
        setHeightByPercent(options.getHeightPercent());

        // tell the code that the view has been initialized and let them use it to
        // set up the layout.
        if (callback != null) {
            callback.initialized(content);
        }

        // add the background dim to the frame
        dim = new View(context);
        dim.setBackgroundColor(Color.BLACK);
        dim.setAlpha(options.getBackgroundDim());

        FrameLayout.LayoutParams dimParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dim.setLayoutParams(dimParams);

        // add the dim and the content view to the upper level frame layout
        addView(dim);
        addView(content);
    }

    /**
     * Sets how far away from the top of the screen the button should be displayed.
     * Distance should be the value in PX
     *
     * @param distance the distance from the top in px
     */
    private void setDistanceFromTop(int distance) {
        this.distanceFromTop = options.fullScreenPeek() ? 0 : distance;
    }

    /**
     * Sets how far away from the left side of the screen the button should be displayed.
     * Distance should be the value in PX
     *
     * @param distance the distance from the left in px
     */
    private void setDistanceFromLeft(int distance) {
        this.distanceFromLeft = options.fullScreenPeek() ? 0 : distance;
    }

    /**
     * Sets the width of the button. Distance should be the value in DP, it will be
     * converted to the appropriate pixel value
     *
     * @param width the width of the circle in px
     */
    private void setWidth(int width) {
        contentParams.width = options.fullScreenPeek() ? screenWidth : width;
        content.setLayoutParams(contentParams);
    }

    /**
     * Sets the height of the button. Distance should be the value in DP, it will be
     * converted to the appropriate pixel value
     *
     * @param height the height of the circle in px
     */
    private void setHeight(int height) {
        contentParams.height = options.fullScreenPeek() ? screenHeight : height;
        content.setLayoutParams(contentParams);
    }

    /**
     * Sets the width of the window according to the screen width
     *
     * @param percent as a decimal, (0.0 - 1.0)
     */
    public void setWidthByPercent(float percent) {
        setWidth((int) (screenWidth * percent));
    }

    /**
     * Sets the height of the window according to the screen height
     *
     * @param percent as a decimal (0.0 - 1.0)
     */
    public void setHeightByPercent(float percent) {
        setHeight((int) (screenHeight * percent));
    }

    /**
     * Places the peek view over the top of the given layout
     *
     * @param under view that the peek view should come above
     */
    public void setOverView(View under) {
        int[] location = new int[2];
        under.getLocationOnScreen(location);

        setStartPoints(location[0], location[1]);
    }

    /**
     * Places the peek view over the top of a motion event.
     *
     * @param event event that activates the peek view
     */
    public void setOverMotionEvent(MotionEvent event) {
        int x = (int) event.getX();

        // we don't want our finger to cover the content we are displaying, so, lets move the x value
        // of our touch event to one side or the other.
        // we will move it over as far as we can, so that

        // is the touch on the right or left side of the screen?
        if (x > screenWidth / 2) { // right
            // we want to move it to the left
            x -= ((contentParams.width / 2) + FINGER_SIZE);

            if (x < 0) {
                x = 0;
            }
        } else { // left
            // we want to move it to the right
            x += ((contentParams.width / 2) + FINGER_SIZE);

            if (x > screenWidth) {
                x = screenWidth;
            }
        }

        setStartPoints(x, (int) event.getY());
    }

    private void setStartPoints(int startX, int startY) {

        startX = startX - (contentParams.width / 2);
        startY = startY - (contentParams.height / 2);

        // check these against the layout bounds now
        if (startX < 0) {
            // if it is off the left side
            startX = 10;
        } else if (startX > screenWidth - contentParams.width) {
            // if it is off the right side
            startX = screenWidth - contentParams.width - 10;
        }

        int statusBar = NavigationUtils.getStatusBarHeight(getContext());
        if (startY < statusBar) { // if it is above the status bar and action bar
            startY = statusBar + 10;
        } else if (NavigationUtils.hasNavBar(getContext()) &&
                startY + contentParams.height > screenHeight - NavigationUtils.getNavBarHeight(getContext())) {
            // if there is a nav bar and the popup is underneath it
            startY = screenHeight - contentParams.height - NavigationUtils.getNavBarHeight(getContext()) - 10;
        } else if (!NavigationUtils.hasNavBar(getContext()) && startY + contentParams.height > screenHeight) {
            startY = screenHeight - contentParams.height - 10;
        }

        setDistanceFromLeft(startX);
        setDistanceFromTop(startY);
    }

    public void show() {
        androidContentView.addView(this);

        // we haven't specified a view to start from
        content.setTranslationX(distanceFromLeft);
        content.setTranslationY(distanceFromTop);

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f, 1.0f);
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (callback != null) {
                    callback.shown();
                }
            }
        });
        animator.setDuration(options.useFadeAnimation() ? ANIMATION_TIME : 0);
        animator.setInterpolator(INTERPOLATOR);
        animator.start();
    }

    public void hide() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0.0f);
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                androidContentView.removeView(PeekView.this);

                if (callback != null) {
                    callback.dismissed();
                }
            }
        });
        animator.setDuration(options.useFadeAnimation() ? ANIMATION_TIME : 0);
        animator.setInterpolator(INTERPOLATOR);
        animator.start();
    }

    private abstract class AnimatorEndListener implements Animator.AnimatorListener {
        @Override public void onAnimationStart(Animator animator) { }
        @Override public void onAnimationCancel(Animator animator) { }
        @Override public void onAnimationRepeat(Animator animator) { }
    }
}