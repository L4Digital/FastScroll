/*
 * Copyright 2016 L4 Digital LLC. All rights reserved.
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

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;

import com.l4digital.fastscroll.FastScroller.SectionIndexer;

@SuppressWarnings("unused, WeakerAccess")
public class FastScrollRecyclerView extends RecyclerView {

    private FastScroller mFastScroller;

    public FastScrollRecyclerView(Context context) {
        super(context);
        prepareLayout(context, null);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareLayout(context, attrs);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof SectionIndexer) {
            setSectionIndexer((SectionIndexer) adapter);
        } else if (adapter == null) {
            setSectionIndexer(null);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>If fast scrolling is enabled the scroll handle should be shown, when smooth scrolling to a given position in order to provide
     * additional visual feedback.</p>
     */
    @Override
    public void smoothScrollToPosition(int position) {
        startFastScroll();
        super.smoothScrollToPosition(position);
    }

    /**
     * {@inheritDoc}
     *
     * <p>If fast scrolling is enabled the scroll handle should be shown, when smooth scrolling to the specified location in order to
     * provide additional visual feedback.</p>
     */
    @Override
    public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
        startFastScroll();
        super.smoothScrollBy(dx, dy, interpolator);
    }

    /**
     * Set the {@link SectionIndexer} for the {@link FastScroller}.
     *
     * @param sectionIndexer The SectionIndexer that provides section text for the FastScroller
     */
    public void setSectionIndexer(SectionIndexer sectionIndexer) {
        mFastScroller.setSectionIndexer(sectionIndexer);
    }

    /**
     * Set the enabled state of fast scrolling.
     *
     * @param enabled True to enable fast scrolling, false otherwise
     */
    public void setFastScrollEnabled(boolean enabled) {
        mFastScroller.setEnabled(enabled);
    }

    /**
     * Check if the state of fast scrolling is enabled.
     *
     * @return True when enabled and false when disabled
     */
    public boolean isFastScrollEnabled() {
        return mFastScroller.isEnabled();
    }

    /**
     * <p>Manually start fast scrolling behavior and show related visual components. Only the handle and the scrollbar will showed when allowed.</p>
     *
     * <p>Only works if fast scrolling is enabled ({@link FastScrollRecyclerView#isFastScrollEnabled()} {@code returns "true"}) to begin with.</p>
     *
     * <p>Can be used when additional visual feedback is need for an action performed by an external source.</p>
     */
    public void startFastScroll() {
        if (mFastScroller.isEnabled()) {
            mFastScroller.startFastScroll();
        }
    }

    /**
     * <p>Manually stop fast scrolling behavior and hide any visible visual components.</p>
     *
     * <p>Only works if fast scrolling is enabled ({@link FastScrollRecyclerView#isFastScrollEnabled()} {@code returns "true"}) to begin with.</p>
     */
    public void stopFastScroll() {
        if (mFastScroller.isEnabled()) {
            mFastScroller.stopFastScroll();
        }
    }

    /**
     * Hide the scrollbar when not scrolling.
     *
     * @param isHidden True to hide the scrollbar, false to show
     */
    public void setHideScrollbar(boolean isHidden) {
        mFastScroller.setHideScrollbar(isHidden);
    }

    /**
     * Display a scroll track while scrolling.
     *
     * @param isVisible True to show scroll track, false to hide
     */
    public void setTrackVisible(boolean isVisible) {
        mFastScroller.setTrackVisible(isVisible);
    }

    /**
     * Set the color of the scroll track.
     *
     * @param color The color for the scroll track
     */
    public void setTrackColor(@ColorInt int color) {
        mFastScroller.setTrackColor(color);
    }

    /**
     * Set the color for the scroll handle.
     *
     * @param color The color for the scroll handle
     */
    public void setHandleColor(@ColorInt int color) {
        mFastScroller.setHandleColor(color);
    }

    /**
     * Set the background color of the index bubble.
     *
     * @param color The background color for the index bubble
     */
    public void setBubbleColor(@ColorInt int color) {
        mFastScroller.setBubbleColor(color);
    }

    /**
     * Set the text color of the index bubble.
     *
     * @param color The text color for the index bubble
     */
    public void setBubbleTextColor(@ColorInt int color) {
        mFastScroller.setBubbleTextColor(color);
    }

    /**
     * Set the fast scroll state change listener.
     *
     * @param fastScrollStateChangeListener The interface that will listen to fastscroll state change events
     */
    public void setFastScrollStateChangeListener(FastScrollStateChangeListener fastScrollStateChangeListener) {
        mFastScroller.setFastScrollStateChangeListener(fastScrollStateChangeListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFastScroller.attachRecyclerView(this);

        ViewParent parent = getParent();

        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            viewGroup.addView(mFastScroller);
            mFastScroller.setLayoutParams(viewGroup);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mFastScroller.detachRecyclerView();
        super.onDetachedFromWindow();
    }

    protected void prepareLayout(Context context, AttributeSet attrs) {
        mFastScroller = new FastScroller(context, attrs);
        mFastScroller.setId(R.id.fastscroller);
    }

}
