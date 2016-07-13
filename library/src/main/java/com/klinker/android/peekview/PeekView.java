package com.klinker.android.peekview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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
    private static final int FINGER_SIZE_DP = 130;

    private int FINGER_SIZE;

    private View content;
    private ViewGroup.LayoutParams contentParams;

    private View dim;

    private PeekViewOptions options;
    private int distanceFromTop;
    private int distanceFromLeft;
    private int screenWidth;
    private int screenHeight;
    private ViewGroup androidContentView = null;
    private OnPeek callbacks;

    public PeekView(Activity context, PeekViewOptions options, @LayoutRes int layoutRes, @Nullable OnPeek callbacks) {
        super(context);
        init(context, options, LayoutInflater.from(context).inflate(layoutRes, this, false), callbacks);
    }

    public PeekView(Activity context, PeekViewOptions options, @NonNull View content, @Nullable OnPeek callbacks) {
        super(context);
        init(context, options, content, callbacks);
    }

    private void init(Activity context, PeekViewOptions options, @NonNull View content, @Nullable OnPeek callbacks) {
        this.options = options;
        this.callbacks = callbacks;

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
        this.content = content;
        contentParams = content.getLayoutParams();

        if (options.getAbsoluteHeight() != 0) {
            setHeight(DensityUtils.toPx(context, options.getAbsoluteHeight()));
        } else {
            setHeightByPercent(options.getHeightPercent());
        }

        if (options.getAbsoluteWidth() != 0) {
            setWidth(DensityUtils.toPx(context, options.getAbsoluteWidth()));
        } else {
            setWidthByPercent(options.getWidthPercent());
        }

        // tell the code that the view has been onInflated and let them use it to
        // set up the layout.
        if (callbacks != null) {
            callbacks.onInflated(content);
        }

        // add the background dim to the frame
        dim = new View(context);
        dim.setBackgroundColor(Color.BLACK);
        dim.setAlpha(options.getBackgroundDim());

        FrameLayout.LayoutParams dimParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dim.setLayoutParams(dimParams);

        // add the dim and the content view to the upper level frame layout
        addView(dim);
        addView(content);
    }

    /**
     * Sets how far away from the top of the screen the view should be displayed.
     * Distance should be the value in PX.
     *
     * @param distance the distance from the top in px.
     */
    private void setDistanceFromTop(int distance) {
        this.distanceFromTop = options.fullScreenPeek() ? 0 : distance;
    }

    /**
     * Sets how far away from the left side of the screen the view should be displayed.
     * Distance should be the value in PX.
     *
     * @param distance the distance from the left in px.
     */
    private void setDistanceFromLeft(int distance) {
        this.distanceFromLeft = options.fullScreenPeek() ? 0 : distance;
    }

    /**
     * Sets the width of the view in PX.
     *
     * @param width the width of the circle in px
     */
    private void setWidth(int width) {
        contentParams.width = options.fullScreenPeek() ? screenWidth : width;
        content.setLayoutParams(contentParams);
    }

    /**
     * Sets the height of the view in PX.
     *
     * @param height the height of the circle in px
     */
    private void setHeight(int height) {
        contentParams.height = options.fullScreenPeek() ? screenHeight : height;
        content.setLayoutParams(contentParams);
    }

    /**
     * Sets the width of the window according to the screen width.
     *
     * @param percent of screen width
     */
    public void setWidthByPercent(@FloatRange(from=0,to=1) float percent) {
        setWidth((int) (screenWidth * percent));
    }

    /**
     * Sets the height of the window according to the screen height.
     *
     * @param percent of screen height
     */
    public void setHeightByPercent(@FloatRange(from=0,to=1) float percent) {
        setHeight((int) (screenHeight * percent));
    }

    /**
     * Places the peek view over the top of a motion event.
     *
     * @param event event that activates the peek view
     */
    public void setOffsetByMotionEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        int movedX = x;
        int movedY = y;

        // we don't want our finger to cover the content we are displaying, so, lets move the x value
        // of our touch event to one side or the other.
        // we will move it over as far as we can, so that

        // is the touch on the right or left side of the screen?
        if (x > screenWidth / 2) { // right
            // we want to move it to the left
            movedX -= ((contentParams.width / 2) + FINGER_SIZE);

            if (movedX < 0) {
                movedX = 0;

                // we should try moving vertically instead, since x hits the left bound and may still cover the finger

                // is y at the top or bottom half of the screen?
                if (y > screenHeight / 2) { // bottom half, move it up
                    movedY -= ((contentParams.height / 2) + FINGER_SIZE);

                    if (movedY < 0) {
                        movedY = 0;
                    }
                } else { // top half, move it down
                    movedY += ((contentParams.height / 2) + FINGER_SIZE);

                    if (movedY > screenHeight) {
                        movedY = screenHeight;
                    }
                }
            }
        } else { // left
            // we want to move it to the right
            movedX += ((contentParams.width / 2) + FINGER_SIZE);

            if (movedX > screenWidth) {
                movedX = screenWidth;

                // we should try moving vertically instead, since x hits the left bound and may still cover the finger

                // is y at the top or bottom half of the screen?
                if (y > screenHeight / 2) { // bottom half, move it up
                    movedY -= ((contentParams.height / 2) + FINGER_SIZE);

                    if (movedY < 0) {
                        movedY = 0;
                    }
                } else { // top half, move it down
                    movedY += ((contentParams.height / 2) + FINGER_SIZE);

                    if (movedY > screenHeight) {
                        movedY = screenHeight;
                    }
                }
            }
        }

        // now pick which values to use...
        if (movedX < screenWidth && movedX > 0) {
            // we know that the x movement didn't move it off the edge, so we got the full finger displacement
            x = movedX;
        } else if (y == screenHeight || y == 0) {
            // we know that the y movement pushed it off to the edge of the screen, so we want to use the x displacement
            x = movedX;
        } else {
            y = movedY;
        }

        setContentOffset(x, y);
    }

    private void setContentOffset(int startX, int startY) {

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
            startY = screenHeight - contentParams.height - NavigationUtils.getNavBarHeight(getContext()) - DensityUtils.toDp(getContext(), 10);
        } else if (!NavigationUtils.hasNavBar(getContext()) && startY + contentParams.height > screenHeight) {
            startY = screenHeight - contentParams.height - DensityUtils.toDp(getContext(), 10);
        }

        setDistanceFromLeft(startX);
        setDistanceFromTop(startY);
    }

    /**
     * Show the content of the PeekView by adding it to the android.R.id.content FrameLayout.
     */
    public void show() {
        androidContentView.addView(this);

        // set the translations for the content view
        content.setTranslationX(distanceFromLeft);
        content.setTranslationY(distanceFromTop);

        // animate the alpha of the PeekView
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f, 1.0f);
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (callbacks != null) {
                    callbacks.shown();
                }
            }
        });
        animator.setDuration(options.useFadeAnimation() ? ANIMATION_TIME : 0);
        animator.setInterpolator(INTERPOLATOR);
        animator.start();
    }

    /**
     * Hide the PeekView and remove it from the android.R.id.content FrameLayout.
     */
    public void hide() {

        // animate with a fade
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0.0f);
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                // remove the view from the screen
                androidContentView.removeView(PeekView.this);

                if (callbacks != null) {
                    callbacks.dismissed();
                }
            }
        });
        animator.setDuration(options.useFadeAnimation() ? ANIMATION_TIME : 0);
        animator.setInterpolator(INTERPOLATOR);
        animator.start();
    }

    /**
     * Wrapper class so we only have to implement the onAnimationEnd method.
     */
    private abstract class AnimatorEndListener implements Animator.AnimatorListener {
        @Override public void onAnimationStart(Animator animator) { }
        @Override public void onAnimationCancel(Animator animator) { }
        @Override public void onAnimationRepeat(Animator animator) { }
    }
}