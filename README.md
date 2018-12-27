# FastScroll
[![License](http://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0) [![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat-square)](https://developer.android.com/about/versions/android-4.0.html) [![Download](https://img.shields.io/badge/JCenter-2.0.1-brightgreen.svg?style=flat-square)](https://bintray.com/l4digital/maven/FastScroll/_latestVersion)

A ListView-like FastScroller for Android’s RecyclerView.

<img src="https://raw.githubusercontent.com/L4Digital/FastScroll/master/fastscroll_example.png" alt="screenshot" width="270">

FastScroll brings the popular fast scrolling and section indexing features of Android’s ListView to the RecyclerView with a Lollipop styled scrollbar and section “bubble” view. The scrollbar provides a handle for quickly navigating a list while the bubble view displays the currently visible section index.

FastScroll was inspired by this [Styling Android blog post](https://blog.stylingandroid.com/recyclerview-fastscroll-part-1/).


## Download

#### Gradle:
~~~groovy
dependencies {
    implementation 'com.l4digital.fastscroll:fastscroll:2.0.1'
}
~~~

#### Maven:
~~~xml
<dependency>
  <groupId>com.l4digital.fastscroll</groupId>
  <artifactId>fastscroll</artifactId>
  <version>2.0.1</version>
</dependency>
~~~


## Usage
There are a few ways to implement the FastScroll library:

* The [FastScrollRecyclerView](#fastscrollrecyclerview) is a `RecyclerView` that creates and adds the `FastScroller` to its parent ViewGroup.

* The [FastScrollView](#fastscrollview) is a layout that creates and manages a `RecyclerView` with a `FastScroller`. `FastScrollView` is particularly useful when the parent ViewGroup requires a single child view, for example a `SwipeRefreshLayout`.

#### FastScrollRecyclerView:
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
        app:handleColor="#999999" />

</FrameLayout>
~~~

`FastScrollRecyclerView` extends Android's `RecyclerView` and can be setup the same way.

~~~kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_example)

    val recyclerView: FastScrollRecyclerView? = findViewById(R.id.recycler_view)
    recyclerView?.layoutManager = LinearLayoutManager(this)
    recyclerView?.adapter = ExampleAdapter()
}
~~~

Implement the `FastScroller.SectionIndexer` interface in your RecyclerView Adapter and override `getSectionText()`.

~~~kotlin
class ExampleAdapter : RecyclerView.Adapter<ExampleAdapter.ViewHolder>(), FastScroller.SectionIndexer {

    ...

    override fun getSectionText(position: Int): CharSequence {
        return getItem(position).getIndex()
    }
}
~~~

#### FastScrollView:
Add the `FastScrollView` to your xml layout and set your customizations using attributes.

~~~xml
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.l4digital.fastscroll.FastScrollView
        android:id="@+id/fastscroll_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:bubbleColor="#00bb00"
        app:bubbleTextColor="#ffffff"
        app:handleColor="#999999" />

</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
~~~

`FastScrollView` contains a `RecyclerView` and a `FastScroller` that can be accessed with public methods.

~~~kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_example)

    val fastScrollView: FastScrollView? = findViewById(R.id.fastscroll_view)
    fastScrollView?.setLayoutManager(LinearLayoutManager(this))
    fastScrollView?.setAdapter(ExampleAdapter())
}
~~~

Implement the `FastScroller.SectionIndexer` interface in your RecyclerView Adapter and override `getSectionText()`.

~~~kotlin
class ExampleAdapter : RecyclerView.Adapter<ExampleAdapter.ViewHolder>(), FastScroller.SectionIndexer {

    ...

    override fun getSectionText(position: Int): CharSequence {
        return getItem(position).getIndex()
    }
}
~~~

#### Alternative Usage:
If you are unable to use the `FastScrollRecyclerView` or `FastScrollView`, you can add a `FastScroller` to your layout and implement with any `RecyclerView`. See this [github issue](https://github.com/L4Digital/FastScroll/issues/4#issuecomment-256975634) for an example.


## Customization
The following attributes can be set to customize the visibility and appearance of the elements within the FastScroller view:

* `app:hideScrollbar=["true" | "false"]` *Hides the scrollbar when not scrolling*
* `app:showBubble=["true" | "false"]` *Shows the section bubble while scrolling*
* `app:showTrack=["true" | "false"]` *Shows the scroll track while scrolling*
* `app:handleColor=["#rrggbb" | "@color"]` *Sets the color of the scroll handle*
* `app:trackColor=["#rrggbb" | "@color"]` *Sets the color of the scroll track*
* `app:bubbleColor=["#rrggbb" | "@color"]` *Sets the background color of the section bubble*
* `app:bubbleSize=["normal" | "small"]` *Sets the size of the section bubble*
* `app:bubbleTextColor=["#rrggbb" | "@color"]` *Sets the text color of the section bubble*
* `app:bubbleTextSize=["sp" | "@dimen"]` *Sets the scaled pixel text size of the section bubble*


## License
    Copyright 2018 L4 Digital. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

