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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * A {@link RecyclerView} that adds a {@link FastScroller} to its parent ViewGroup.
 * <p>
 * FastScrollRecyclerView simplifies implementation by creating and adding the FastScroller,
 * and managing its lifecycle.
 */
@SuppressWarnings("unused")
public class FastScrollRecyclerView extends RecyclerView {

    private FastScroller fastScroller;

    public FastScrollRecyclerView(@NonNull Context context) {
        super(context);
        layout(context, null);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public FastScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        layout(context, attrs);
    }

    /**
     * Set a new {@link RecyclerView.Adapter} that implements {@link FastScroller.SectionIndexer}
     * to provide child views for the RecyclerView and section text for the FastScroller.
     *
     * @param adapter The new {@link RecyclerView.Adapter} to set, or null to set none
     */
    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof FastScroller.SectionIndexer) {
            fastScroller.setSectionIndexer((FastScroller.SectionIndexer) adapter);
        } else if (adapter == null) {
            fastScroller.setSectionIndexer(null);
        }
    }

    /**
     * Set the visibility state of this view.
     *
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}
     */
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        fastScroller.setVisibility(visibility);
    }

    /**
     * Set a new {@link FastScroller.FastScrollListener} that will listen to fast scroll events.
     *
     * @param fastScrollListener The new {@link FastScroller.FastScrollListener} to set, or null to set none
     */
    public void setFastScrollListener(@Nullable FastScroller.FastScrollListener fastScrollListener) {
        fastScroller.setFastScrollListener(fastScrollListener);
    }

    /**
     * Set a new {@link FastScroller.SectionIndexer} that provides section text for the {@link FastScroller}.
     *
     * @param sectionIndexer The new {@link FastScroller.SectionIndexer} to set, or null to set no none
     */
    public void setSectionIndexer(@Nullable FastScroller.SectionIndexer sectionIndexer) {
        fastScroller.setSectionIndexer(sectionIndexer);
    }

    /**
     * Set the enabled state of fast scrolling.
     *
     * @param enabled True to enable fast scrolling, false otherwise
     */
    public void setFastScrollEnabled(boolean enabled) {
        fastScroller.setEnabled(enabled);
    }

    /**
     * Hide the scrollbar when not scrolling.
     *
     * @param hideScrollbar True to hide the scrollbar, false to show
     */
    public void setHideScrollbar(boolean hideScrollbar) {
        fastScroller.setHideScrollbar(hideScrollbar);
    }

    /**
     * Show the scroll track while scrolling.
     *
     * @param visible True to show scroll track, false to hide
     */
    public void setTrackVisible(boolean visible) {
        fastScroller.setTrackVisible(visible);
    }

    /**
     * Set the color of the scroll track.
     *
     * @param color The color for the scroll track
     */
    public void setTrackColor(@ColorInt int color) {
        fastScroller.setTrackColor(color);
    }

    /**
     * Set the color of the scroll handle.
     *
     * @param color The color for the scroll handle
     */
    public void setHandleColor(@ColorInt int color) {
        fastScroller.setHandleColor(color);
    }

    /**
     * Show the section bubble while scrolling.
     *
     * @param visible True to show the bubble, false to hide
     */
    public void setBubbleVisible(boolean visible) {
        fastScroller.setBubbleVisible(visible);
    }

    /**
     * Set the background color of the section bubble.
     *
     * @param color The background color for the section bubble
     */
    public void setBubbleColor(@ColorInt int color) {
        fastScroller.setBubbleColor(color);
    }

    /**
     * Set the text color of the section bubble.
     *
     * @param color The text color for the section bubble
     */
    public void setBubbleTextColor(@ColorInt int color) {
        fastScroller.setBubbleTextColor(color);
    }

    /**
     * Set the scaled pixel text size of the section bubble.
     *
     * @param size The scaled pixel text size for the section bubble
     */
    public void setBubbleTextSize(int size) {
        fastScroller.setBubbleTextSize(size);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        fastScroller.attachRecyclerView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        fastScroller.detachRecyclerView();
        super.onDetachedFromWindow();
    }

    private void layout(Context context, AttributeSet attrs) {
        fastScroller = new FastScroller(context, attrs);
        fastScroller.setId(R.id.fast_scroller);
    }
}
