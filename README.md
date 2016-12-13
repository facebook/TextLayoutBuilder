TextLayoutBuilder
=================
Build text [Layout](https://developer.android.com/reference/android/text/Layout.html)s easily on Android.

![TextLayoutBuilder logo](https://facebookincubator.github.io/TextLayoutBuilder/logo.png)

Features
--------
- Allows creating text `Layout`s easily.
- Re-use the same builder to create `Layout`s of similar style.
- Cache `Layout`s of commonly used strings.
- Glyph warming to improve performance.

Usage
-----
1. Add the dependency to your `build.gradle`:
  ```groovy
  compile 'com.facebook.fbui.textlayoutbuilder:textlayoutbuilder:1.0.0'
  ```

2. Set the properties on the `TextLayoutBuilder`:
  ```java
  TextLayoutBuilder builder = new TextLayoutBuilder()
      .setTextAppearance(context, resId)
      .setText("TextLayoutBuilder makes life easy")
      .setWidth(400);
  ```

3. Call `build()` on the builder to get a `Layout`:
  ```java
  Layout layout = builder.build();
  ```

4. Use the `Layout` in your code:
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

Download
--------
Grab via Gradle:

```groovy
compile 'com.facebook.fbui.textlayoutbuilder:textlayoutbuilder:1.0.0'
```

or Maven:

```xml
<dependency>
  <groupId>com.facebook.fbui.textlayoutbuilder</groupId>
  <artifactId>textlayoutbuilder</artifactId>
  <version>1.0.0</version>
</dependency>
```
