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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.l4digital.fastscroll.FastScroller;
import com.l4digital.fastscroll.OnFastScrollStateChangeListener;

import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        FastScrollRecyclerView recyclerView = (FastScrollRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ExampleAdapter());
        recyclerView.setOnFastScrollStateChangeListener(new OnFastScrollStateChangeListener() {
            @Override
            public void onFastScrollStart() {
                Toast.makeText(ExampleActivity.this, "Fast scroll started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFastScrollStop() {
                Toast.makeText(ExampleActivity.this, "Fast scroll stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ViewHolder> implements FastScroller.SectionIndexer {

        private final List<String> mItemList;

        public ExampleAdapter() {
            mItemList = new ArrayList<>();

            for (int i = 0; i < 26; i++) {
                // add several items for each letter in the alphabet
                for (int x = 0; x < 5; x++) {
                    mItemList.add(Character.toString((char) (65 + i)) + " example item");
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ViewHolder(inflater.inflate(R.layout.item_example, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(mItemList.get(position));
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        @Override
        public String getSectionText(int position) {
            return String.valueOf(mItemList.get(position).charAt(0));
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView;
            }

            public void bind(String item) {
                mTextView.setText(item);
            }
        }
    }
}
