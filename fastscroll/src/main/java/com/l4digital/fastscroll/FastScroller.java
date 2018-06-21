/*
 * Copyright 2018 Globant. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.l4digital.fastscroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FastScroller extends LinearLayout {

    public interface SectionIndexer {

        String getSectionText(int position);
    }

    private static final int BUBBLE_ANIM_DURATION = 100;
    private static final int SCROLLBAR_ANIM_DURATION = 300;
    private static final int SCROLLBAR_HIDE_DELAY = 1000;
    private static final int TRACK_SNAP_RANGE = 5;

    @ColorInt private int bubbleColor;
    @ColorInt private int handleColor;

    private int bubbleHeight;
    private int handleHeight;
    private int viewHeight;
    private boolean hideScrollbar;
    private boolean showBubble;
    private SectionIndexer sectionIndexer;
    private ViewPropertyAnimator scrollbarAnimator;
    private ViewPropertyAnimator bubbleAnimator;
    private RecyclerView recyclerView;
    private TextView bubbleView;
    private ImageView handleView;
    private ImageView trackView;
    private View scrollbar;
    private Drawable bubbleImage;
    private Drawable handleImage;
    private Drawable trackImage;

    private FastScrollStateChangeListener fastScrollStateChangeListener;

    private Runnable scrollbarHider = new Runnable() {

        @Override
        public void run() {
            hideScrollbar();
        }
    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!handleView.isSelected() && isEnabled()) {
                setViewPositions(getScrollProportion(recyclerView));
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (isEnabled()) {
                switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    getHandler().removeCallbacks(scrollbarHider);
                    cancelAnimation(scrollbarAnimator);

                    if (!isViewVisible(scrollbar)) {
                        showScrollbar();
                    }

                    break;

                case RecyclerView.SCROLL_STATE_IDLE:
                    if (hideScrollbar && !handleView.isSelected()) {
                        getHandler().postDelayed(scrollbarHider, SCROLLBAR_HIDE_DELAY);
                    }

                    break;
                }
            }
        }
    };

    public FastScroller(@NonNull Context context) {
        super(context);
        layout(context, null);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    public FastScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScroller(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        layout(context, attrs);
        setLayoutParams(generateLayoutParams(attrs));
    }

    @Override
    public void setLayoutParams(@NonNull ViewGroup.LayoutParams params) {
        params.width = LayoutParams.WRAP_CONTENT;
        super.setLayoutParams(params);
    }

    public void setLayoutParams(@NonNull ViewGroup viewGroup) {
        @IdRes int recyclerViewId = recyclerView != null ? recyclerView.getId() : NO_ID;
        int marginTop = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_margin_top);
        int marginBottom = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_margin_bottom);

        if (recyclerViewId == NO_ID) {
            throw new IllegalArgumentException("RecyclerView must have a view ID");
        }

        if (viewGroup instanceof ConstraintLayout) {
            ConstraintSet constraintSet = new ConstraintSet();
            @IdRes int layoutId = getId();

            constraintSet.clone((ConstraintLayout) viewGroup);
            constraintSet.connect(layoutId, ConstraintSet.TOP, recyclerViewId, ConstraintSet.TOP);
            constraintSet.connect(layoutId, ConstraintSet.BOTTOM, recyclerViewId, ConstraintSet.BOTTOM);
            constraintSet.connect(layoutId, ConstraintSet.END, recyclerViewId, ConstraintSet.END);
            constraintSet.applyTo((ConstraintLayout) viewGroup);

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) getLayoutParams();
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);

        } else if (viewGroup instanceof CoordinatorLayout) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) getLayoutParams();

            layoutParams.setAnchorId(recyclerViewId);
            layoutParams.anchorGravity = GravityCompat.END;
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);

        } else if (viewGroup instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();

            layoutParams.gravity = GravityCompat.END;
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);

        } else if (viewGroup instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            int endRule = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                    RelativeLayout.ALIGN_END : RelativeLayout.ALIGN_RIGHT;

            layoutParams.addRule(RelativeLayout.ALIGN_TOP, recyclerViewId);
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, recyclerViewId);
            layoutParams.addRule(endRule, recyclerViewId);
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);

        } else {
            throw new IllegalArgumentException("Parent ViewGroup must be a ConstraintLayout, CoordinatorLayout, FrameLayout, or RelativeLayout");
        }

        updateViewHeights();
    }

    public void setSectionIndexer(@Nullable SectionIndexer sectionIndexer) {
        this.sectionIndexer = sectionIndexer;
    }

    public void attachRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        //noinspection ConstantConditions
        if (this.recyclerView != null) {
            this.recyclerView.addOnScrollListener(scrollListener);
            post(new Runnable() {

                @Override
                public void run() {
                    // set initial positions for bubble and handle
                    setViewPositions(getScrollProportion(FastScroller.this.recyclerView));
                }
            });
        }
    }

    public void detachRecyclerView() {
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(scrollListener);
            recyclerView = null;
        }
    }

    /**
     * Hide the scrollbar when not scrolling.
     *
     * @param hideScrollbar True to hide the scrollbar, false to show
     */
    public void setHideScrollbar(boolean hideScrollbar) {
        this.hideScrollbar = hideScrollbar;
        scrollbar.setVisibility(hideScrollbar ? GONE : VISIBLE);
    }

    /**
     * Show the section bubble while scrolling.
     *
     * @param visible True to show the bubble, false to hide
     */
    public void setBubbleVisible(boolean visible) {
        showBubble = visible;
    }

    /**
     * Display a scroll track while scrolling.
     *
     * @param visible True to show scroll track, false to hide
     */
    public void setTrackVisible(boolean visible) {
        trackView.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Set the color of the scroll track.
     *
     * @param color The color for the scroll track
     */
    public void setTrackColor(@ColorInt int color) {
        @ColorInt int trackColor = color;

        if (trackImage == null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fastscroll_track);

            if (drawable != null) {
                trackImage = DrawableCompat.wrap(drawable);
                trackImage.mutate();
            }
        }

        DrawableCompat.setTint(trackImage, trackColor);
        trackView.setImageDrawable(trackImage);
    }

    /**
     * Set the color for the scroll handle.
     *
     * @param color The color for the scroll handle
     */
    public void setHandleColor(@ColorInt int color) {
        handleColor = color;

        if (handleImage == null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fastscroll_handle);

            if (drawable != null) {
                handleImage = DrawableCompat.wrap(drawable);
                handleImage.mutate();
            }
        }

        DrawableCompat.setTint(handleImage, handleColor);
        handleView.setImageDrawable(handleImage);
    }

    /**
     * Set the background color of the index bubble.
     *
     * @param color The background color for the index bubble
     */
    public void setBubbleColor(@ColorInt int color) {
        bubbleColor = color;

        if (bubbleImage == null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fastscroll_bubble);

            if (drawable != null) {
                bubbleImage = DrawableCompat.wrap(drawable);
                bubbleImage.mutate();
            }
        }

        DrawableCompat.setTint(bubbleImage, bubbleColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            bubbleView.setBackground(bubbleImage);
        } else {
            //noinspection deprecation
            bubbleView.setBackgroundDrawable(bubbleImage);
        }
    }

    /**
     * Set the text color of the index bubble.
     *
     * @param color The text color for the index bubble
     */
    public void setBubbleTextColor(@ColorInt int color) {
        bubbleView.setTextColor(color);
    }

    /**
     * Set the fast scroll state change listener.
     *
     * @param fastScrollStateChangeListener The interface that will listen to fastscroll state change events
     */
    public void setFastScrollStateChangeListener(@Nullable FastScrollStateChangeListener fastScrollStateChangeListener) {
        this.fastScrollStateChangeListener = fastScrollStateChangeListener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setVisibility(enabled ? VISIBLE : GONE);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (event.getX() < handleView.getX() - ViewCompat.getPaddingStart(handleView)) {
                return false;
            }

            requestDisallowInterceptTouchEvent(true);
            setHandleSelected(true);

            getHandler().removeCallbacks(scrollbarHider);
            cancelAnimation(scrollbarAnimator);
            cancelAnimation(bubbleAnimator);

            if (!isViewVisible(scrollbar)) {
                showScrollbar();
            }

            if (showBubble && sectionIndexer != null) {
                showBubble();
            }

            if (fastScrollStateChangeListener != null) {
                fastScrollStateChangeListener.onFastScrollStart(this);
            }
        case MotionEvent.ACTION_MOVE:
            final float y = event.getY();
            setViewPositions(y);
            setRecyclerViewPosition(y);
            return true;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            requestDisallowInterceptTouchEvent(false);
            setHandleSelected(false);

            if (hideScrollbar) {
                getHandler().postDelayed(scrollbarHider, SCROLLBAR_HIDE_DELAY);
            }

            hideBubble();

            if (fastScrollStateChangeListener != null) {
                fastScrollStateChangeListener.onFastScrollStop(this);
            }

            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;

            if (handleView.getY() == 0) {
                proportion = 0f;
            } else if (handleView.getY() + handleHeight >= viewHeight - TRACK_SNAP_RANGE) {
                proportion = 1f;
            } else {
                proportion = y / (float) viewHeight;
            }

            int scrolledItemCount = Math.round(proportion * itemCount);

            if (isLayoutReversed(recyclerView.getLayoutManager())) {
                scrolledItemCount = itemCount - scrolledItemCount;
            }

            int targetPos = getValueInRange(0, itemCount - 1, scrolledItemCount);
            recyclerView.getLayoutManager().scrollToPosition(targetPos);

            if (showBubble && sectionIndexer != null) {
                bubbleView.setText(sectionIndexer.getSectionText(targetPos));
            }
        }
    }

    private float getScrollProportion(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return 0;
        }

        final int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
        final int verticalScrollRange = recyclerView.computeVerticalScrollRange();
        final float rangeDiff = verticalScrollRange - viewHeight;
        float proportion = (float) verticalScrollOffset / (rangeDiff > 0 ? rangeDiff : 1f);
        return viewHeight * proportion;
    }

    @SuppressWarnings("SameParameterValue")
    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    private void setViewPositions(float y) {
        bubbleHeight = bubbleView.getHeight();
        handleHeight = handleView.getHeight();

        int bubbleY = getValueInRange(0, viewHeight - bubbleHeight - handleHeight / 2, (int) (y - bubbleHeight));
        int handleY = getValueInRange(0, viewHeight - handleHeight, (int) (y - handleHeight / 2));

        if (showBubble) {
            bubbleView.setY(bubbleY);
        }

        handleView.setY(handleY);
    }

    private void updateViewHeights() {
        int measureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

        bubbleView.measure(measureSpec, measureSpec);
        bubbleHeight = bubbleView.getMeasuredHeight();
        handleView.measure(measureSpec, measureSpec);
        handleHeight = handleView.getMeasuredHeight();
    }

    private boolean isLayoutReversed(@NonNull final RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).getReverseLayout();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getReverseLayout();
        }

        return false;
    }

    private boolean isViewVisible(View view) {
        return view != null && view.getVisibility() == VISIBLE;
    }

    private void cancelAnimation(ViewPropertyAnimator animator) {
        if (animator != null) {
            animator.cancel();
        }
    }

    private void showBubble() {
        if (!isViewVisible(bubbleView)) {
            bubbleView.setVisibility(VISIBLE);
            bubbleAnimator = bubbleView.animate().alpha(1f)
                    .setDuration(BUBBLE_ANIM_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        // adapter required for new alpha value to stick
                    });
        }
    }

    private void hideBubble() {
        if (isViewVisible(bubbleView)) {
            bubbleAnimator = bubbleView.animate().alpha(0f)
                    .setDuration(BUBBLE_ANIM_DURATION)
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            bubbleView.setVisibility(GONE);
                            bubbleAnimator = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            bubbleView.setVisibility(GONE);
                            bubbleAnimator = null;
                        }
                    });
        }
    }

    private void showScrollbar() {
        if (recyclerView.computeVerticalScrollRange() - viewHeight > 0) {
            float transX = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end);

            scrollbar.setTranslationX(transX);
            scrollbar.setVisibility(VISIBLE);
            scrollbarAnimator = scrollbar.animate().translationX(0f).alpha(1f)
                    .setDuration(SCROLLBAR_ANIM_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        // adapter required for new alpha value to stick
                    });
        }
    }

    private void hideScrollbar() {
        float transX = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end);

        scrollbarAnimator = scrollbar.animate().translationX(transX).alpha(0f)
                .setDuration(SCROLLBAR_ANIM_DURATION)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        scrollbar.setVisibility(GONE);
                        scrollbarAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        scrollbar.setVisibility(GONE);
                        scrollbarAnimator = null;
                    }
                });
    }

    private void setHandleSelected(boolean selected) {
        handleView.setSelected(selected);
        DrawableCompat.setTint(handleImage, selected ? bubbleColor : handleColor);
    }

    @SuppressWarnings("ConstantConditions")
    private void layout(Context context, AttributeSet attrs) {
        inflate(context, R.layout.fastscroller, this);

        setClipChildren(false);
        setOrientation(HORIZONTAL);

        bubbleView = findViewById(R.id.fastscroll_bubble);
        handleView = findViewById(R.id.fastscroll_handle);
        trackView = findViewById(R.id.fastscroll_track);
        scrollbar = findViewById(R.id.fastscroll_scrollbar);

        @ColorInt int bubbleColor = Color.GRAY;
        @ColorInt int handleColor = Color.DKGRAY;
        @ColorInt int trackColor = Color.LTGRAY;
        @ColorInt int textColor = Color.WHITE;

        boolean hideScrollbar = true;
        boolean showBubble = true;
        boolean showTrack = false;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FastScroller, 0, 0);

            if (typedArray != null) {
                try {
                    bubbleColor = typedArray.getColor(R.styleable.FastScroller_bubbleColor, bubbleColor);
                    handleColor = typedArray.getColor(R.styleable.FastScroller_handleColor, handleColor);
                    trackColor = typedArray.getColor(R.styleable.FastScroller_trackColor, trackColor);
                    textColor = typedArray.getColor(R.styleable.FastScroller_bubbleTextColor, textColor);
                    hideScrollbar = typedArray.getBoolean(R.styleable.FastScroller_hideScrollbar, hideScrollbar);
                    showBubble = typedArray.getBoolean(R.styleable.FastScroller_showBubble, showBubble);
                    showTrack = typedArray.getBoolean(R.styleable.FastScroller_showTrack, showTrack);
                } finally {
                    typedArray.recycle();
                }
            }
        }

        setTrackColor(trackColor);
        setHandleColor(handleColor);
        setBubbleColor(bubbleColor);
        setBubbleTextColor(textColor);
        setHideScrollbar(hideScrollbar);
        setBubbleVisible(showBubble);
        setTrackVisible(showTrack);
    }
}
