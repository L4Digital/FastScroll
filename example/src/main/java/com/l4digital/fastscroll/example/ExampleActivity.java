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

package com.l4digital.fastscroll.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.l4digital.fastscroll.FastScrollRecyclerView;

public class ExampleActivity extends AppCompatActivity {

    FastScrollRecyclerView mRecyclerView;
    LinearLayoutManager mManager;
    //GridLayoutManager mManager;
    ExampleAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        mRecyclerView = findViewById(R.id.recycler_view);

        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // mManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mManager.setStackFromEnd(false);

        mAdapter = new ExampleAdapter();

        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    // TODO: replace the hardcoded Strings at some point.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_fast_scroll:
                Toast.makeText(this, "Showing FastScroll...", Toast.LENGTH_SHORT).show();

                mRecyclerView.startFastScroll();
                return true;

            case R.id.action_hide_fast_scroll:
                Toast.makeText(this, "Hiding FastScroll...", Toast.LENGTH_SHORT).show();

                mRecyclerView.stopFastScroll();
                return true;

            case R.id.action_scroll_to:
                Toast.makeText(this, "Scrolling to position 25...", Toast.LENGTH_SHORT).show();

                mRecyclerView.smoothScrollToPosition(25);
                return true;

            case R.id.action_reverse_layout:
                Toast.makeText(this, "Reversing the layout", Toast.LENGTH_SHORT).show();

                mManager.setReverseLayout(!mManager.getReverseLayout());
                mAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
