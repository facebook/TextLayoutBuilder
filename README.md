TextLayoutBuilder
=================
Build text [Layout](https://developer.android.com/reference/android/text/Layout.html)s easily on Android.

![TextLayoutBuilder logo](./docs/logo.png)

Features
--------
- Create text `Layout`s easily.
- Reuse builders to create similarly styled `Layout`s.
- Cache `Layout`s of commonly used strings.
- Improve performance using glyph warming.

Download
--------
If using Gradle, add this to your `build.gradle`:

```groovy
compile 'com.facebook.fbui.textlayoutbuilder:textlayoutbuilder:1.0.0'
```

or, if using Maven:

```xml
<dependency>
  <groupId>com.facebook.fbui.textlayoutbuilder</groupId>
  <artifactId>textlayoutbuilder</artifactId>
  <version>1.0.0</version>
  <type>aar</type>
</dependency>
```

Usage
-----
1. Set the properties on the `TextLayoutBuilder`:
  ```java
  TextLayoutBuilder builder = new TextLayoutBuilder()
      .setTextAppearance(context, resId)
      .setText("TextLayoutBuilder makes life easy")
      .setWidth(400);
  ```

2. Call `build()` on the builder to get a `Layout`:
  ```java
  Layout layout = builder.build();
  ```

3. Use the `Layout` in your code:
  ```java
  public class CustomView extends View {
      private Layout mLayout;

      public CustomView(Context context, AttributeSet attrs) {
          super(context, attrs);
      }

      public void setLayout(Layout layout) {
          mLayout = layout;
      }

      @Override
      protected void onDraw(Canvas canvas) {
          super.draw(canvas);

          // Draw the layout.
          mLayout.draw(canvas);
      }
  }
  ```

Additional Usage
----------------
1. Cache the layouts for commonly used strings by turning on caching in the `TextLayoutBuilder`.
  ```java
  mTextLayoutBuilder.setShouldCacheLayout(true);
  ```

2. Glyph warming provides significant performance boost for large blurbs of text.
Turn this on and pass in a `GlyphWarmer` for the `TextLayoutBuilder`.
  ```java
  mTextLayoutBuilder
      .setShouldWarmText(true)
      .setGlyphWarmer(new GlyphWarmerImpl());
  ```

3. Import a style defined in XML into a `TextLayoutBuilder` object.
  ```java
  ResourceTextLayoutHelper.updateFromStyleResource(
      mTextLayoutBuilder, // builder object
      mContext,           // Activity context
      resId);             // style resource id
  ```
