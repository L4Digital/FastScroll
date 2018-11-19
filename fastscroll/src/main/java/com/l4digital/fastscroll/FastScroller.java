/*
 * Copyright 2018 L4 Digital. All rights reserved.
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
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A ListView-like FastScroller for the {@link RecyclerView}.
 * <p>
 * FastScroller provides the fast scrolling and section indexing for a RecyclerView,
 * with a Lollipop styled scrollbar and section “bubble” view. The scrollbar provides
 * a handle for quickly navigating the list while the bubble view displays the
 * currently visible section index.
 * <p>
 * The following attributes can be set to customize the visibility and appearance of
 * the elements within the FastScroller view:
 * <p>
 * {@link R.styleable#FastScroller_hideScrollbar}
 * {@link R.styleable#FastScroller_showBubble}
 * {@link R.styleable#FastScroller_showTrack}
 * {@link R.styleable#FastScroller_handleColor}
 * {@link R.styleable#FastScroller_trackColor}
 * {@link R.styleable#FastScroller_bubbleColor}
 * {@link R.styleable#FastScroller_bubbleSize}
 * {@link R.styleable#FastScroller_bubbleTextColor}
 * {@link R.styleable#FastScroller_bubbleTextSize}
 */
public class FastScroller extends LinearLayout {

    public enum Size {
        NORMAL(R.drawable.fastscroll_bubble, R.dimen.fastscroll_bubble_text_size),
        SMALL(R.drawable.fastscroll_bubble_small, R.dimen.fastscroll_bubble_text_size_small);

        @DrawableRes public int drawableId;
        @DimenRes public int textSizeId;

        Size(@DrawableRes int drawableId, @DimenRes int textSizeId) {
            this.drawableId = drawableId;
            this.textSizeId = textSizeId;
        }

        public static Size fromOrdinal(int ordinal) {
            return ordinal >= 0 && ordinal < values().length ? values()[ordinal] : NORMAL;
        }
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
    private Drawable bubbleImage;
    private Drawable handleImage;
    private Drawable trackImage;
    private ImageView handleView;
    private ImageView trackView;
    private RecyclerView recyclerView;
    private Size bubbleSize;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView bubbleView;
    private View scrollbar;
    private ViewPropertyAnimator scrollbarAnimator;
    private ViewPropertyAnimator bubbleAnimator;

    private FastScrollListener fastScrollListener;
    private SectionIndexer sectionIndexer;

    private final Runnable scrollbarHider = new Runnable() {

        @Override
        public void run() {
            hideScrollbar();
        }
    };

    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!handleView.isSelected() && isEnabled()) {
                setViewPositions(getScrollProportion(recyclerView));
            }

            if (swipeRefreshLayout != null) {
                int firstVisibleItem = findFirstVisibleItemPosition(recyclerView.getLayoutManager());
                int topPosition = recyclerView.getChildCount() == 0 ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topPosition >= 0);
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
        this(context, Size.NORMAL);
    }

    public FastScroller(@NonNull Context context, Size size) {
        super(context);
        layout(context, size);
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

            if (fastScrollListener != null) {
                fastScrollListener.onFastScrollStart(this);
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

            if (fastScrollListener != null) {
                fastScrollListener.onFastScrollStop(this);
            }

            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Set the enabled state of this view.
     *
     * @param enabled True if this view is enabled, false otherwise
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setVisibility(enabled ? VISIBLE : GONE);
    }

    /**
     * Set the {@link ViewGroup.LayoutParams} associated with this view. These supply
     * parameters to the <i>parent</i> of this view specifying how it should be
     * arranged.
     *
     * @param params The {@link ViewGroup.LayoutParams} for this view, cannot be null
     */
    @Override
    public void setLayoutParams(@NonNull ViewGroup.LayoutParams params) {
        params.width = LayoutParams.WRAP_CONTENT;
        super.setLayoutParams(params);
    }

    /**
     * Set the {@link ViewGroup.LayoutParams} associated with this view. These supply
     * parameters to the <i>parent</i> of this view specifying how it should be
     * arranged.
     *
     * @param viewGroup The parent {@link ViewGroup} for this view, cannot be null
     */
    public void setLayoutParams(@NonNull ViewGroup viewGroup) {
        int recyclerViewId = recyclerView != null ? recyclerView.getId() : NO_ID;
        int marginTop = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_margin_top);
        int marginBottom = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_margin_bottom);

        if (recyclerViewId == NO_ID) {
            throw new IllegalArgumentException("RecyclerView must have a view ID");
        }

        if (viewGroup instanceof ConstraintLayout) {
            ConstraintSet constraintSet = new ConstraintSet();
            int endId = recyclerView.getParent() == getParent() ? recyclerViewId : ConstraintSet.PARENT_ID;
            int startId = getId();

            constraintSet.clone((ConstraintLayout) viewGroup);
            constraintSet.connect(startId, ConstraintSet.TOP, endId, ConstraintSet.TOP);
            constraintSet.connect(startId, ConstraintSet.BOTTOM, endId, ConstraintSet.BOTTOM);
            constraintSet.connect(startId, ConstraintSet.END, endId, ConstraintSet.END);
            constraintSet.applyTo((ConstraintLayout) viewGroup);

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) getLayoutParams();

            layoutParams.height = 0;
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);

        } else if (viewGroup instanceof CoordinatorLayout) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) getLayoutParams();

            layoutParams.height = LayoutParams.MATCH_PARENT;
            layoutParams.anchorGravity = GravityCompat.END;
            layoutParams.setAnchorId(recyclerViewId);
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);

        } else if (viewGroup instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();

            layoutParams.height = LayoutParams.MATCH_PARENT;
            layoutParams.gravity = GravityCompat.END;
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);

        } else if (viewGroup instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            int endRule = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                    RelativeLayout.ALIGN_END : RelativeLayout.ALIGN_RIGHT;

            layoutParams.height = 0;
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

    /**
     * Set the {@link RecyclerView} associated with this {@link FastScroller}. This allows the
     * FastScroller to set its layout parameters and listen for scroll changes.
     *
     * @param recyclerView The {@link RecyclerView} to attach, cannot be null
     * @see #detachRecyclerView()
     */
    public void attachRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        if (getParent() instanceof ViewGroup) {
            setLayoutParams((ViewGroup) getParent());
        } else if (recyclerView.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) recyclerView.getParent();
            viewGroup.addView(this);
            setLayoutParams(viewGroup);
        }

        recyclerView.addOnScrollListener(scrollListener);

        post(new Runnable() {

            @Override
            public void run() {
                // set initial positions for bubble and handle
                setViewPositions(getScrollProportion(FastScroller.this.recyclerView));
            }
        });
    }

    /**
     * Clears references to the attached {@link RecyclerView} and stops listening for scroll changes.
     *
     * @see #attachRecyclerView(RecyclerView)
     */
    public void detachRecyclerView() {
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(scrollListener);
            recyclerView = null;
        }
    }

    /**
     * Set a new {@link FastScrollListener} that will listen to fast scroll events.
     *
     * @param fastScrollListener The new {@link FastScrollListener} to set, or null to set none
     */
    public void setFastScrollListener(@Nullable FastScrollListener fastScrollListener) {
        this.fastScrollListener = fastScrollListener;
    }

    /**
     * Set a new {@link SectionIndexer} that provides section text for this {@link FastScroller}.
     *
     * @param sectionIndexer The new {@link SectionIndexer} to set, or null to set none
     */
    public void setSectionIndexer(@Nullable SectionIndexer sectionIndexer) {
        this.sectionIndexer = sectionIndexer;
    }

    /**
     * Set a {@link SwipeRefreshLayout} to disable when the {@link RecyclerView} is scrolled away from the top.
     * <p>
     * Required when {@link Build.VERSION#SDK_INT} < {@value Build.VERSION_CODES#LOLLIPOP}, otherwise use
     * {@link View#setNestedScrollingEnabled(boolean) setNestedScrollingEnabled(true)}.
     *
     * @param swipeRefreshLayout The {@link SwipeRefreshLayout} to set, or null to set none
     */
    public void setSwipeRefreshLayout(@Nullable SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
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
     * Show the scroll track while scrolling.
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
     * Set the color of the scroll handle.
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
     * Show the section bubble while scrolling.
     *
     * @param visible True to show the bubble, false to hide
     */
    public void setBubbleVisible(boolean visible) {
        showBubble = visible;
    }

    /**
     * Set the background color of the section bubble.
     *
     * @param color The background color for the section bubble
     */
    public void setBubbleColor(@ColorInt int color) {
        bubbleColor = color;

        if (bubbleImage == null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), bubbleSize.drawableId);

            if (drawable != null) {
                bubbleImage = DrawableCompat.wrap(drawable);
                bubbleImage.mutate();
            }
        }

        DrawableCompat.setTint(bubbleImage, bubbleColor);
        ViewCompat.setBackground(bubbleView, bubbleImage);
    }

    /**
     * Set the text color of the section bubble.
     *
     * @param color The text color for the section bubble
     */
    public void setBubbleTextColor(@ColorInt int color) {
        bubbleView.setTextColor(color);
    }

    /**
     * Set the scaled pixel text size of the section bubble.
     *
     * @param size The scaled pixel text size for the section bubble
     */
    public void setBubbleTextSize(int size) {
        bubbleView.setTextSize(size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
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
        bubbleHeight = bubbleView.getMeasuredHeight();
        handleHeight = handleView.getMeasuredHeight();

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

    private int findFirstVisibleItemPosition(@NonNull final RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null)[0];
        }

        return 0;
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

    private void layout(Context context, AttributeSet attrs) {
        layout(context, attrs, Size.NORMAL);
    }

    private void layout(Context context, Size size) {
        layout(context, null, size);
    }

    @SuppressWarnings("ConstantConditions")
    private void layout(Context context, AttributeSet attrs, Size size) {
        inflate(context, R.layout.fast_scroller, this);

        setClipChildren(false);
        setOrientation(HORIZONTAL);

        bubbleView = findViewById(R.id.fastscroll_bubble);
        handleView = findViewById(R.id.fastscroll_handle);
        trackView = findViewById(R.id.fastscroll_track);
        scrollbar = findViewById(R.id.fastscroll_scrollbar);

        bubbleSize = size;

        final int accent = getColorAttr(context, R.attr.colorAccent);
        final int transparent = ContextCompat.getColor(context, android.R.color.transparent);
        @ColorInt int bubbleColor = accent;
        @ColorInt int handleColor = accent;
        @ColorInt int trackColor = transparent;
        @ColorInt int textColor = transparent;

        boolean hideScrollbar = true;
        boolean showBubble = true;
        boolean showTrack = false;

        float textSize = getResources().getDimension(size.textSizeId);

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

                    int sizeOrdinal = typedArray.getInt(R.styleable.FastScroller_bubbleSize, size.ordinal());
                    bubbleSize = Size.fromOrdinal(sizeOrdinal);

                    textSize = typedArray.getDimension(R.styleable.FastScroller_bubbleTextSize,
                            getResources().getDimension(bubbleSize.textSizeId));
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

        bubbleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    private static int getColorAttr(Context context, int attrId) {
        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }


    /**
     * A FastScrollListener can be added to a {@link FastScroller} to receive messages when a
     * fast scrolling event has occurred.
     *
     * @see FastScroller#setFastScrollListener(FastScrollListener)
     */
    public interface FastScrollListener {

        /**
         * Called when fast scrolling begins.
         */
        void onFastScrollStart(FastScroller fastScroller);

        /**
         * Called when fast scrolling ends.
         */
        void onFastScrollStop(FastScroller fastScroller);
    }

    /**
     * A SectionIndexer can be added to a {@link FastScroller} to provide the text to display
     * for the visible section while fast scrolling.
     *
     * @see FastScroller#setSectionIndexer(SectionIndexer)
     */
    public interface SectionIndexer {

        /**
         * Get the text to be displayed for the visible section at the current position.
         *
         * @param position The current position of the visible section
         * @return The text to display
         */
        CharSequence getSectionText(int position);
    }
}
