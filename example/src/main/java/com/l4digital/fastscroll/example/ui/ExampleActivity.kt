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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.l4digital.fastscroll.example.databinding.ActivityExampleBinding
import com.l4digital.fastscroll.example.extension.context
import com.l4digital.fastscroll.example.extension.setContentView
import com.l4digital.fastscroll.example.extension.viewBinding

class ExampleActivity : AppCompatActivity() {

    private val viewBinding by viewBinding(ActivityExampleBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.setContentView(this).apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = ExampleAdapter()
        }
    }
}
