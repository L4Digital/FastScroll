# FastScroll
[![License](http://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat-square)](https://developer.android.com/about/versions/android-4.0.html)
[![Download](https://img.shields.io/badge/JCenter-1.1.1-brightgreen.svg?style=flat-square)](https://bintray.com/l4digital/maven/FastScroll/_latestVersion)

A ListView-like FastScroller for Android’s RecyclerView.

<img src="https://raw.githubusercontent.com/L4Digital/FastScroll/master/fastscroll_example.png" alt="screenshot" width="270">

FastScroll brings the popular fast scrolling and section indexing features of Android’s ListView to the RecyclerView with a Lollipop styled scrollbar and section “bubble” view. The scrollbar provides a handle for quickly navigating a list while the bubble view displays the currently visible section index.

FastScroll was inspired by this [Styling Android blog post](https://blog.stylingandroid.com/recyclerview-fastscroll-part-1/).



## Download

#### Gradle:
~~~groovy
dependencies {
    compile 'com.l4digital.fastscroll:fastscroll:1.1.1'
}
~~~

#### Maven:
~~~xml
<dependency>
  <groupId>com.l4digital.fastscroll</groupId>
  <artifactId>fastscroll</artifactId>
  <version>1.1.1</version>
</dependency>
~~~



## Usage
`FastScrollRecyclerView` extends Android's `RecyclerView` and can be setup the same way.

~~~java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    FastScrollRecyclerView recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new ExampleAdapter());
}
~~~

Add the `FastScrollRecyclerView` to your xml layout and set your customizations using attributes.  

*The parent ViewGroup must be a ConstraintLayout, CoordinatorLayout, FrameLayout, or RelativeLayout in order for the FastScroller to be properly displayed on top of the RecyclerView.*

~~~xml
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.l4digital.fastscroll.FastScrollRecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:bubbleColor="#00bb00"
        app:bubbleTextColor="#ffffff"
        app:handleColor="#999999"
        app:trackColor="#bbbbbb"
        app:hideScrollbar="false"
        app:showTrack="false" />

</FrameLayout>
~~~

Implement the `FastScroller.SectionIndexer` interface in your RecyclerView Adapter and override `getSectionText()`.

~~~java
class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ViewHolder> implements FastScroller.SectionIndexer {

    ...

    @Override
    public String getSectionText(int position) {
        return getItem(position).getIndex();
    }
}
~~~

#### Alternative Usage:
If you are unable to use the `FastScrollRecyclerView`, you can add a `FastScroller` to your layout and implement with any `RecyclerView`. See this [github issue](https://github.com/L4Digital/FastScroll/issues/4#issuecomment-256975634) for an example.



## License
    Copyright 2017 L4 Digital LLC. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.