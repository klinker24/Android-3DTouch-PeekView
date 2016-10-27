# Android 3D Touch - PeekView

![preview](preview.gif)

iOS uses [3D Touch](http://www.apple.com/iphone-6s/3d-touch/) as a way to "peek" into full content, such as emails, pictures, web searches, etc. While they have dedicated hardware for this functionality, it is still possible to get similar functionality out of Android, with a long click, rather than the dedicated hardware.

This library aims to create a simple and powerful API to enable 3D Touch style "Peeking" on Android.

## Features

![features](features.gif)

1. Simple API
2. Quick and easy to implement in any app
3. Long clicking a view will overlay the `PeekView` until you stop touching the screen again
4. The `PeekView` is smart. It will be displayed relative to the touch location, not on some random place on the screen. It will also move out of the way of your finger so that it isn't covered up when displayed.
5. Customize the `PeekView`'s window size (fullscreen or percent of screen size)
6. Change the background dim amount
7. Haptic feedback is optional
8. Peek into ANY type of views. Example even uses a `WebView`
9. Blur the background behind the `PeekView` for an extra "WOW" factor over the dim percentage

## Installation

There are two ways to use this library:

#### As a Gradle dependency

This is the preferred way. Simply add:

```groovy
dependencies {
    compile 'com.klinkerapps:peekview:1.2.3'
}
```

to your project dependencies and run `./gradlew build` or `./gradlew assemble`.

#### As a library project

Download the source code and import it as a library project in Eclipse. The project is available in the folder **library**. For more information on how to do this, read [here](http://developer.android.com/tools/projects/index.html#LibraryProjects).

## Example Usage

This library is extremely easy to use. There are two steps:

- Any activities you want to implement the PeekView on, you must use `PeekViewActivity` as the superclass.

`PeekViewActivity` is a descendant of `AppCompatActivity`, so for almost every case, this will just be plug and play, a simple switch. `PeekView` requires this activity to montitor the touch events and determine when the user lifts their finger to remove the `PeekView`.

- Use the `Peek.into(...` builder to start creating your `PeekView` on a UI element. There are examples of this in the [MainActivity](https://github.com/klinker24/Android-3DTouch-PeekView/blob/master/example/src/main/java/com/klinker/android/peekview_example/MainActivity.java), but here is a very simple one that will display an image full screen.

```java
Peek.into(R.layout.image_peek, new SimpleOnPeek() {
    @Override
    public void onInflated(View rootView) {
        // rootView is the layout inflated from R.layout.image_peek
        ((ImageView) rootView.findViewById(R.id.image))
                .setImageDrawable(getResources().getDrawable(R.drawable.klinker_apps));
    }
}).applyTo(this, findViewById(R.id.image_peek));
```

### More advanced usage

Here are a few of the more advanced things that you can do with PeekView

#### PeekViewOptions

This is how you customize your `PeekView`. After creating a `PeekViewOptions` object, simply add it with `Peek.with(options)`

Here is a list of all the possible options, along with the implementation:

```java
PeekViewOptions options = new PeekViewOptions();
options.setBackgroundDim(1f);           // range: 0  - 1  (default is .6)
options.setHapticFeedback(false);       // default is true

// it may be a good idea to set set these through resources so that you can use different options based on screen size and orientation
options.setWidthPercent(.4f);           // range: .1 - .9 (default is .6)
options.setHeightPercent(.4f);          // range: .1 - .9 (default is .5)

// you can also set the size of the PeekView using absolute values, instead of percentages. 
// Setting these will override the corresponding percentage value.
// You should use this instead of setting the size of the view from the layout resources, as those get overridden.
options.setAbsoluteWidth(200);          // 200 DP
options.setAbsoluteHeight(200);         // 200 DP

// default is false. If you change this to true, it will ignore the width and height percentages you set.
options.setFullScreenPeek(true); 
// default is true. Unless you are going to animate things yourself, i recommend leaving this as true.
options.setFadeAnimation(false);

// PeekView has the ability to blur the background behind it, instead of just using a simple dark dim.
// If you set a blurred view, then it will invalidate whatever you set as your background dim.
// If you do this, please look at the installation steps for the blur effect, or the app will crash.
options.setBlurBackground(true);                            // default is true
options.setBlurOverlayColor(Color.parse("#99000000"));      // #99000000 default

Peek.into(...).with(options).applyTo(...);
```

#### Different lifecycle events

`PeekView` has a number of lifecycle events that you can choose to implement. In the above example, I just showed the use of `SimpleOnPeek`, which provides a callback so that you can initialize your layout after it has been inflated. Similar to what you would do after `Activity.setContentView(...)`. 

If you are doing any kind of custom animations, or network calls though, you may need something more powerful. `SimpleOnPeek` is just a wrapper of the `OnPeek` interface, which also contains callbacks for when the `PeekView` is displayed to the user on the screen (helpful if you wanted to do your own animations instead of use the default fade animation), as well as a callback for when the `PeekView` is dismissed from the screen (a good place to stop any networking activities).

Implementing these callbacks is straightforward and almost the same as you have done with the `SimpleOnPeek`: 

```java
Peek.into(..., new OnPeek() {
    @Override
    public void onInflated(View rootView) {
        // the normal inflation callback
    }

    @Override
    public void shown() {
        // the view is shown to the user
        // Could be a nice place for custom animations on the inflated view
    }

    @Override
    public void dismissed() {
        // the view is dismissed from the screen
        // It is destroyed an never reused, so any cleanup would go here
    }
}).applyTo(...);
```

#### Clearing the Peek for a View

Sometimes it may be necessary to not allow "peeking" in a `View` where it was previously allowed. Some situations that come to mind would be in a `RecyclerView` or `ListView` where content could be different. Because of the way these lists work, when the view gets recycled, it will retain the "peeking" ability of the original `View`, unless you clear it. Clearing a "peek" is really easy:

```java
Peek.clear(View);
```

Currently, all this method does is set the `TouchListener` to `null` for the provided `View`. In the future though, there may be more advanced changes that will be made to this method, so, it is probably safer to use the `Peek#clear` method instead of just `null`ing the `TouchListener` yourself.

## Contributing

Please fork this repository and contribute back using [pull requests](https://github.com/klinker24/Android-3DTouch-PeekView/pulls). Features can be requested using [issues](https://github.com/klinker24/Android-3DTouch-PeekView/issues). All code, comments, and critiques are greatly appreciated.

## Changelog

The full changelog for the library can be found [here](https://github.com/klinker24/Android-3DTouch-PeekView/blob/master/changelog.md).


## License

    Copyright 2016 Luke Klinker

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
