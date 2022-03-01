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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.l4digital.fastscroll.example.R
import com.l4digital.fastscroll.example.data.ExampleDataProvider
import com.l4digital.fastscroll.example.databinding.FragmentExamplesBinding
import com.l4digital.fastscroll.example.extension.toastShort
import com.l4digital.fastscroll.example.extension.viewBinding
import com.l4digital.fastscroll.example.ui.adapter.ItemExampleAdapter

class ExampleSortingFragment : Fragment() {

    private var isSorted = false

    private val binding by viewBinding(FragmentExamplesBinding::inflate)

    private val itemListAdapter = ItemExampleAdapter()

    private val itemList
        get() = when {
            isSorted -> ExampleDataProvider.androidList.toMutableList().apply { sort() }
            else -> ExampleDataProvider.androidList.toMutableList().apply { shuffle() }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return binding.apply {
            recyclerView.adapter = itemListAdapter.apply { submitList(itemList) }
            recyclerView.setFastScrollEnabled(isSorted)
        }.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.menu_sorting, menu)

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_sort -> {
            isSorted = !isSorted
            itemListAdapter.submitList(itemList)
            binding.recyclerView.setFastScrollEnabled(isSorted)
            context?.toastShort(if (isSorted) "Sorted" else "Shuffled")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
