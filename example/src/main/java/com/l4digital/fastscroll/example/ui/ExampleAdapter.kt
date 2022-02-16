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

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.l4digital.fastscroll.example.databinding.ItemExampleBinding

@Suppress("MagicNumber")
class ExampleAdapter : RecyclerView.Adapter<ExampleAdapter.ViewHolder>(),
    FastScroller.SectionIndexer {

    private val itemList = mutableListOf<String>()

    private var layoutInflater: LayoutInflater? = null

    init {
        repeat(26) { i ->
            // add several items for each letter in the alphabet
            repeat(4) {
                itemList.add("${(65 + i).toChar()} example item")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = getLayoutInflater(parent.context)
        return ViewHolder(ItemExampleBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    override fun getSectionText(position: Int) = itemList[position][0].toString()

    private fun getLayoutInflater(context: Context) =
        layoutInflater ?: LayoutInflater.from(context).also { layoutInflater = it }

    class ViewHolder(private val binding: ItemExampleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.root.text = item
        }
    }
}
