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

package com.l4digital.fastscroll.example

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.l4digital.fastscroll.FastScroller

import java.util.ArrayList

class ExampleAdapter : RecyclerView.Adapter<ExampleAdapter.ViewHolder>(),
    FastScroller.SectionIndexer {

    private val itemList: MutableList<String> = ArrayList()

    init {
        for (i in 0..25) {
            // add several items for each letter in the alphabet
            for (x in 0..4) {
                itemList.add(Character.toString((65 + i).toChar()) + " example item")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_example, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    override fun getSectionText(position: Int) = itemList[position][0].toString()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView as TextView

        fun bind(item: String) {
            textView.text = item
        }
    }
}
