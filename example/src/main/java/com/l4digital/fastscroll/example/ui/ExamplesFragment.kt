/*
 * Copyright 2022 Randy Webster. All rights reserved.
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

package com.l4digital.fastscroll.example.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.l4digital.fastscroll.example.R
import com.l4digital.fastscroll.example.databinding.FragmentExamplesBinding
import com.l4digital.fastscroll.example.extension.viewBinding
import com.l4digital.fastscroll.example.ui.adapter.ItemExampleAdapter
import com.l4digital.fastscroll.example.ui.adapter.ItemSelectListener

const val EXAMPLES_FRAGMENT_TAG = "ExamplesFragment"

class ExamplesFragment(private val containerViewId: Int) : Fragment(), ItemSelectListener {

    private val exampleList = mapOf(
        0 to "Compose Example",
        R.layout.example_layout_constraint to "ConstraintLayout Example",
        R.layout.example_layout_coordinator to "CoordinatorLayout Example",
        R.layout.example_layout_frame to "FrameLayout Example",
        R.layout.example_layout_relative to "RelativeLayout Example",
        R.layout.example_layout_swipe_refresh to "SwipeRefreshLayout Example",
        R.layout.example_bubble_small_always to "Small Bubble Always Example",
        R.layout.example_bubble_none to "No Bubble Example",
        R.layout.example_track_visible to "Show Track Example"
    )

    private val exampleListAdapter = ItemExampleAdapter(exampleList.map { it.value }, this)

    private val viewBinding by viewBinding(FragmentExamplesBinding::inflate)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        viewBinding.apply { recyclerView.adapter = exampleListAdapter }.root

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.app_name)
    }

    override fun onItemSelected(position: Int) {
        val example = exampleList.entries.elementAt(position)
        val fragment = when (example.key) {
            0 -> ExampleComposeFragment()
            else -> ExampleLayoutFragment(example.key)
        }

        activity?.title = example.value
        parentFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right
            )
            replace(containerViewId, fragment)
            addToBackStack(null)
        }
    }
}
