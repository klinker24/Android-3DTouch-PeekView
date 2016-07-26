## Changelog

### Version 1.2.0:
- Blur the background behind `PeekView` with `PeekViewOptions#setBlurredView`, instead of just using a dark dim

### Version 1.1.1:
- Improve long and short click gesture detection with `PeekView`

### Version 1.1.0:
- Initial public release!

### Version 1.0.8 BETA:
- Some bug fixes

### Version 1.0.6 BETA:
- Make `PeekView` smarter. Will react better to finger placement and move out of the way, so that it is not covered up when displayed.
- Ensure correct the `PeekView` is always dismissed when `Activity#onPause` is called.

### Version 1.0.5 BETA:
- Set the absolute width and height of a view instead of a screen size percentage

### Version 1.0.4 BETA:
- Allow for clearing the "peek" ability on a `View`

### Version 1.0.3 BETA:
- Show ripple touch effects on the clicked views, if they use a RippleDrawable for a background

### Version 1.0.2 BETA:
- Match `AppCompatActivity`'s protectected visibility for onCreate()

### Version 1.0.1 BETA:
- Fix issue with the `PeekView` consuming click events

### Version 1.0.0 BETA:
- Initial BETA Release