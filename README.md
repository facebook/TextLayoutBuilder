<img src="/docs/logo.png" width="128" align="right"/>

# TextLayoutBuilder

Build text [Layout](https://developer.android.com/reference/android/text/Layout.html)s easily on Android.

## Features
- Create text `Layout`s easily.
- Reuse builders to create similarly styled `Layout`s.
- Cache `Layout`s of commonly used strings.
- Improve performance by warming up the FreeType cache.

## Download
If using Gradle, add this to your `build.gradle`:

```groovy
compile 'com.facebook.fbui.textlayoutbuilder:textlayoutbuilder:1.6.0'
```

or, if using Maven:

```xml
<dependency>
  <groupId>com.facebook.fbui.textlayoutbuilder</groupId>
  <artifactId>textlayoutbuilder</artifactId>
  <version>1.6.0</version>
  <type>aar</type>
</dependency>
```

## Usage
1. Set the properties on the `TextLayoutBuilder`:
  ```java
  TextLayoutBuilder builder = new TextLayoutBuilder()
      .setText("TextLayoutBuilder makes life easy")
      .setTextColor(Color.BLUE)
      .setWidth(400 /*, MEASURE_MODE_EXACTLY */);
  ```

2. Call `build()` on the builder to get a `Layout`:
  ```java
  Layout layout = builder.build();
  ```

3. Use the `Layout` in your code:
  ```java
  public class CustomView extends View {
      private Layout layout;

      public CustomView(Context context) {
          super(context);
      }

      public void setLayout(Layout layout) {
          this.layout = layout;
      }

      @Override
      protected void onDraw(Canvas canvas) {
          super.onDraw(canvas);

          // Draw the layout.
          layout.draw(canvas);
      }
  }
  ```

## Additional Usage
1. Cache the layouts for commonly used strings by turning on caching in the `TextLayoutBuilder`.
  ```java
  textLayoutBuilder.setShouldCacheLayout(true);
  ```

2. Glyph warming provides significant performance boost for large blurbs of text.
Turn this on and pass in a `GlyphWarmer` for the `TextLayoutBuilder`.
  ```java
  textLayoutBuilder
      .setShouldWarmText(true)
      .setGlyphWarmer(new GlyphWarmerImpl());
  ```

3. Import a style defined in XML into a `TextLayoutBuilder` object.
  ```java
  ResourceTextLayoutHelper.updateFromStyleResource(
      textLayoutBuilder, // builder object
      context,           // Activity context
      resId);            // style resource id
  ```

## License

TextLayoutBuilder is Apache-2-licensed.
