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

import android.view.View
import androidx.recyclerview.widget.RecyclerView

typealias ItemSelectListener = (Int) -> Unit

abstract class ItemViewHolder<Item>(
    view: View,
    private val itemSelectListener: ItemSelectListener? = null
) : RecyclerView.ViewHolder(view), View.OnClickListener {

    init {
        itemSelectListener?.let { view.setOnClickListener(this) }
    }

    abstract fun bind(item: Item)

    override fun onClick(v: View?) {
        val position = bindingAdapterPosition

        if (position != RecyclerView.NO_POSITION) {
            itemSelectListener?.invoke(position)
        }
    }
}
