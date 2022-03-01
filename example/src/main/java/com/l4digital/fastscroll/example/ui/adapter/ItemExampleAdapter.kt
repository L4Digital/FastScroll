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

package com.l4digital.fastscroll.example.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.l4digital.fastscroll.FastScroller
import com.l4digital.fastscroll.example.databinding.ItemExampleBinding

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem.hashCode() == newItem.hashCode()
    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
}

class ItemExampleAdapter(
    itemList: List<String>? = null,
    private val itemSelectListener: ItemSelectListener? = null
) : ListAdapter<String, ItemExampleAdapter.ViewHolder>(DIFF_CALLBACK), FastScroller.SectionIndexer {

    init {
        itemList?.let { submitList(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemExampleBinding.inflate(inflater, parent, false)
        return ViewHolder(itemBinding, itemSelectListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    override fun getSectionText(position: Int) = getItem(position)[0].toString()

    class ViewHolder(
        private val binding: ItemExampleBinding,
        itemSelectListener: ItemSelectListener?
    ) : ItemViewHolder<String>(binding.root, itemSelectListener) {

        override fun bind(item: String) {
            binding.root.text = item
        }
    }
}
