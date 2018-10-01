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

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A layout that contains and manages a {@link RecyclerView} with a {@link FastScroller}.
 * <p>
 * FastScrollView simplifies implementation by creating the RecyclerView and FastScroller,
 * and managing the FastScroller lifecycle. It's also useful when the parent ViewGroup
 * requires a single child view, for example a {@link SwipeRefreshLayout}.
 */
@SuppressWarnings("unused")
public class FastScrollView extends FrameLayout {

    private FastScroller fastScroller;
    private RecyclerView recyclerView;

    public FastScrollView(@NonNull Context context) {
        super(context);
        layout(context, null);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public FastScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        layout(context, attrs);
    }

    /**
     * Get the current {@link FastScroller} for this {@link FastScrollView}.
     *
     * @return The {@link FastScroller} instance
     */
    @NonNull
    public FastScroller getFastScroller() {
        return fastScroller;
    }

    /**
     * Get the current {@link RecyclerView} for this {@link FastScrollView}.
     *
     * @return The {@link RecyclerView} instance
     */
    @NonNull
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * Set a new {@link RecyclerView.Adapter} that implements {@link FastScroller.SectionIndexer}
     * to provide child views for the RecyclerView and section text for the FastScroller.
     *
     * @param adapter The new {@link RecyclerView.Adapter} to set, or null to set none
     */
    public void setAdapter(@Nullable RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);

        if (adapter instanceof FastScroller.SectionIndexer) {
            fastScroller.setSectionIndexer((FastScroller.SectionIndexer) adapter);
        } else if (adapter == null) {
            fastScroller.setSectionIndexer(null);
        }
    }

    /**
     * Set a new {@link RecyclerView.LayoutManager} for the RecyclerView.
     *
     * @param layoutManager The new {@link RecyclerView.LayoutManager} to set
     */
    public void setLayoutManager(@NonNull RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addView(recyclerView);
        recyclerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        fastScroller.attachRecyclerView(recyclerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setNestedScrollingEnabled(true);
        } else if (getParent() instanceof SwipeRefreshLayout) {
            fastScroller.setSwipeRefreshLayout((SwipeRefreshLayout) getParent());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        fastScroller.detachRecyclerView();
        removeAllViews();
        super.onDetachedFromWindow();
    }

    private void layout(Context context, AttributeSet attrs) {
        fastScroller = new FastScroller(context, attrs);
        fastScroller.setId(R.id.fast_scroller);
        recyclerView = new RecyclerView(context, attrs);
        recyclerView.setId(R.id.recycler_view);
    }
}
