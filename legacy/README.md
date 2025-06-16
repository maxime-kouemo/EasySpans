# EasySpans

The `EasySpans` library provides a flexible and chainable API for applying Android text spans to `TextView` widgets. It supports global and localized span application, including custom spans, regex-based targeting, delimiter-based targeting, and clickable links. This documentation includes examples for common use cases, as well as examples adapted from the `EasySpansCompose` library, reimplemented for the legacy `EasySpans` API. The content is informed by the provided unit tests for robustness and clarity.

## Setup
Add to your `build.gradle`:
```kotlin
dependencies {
    implementation(project(":legacy")) // see the repo's app's settings.gradle for the moment, the library version is coming soon
}
```

## Overview

`EasySpans` simplifies applying multiple text spans to a `TextView` using a builder pattern. It allows chaining span configurations to apply them globally (to the entire text) or to specific portions (via regex, delimiters, or occurrence positions). The library supports standard Android spans (`ForegroundColorSpan`, `UnderlineSpan`, etc.) and custom spans (`PaddingBackgroundColorSpan`, `TextCaseSpan`, `ClickableLinkSpan`).

Key features:
- **Global Spans**: Apply spans to the entire text.
- **Localized Spans**: Target specific text portions using regex or delimiter-based occurrence locations.
- **Clickable Links**: Support clickable text with custom click listeners via `ClickableLinkSpan`.
- **Custom Spans**: Includes `PaddingBackgroundColorSpan` for paragraph backgrounds, `TextCaseSpan` for case transformations, and more.
- **Occurrence Targeting**: Apply spans to the first, last, nth, specific indices, or all occurrences of a pattern or delimiter-separated text.
- **Case-Insensitive Regex**: Supports case-insensitive matching with `(?i)` prefix.
- **Robust Error Handling**: Gracefully handles invalid inputs like malformed regex, empty text, or invalid resource IDs.

## Usage

The `EasySpans` API centers around the `EasySpans.Builder` class, which takes a `Context`, the input text, and a `TextView` as parameters. The builder allows chaining methods to configure spans, followed by `build()` and `create()` to generate a `Spanned` object.

### Basic Example
Apply bold text, a teal color, and an underline to the entire text:

```kotlin
val spanned = EasySpans.Builder(context, text, textView)
    .setTextStyle(Typeface.BOLD)
    .setColor(R.color.teal_700)
    .isUnderlined()
    .build()
    .create() as Spanned

textView.text = spanned
```

### Targeting Specific Text
Apply an underline to the third word (delimited by spaces):

```kotlin
val spanned = EasySpans.Builder(context, text, textView)
    .setOccurrenceLocation(
        OccurrenceLocation(
            DelimitationType.BOUNDARY(" "),
            OccurrencePosition.Nth(2)
        )
    )
    .isUnderlined()
    .build()
    .create() as Spanned
```

### Clickable Link Example
Make the first occurrence of "ipsum" clickable with a purple color:

```kotlin
val spanned = EasySpans.Builder(context, text, textView)
    .setOccurrenceChunks(
        OccurrenceChunk(
            location = OccurrenceLocation(
                DelimitationType.REGEX("ipsum"),
                OccurrencePosition.First
            ),
            builder = OccurrenceChunkBuilder()
                .setColor(R.color.purple_500)
                .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                    override fun onLinkClick(view: View) {
                        // Handle click
                    }
                })
        )
    )
    .build()
    .create() as Spanned
```

## API Details

### Builder Methods
The `EasySpans.Builder` class provides the following methods for configuring spans:

- **Global Spans**:
  - `setColor(@ColorRes colorRes: Int)`: Applies a `ForegroundColorSpan`.
  - `setTextSize(@DimenRes textSizeRes: Int)`: Applies an `AbsoluteSizeSpan`.
  - `setTextStyle(typeface: Int)`: Applies a `StyleSpan` (e.g., `Typeface.BOLD`, `Typeface.ITALIC`).
  - `isUnderlined()`: Applies an `UnderlineSpan`.
  - `isStrikeThrough()`: Applies a `StrikethroughSpan`.
  - `setFont(@FontRes fontRes: Int)`: Applies a `TypefaceSpan` (API 28+).
  - `setTextCaseType(type: TextCaseSpan.TextCaseType)`: Applies a `TextCaseSpan` for case transformation (e.g., `UPPER_CASE`).
  - `setParagraphBackgroundColor(bg: SequenceBackgroundColor)`: Applies a `PaddingBackgroundColorSpan` with padding and gravity.
  - `setStyle(@StyleRes styleRes: Int)`: Applies a `TextAppearanceSpan`.
  - `setScriptType(type: ScriptType)`: Applies `SubscriptSpan` or `SuperscriptSpan` (e.g., `ScriptType.SUB`).

- **Localized Spans**:
  - `setOccurrenceLocation(location: OccurrenceLocation)`: Targets specific text portions for spans based on `DelimitationType` (`BOUNDARY` or `REGEX`) and `OccurrencePosition` (`First`, `Last`, `Nth`, `Indices`, `All`).
  - `setOccurrenceChunks(vararg chunks: OccurrenceChunk)`: Applies spans to specific text portions with custom configurations, including clickable links.

### OccurrenceChunkBuilder
The `OccurrenceChunkBuilder` class configures spans for specific text portions within an `OccurrenceChunk`. It supports all global span methods plus:

- `setOnLinkClickListener(listener: ClickableLinkSpan.OnLinkClickListener)`: Makes the chunk clickable with a `ClickableLinkSpan`.
- `setChunkBackgroundColor(@ColorRes colorRes: Int)`: Applies a `BackgroundColorSpan` to the chunk.

### DelimitationType
- `BOUNDARY(delimiter: String)`: Splits text by a delimiter (e.g., spaces, commas).
- `REGEX(pattern: String)`: Matches text using a regex pattern. Supports case-insensitive matching with `(?i)` prefix.

### OccurrencePosition
- `First`: Targets the first occurrence.
- `Last`: Targets the last occurrence.
- `Nth(n: Int)`: Targets the nth occurrence (0-based).
- `Indices(vararg indices: Int)`: Targets specific occurrence indices.
- `All`: Targets all occurrences.

### OccurrenceLocation
`OccurrenceLocation` defines how to target specific text portions for span application by combining `DelimitationType` and `OccurrencePosition`. This allows precise control over which parts of the text receive styling or annotations.

#### DelimitationType
Specifies how text should be delimited for span operations:

- **REGEX**: Matches text using a regular expression; spans are applied to each regex match.
  - **Example**: For `val sentence = "The cat stole the bacon, but the cat returned."` and `val value = "cat"`, a regex pattern `"cat"` identifies matches at positions 4-6 (`"cat"` in `"The cat"`) and 25-27 (`"cat"` in `"the cat"`). Styles are applied to these matches based on the specified `OccurrencePosition`.
  - **OccurrencePosition for REGEX**:
    - `First`: The first regex match.
      - **Example**: For `"The cat stole the bacon, but the cat returned."` with regex `"cat"`, styles `"cat"` at positions 4-6.
    - `Nth(n: Int)`: The nth regex match (0-based index).
      - **Example**: `Nth(1)` with regex `"cat"` styles `"cat"` at positions 25-27.
    - `Indices(indices: List)`: Specific indexed regex matches (0-based).
      - **Example**: `Indices(0, 1)` with regex `"cat"` styles `"cat"` at 4-6 and 25-27.
    - `All`: All regex matches.
      - **Example**: With regex `"cat"`, styles both `"cat"` instances at 4-6 and 25-27.
    - `Last`: The last regex match.
      - **Example**: With regex `"cat"`, styles `"cat"` at 25-27.

- **BOUNDARY**: Splits text by a boundary string; spans are applied to the segments between delimiters (not the delimiters themselves).
  - **Example**: Using the same sentence with a boundary `" "`, splits into segments: `"The"`, `"cat"`, `"stole"`, `"the"`, `"bacon,"`, `"but"`, `"the"`, `"cat"`, `"returned."`. Styles are applied to these segments based on the specified `OccurrencePosition`.
  - **OccurrencePosition for BOUNDARY**:
    - `First`: The first segment.
      - **Example**: For `"The cat stole the bacon, but the cat returned."` with boundary `" "`, styles `"The"`.
    - `Nth(n: Int)`: The nth segment (0-based index).
      - **Example**: `Nth(1)` with boundary `" "` styles `"cat"`.
    - `Indices(indices: List)`: Specific indexed segments (0-based).
      - **Example**: `Indices(0, 2)` with boundary `" "` styles `"The"` and `"stole"`.
    - `All`: All segments.
      - **Example**: With boundary `" "`, styles `"The"`, `"cat"`, `"stole"`, `"the"`, `"bacon,"`, `"but"`, `"the"`, `"cat"`, `"returned."`.
    - `Last`: The last segment.
      - **Example**: With boundary `" "`, styles `"returned."`.

This combination allows precise control over which parts of the text receive styling or annotations.

### Custom Spans
- `PaddingBackgroundColorSpan`: Applies a background color with padding and gravity to a paragraph.
- `TextCaseSpan`: Transforms text case (e.g., to uppercase) without altering the underlying string.
- `ClickableLinkSpan`: A clickable span with customizable color and underline, overriding default link styling.

## Error Handling
- **Malformed Regex**: Throws `PatternSyntaxException` for invalid regex patterns, resulting in no spans applied.
- **Empty Text**: Returns an empty `Spanned` with no spans.
- **Invalid Resource IDs**: Skips invalid resources or throws `Resources.NotFoundException`.
- **Invalid Occurrence Indices**: Ignores invalid indices (e.g., negative or out-of-bounds) and applies spans only to valid indices.
- **Empty Regex or Delimiter**: No spans are applied for empty regex or delimiter.
- **Concurrent Access**: Thread-safe for span creation and click handling.

## Testing Insights
The `EasySpans` library is validated through extensive unit tests, ensuring robust functionality across various use cases. These tests confirm correct span application for global and localized styling, proper handling of regex and delimiter-based targeting, and reliable clickable link behavior. Edge cases, such as empty inputs, invalid regex patterns, and concurrent access, are rigorously tested to guarantee stability and performance in real-world applications.

## Limitations
- **API Level Dependency**: `TypefaceSpan` with custom fonts requires API 28+.
- **TextCaseSpan Behavior**: adds no span at all. Only the text case (upperCase, lowerCase, Capitalize, etc.) is applied to the chunk of the text, affecting span counts.
- **TextView Dependency**: Requires a non-null `TextView` for some operations (setParagraphBackground, and ClickableLinkSpan).
- **No Formatted String Support**: Does not handle string resource formatting (e.g., `%s`).

## Best Practices
- Validate regex patterns and resource IDs.
- Use `(?i)` for case-insensitive regex.
- Optimize for large texts by limiting chunks or regex complexity.
- Test concurrent scenarios for click handlers.
- Preserve `TextView` state when reapplying spans.

## Example Scenarios

### 1. Global Styling
Apply uniform styles to the entire text, including color, size, font style, and text case:

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor", textView)
    .setColor(R.color.teal_700)
    .setTextSize(R.dimen.test_default_text_size)
    .setTextStyle(Typeface.BOLD_ITALIC)
    .setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE)
    .build()
    .create() as Spanned

textView.text = spanned
```

### 2. Styling a Specific Occurrence
Style the third word in the text, split by spaces, with an underline:

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor sit amet", textView)
    .setOccurrenceLocation(
        OccurrenceLocation(
            DelimitationType.BOUNDARY(" "),
            OccurrencePosition.Nth(2)
        )
    )
    .isUnderlined()
    .build()
    .create() as Spanned

textView.text = spanned
```

### 3. Clickable Link with Regex
Make the last occurrence of "nec" in a text clickable and styled with a specific color:

```kotlin
val spanned = EasySpans.Builder(context, text, textView)
    .setOccurrenceChunks(
        OccurrenceChunk(
            location = OccurrenceLocation(
                DelimitationType.REGEX("nec"),
                OccurrencePosition.Last
            ),
            builder = OccurrenceChunkBuilder()
                .setColor(R.color.purple_500)
                .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                    override fun onLinkClick(view: View) {
                        // Handle click
                    }
                })
        )
    )
    .build()
    .create() as Spanned

textView.text = spanned
```

### 4. Styling All Regex Matches
Apply underline, strikethrough, and uppercase transformation to all occurrences of "nec":

```kotlin
val spanned = EasySpans.Builder(context, text, textView)
    .setOccurrenceLocation(
        OccurrenceLocation(
            DelimitationType.REGEX("nec"),
            OccurrencePosition.All
        )
    )
    .isUnderlined()
    .isStrikeThrough()
    .setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE)
    .build()
    .create() as Spanned

textView.text = spanned
```

### 5. Multiple Occurrence Chunks with Different Styles
Apply different styles and clickable links to three distinct text segments: the first "nec" (regex), the second word (delimiter), and all "tempus" occurrences (regex):

```kotlin
val spanned = EasySpans.Builder(context, text, textView)
    .setOccurrenceChunks(
        OccurrenceChunk(
            location = OccurrenceLocation(
                DelimitationType.REGEX("nec"),
                OccurrencePosition.First
            ),
            builder = OccurrenceChunkBuilder()
                .setColor(R.color.teal_700)
                .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                    override fun onLinkClick(view: View) {
                        // Handle click
                    }
                })
        ),
        OccurrenceChunk(
            location = OccurrenceLocation(
                DelimitationType.BOUNDARY(" "),
                OccurrencePosition.Nth(1)
            ),
            builder = OccurrenceChunkBuilder()
                .setTextStyle(Typeface.BOLD)
                .setTextSize(R.dimen.test_default_text_size)
        ),
        OccurrenceChunk(
            location = OccurrenceLocation(
                DelimitationType.REGEX("tempus"),
                OccurrencePosition.All
            ),
            builder = OccurrenceChunkBuilder()
                .setColor(R.color.purple_500)
                .isUnderlined()
        )
    )
    .build()
    .create() as Spanned

textView.text = spanned
```

### 6. Highlight Monetary Values
Highlight all monetary values (e.g., `5 $`, `$10.50`):

```kotlin
val spanned = EasySpans.Builder(context, "Price: $10.50, 5 $", textView)
    .setOccurrenceChunks(
        OccurrenceChunk(
            location = OccurrenceLocation(
                DelimitationType.REGEX("(\\d+\\s?\\$)|(\\$\\d+(\\.\\d{1,2})?)|(\\d+\\.\\d{1,2}\\s?\\$)"),
                OccurrencePosition.All
            ),
            builder = OccurrenceChunkBuilder().setColor(R.color.teal_700)
        )
    )
    .build()
    .create() as Spanned

textView.text = spanned
```

### 7. Create a Clickable Glossary
Make all occurrences of "consectetur" and "Donec" clickable:

```kotlin
val spanned = EasySpans.Builder(context, text, textView)
    .setOccurrenceChunks(
        OccurrenceChunk(
            location = OccurrenceLocation(DelimitationType.REGEX("consectetur")),
            builder = OccurrenceChunkBuilder()
                .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                    override fun onLinkClick(view: View) {
                        // Handle click
                    }
                })
        ),
        OccurrenceChunk(
            location = OccurrenceLocation(DelimitationType.REGEX("Donec")),
            builder = OccurrenceChunkBuilder()
                .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                    override fun onLinkClick(view: View) {
                        // Handle click
                    }
                })
        )
    )
    .build()
    .create() as Spanned

textView.text = spanned
```

### 8. Style a Specific Word
Apply bold and a larger size to the second occurrence of "ipsum":

```kotlin
val spanned = EasySpans.Builder(context, text, textView)
    .setOccurrenceLocation(
        OccurrenceLocation(DelimitationType.REGEX("ipsum"), OccurrencePosition.Nth(1))
    )
    .setTextStyle(Typeface.BOLD)
    .setTextSize(R.dimen.test_default_text_size)
    .build()
    .create() as Spanned

textView.text = spanned
```

### 9. Bold and Color a Specific Word
Apply bold style and purple color to the word "ipsum":

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor", textView)
    .setOccurrenceLocation(
        OccurrenceLocation(DelimitationType.REGEX("ipsum"), OccurrencePosition.First)
    )
    .setTextStyle(Typeface.BOLD)
    .setColor(R.color.purple_500)
    .build()
    .create() as Spanded

textView.text = spanned
```

### 10. Underline and Color Multiple Words
Underline and color all occurrences of "ipsum" and "dolor" in teal:

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor ipsum", textView)
    .setOccurrenceChunks(
        OccurrenceChunk(
            location = OccurrenceLocation(DelimitationType.REGEX("ipsum"), OccurrencePosition.All),
            builder = OccurrenceChunkBuilder()
                .setColor(R.color.teal_700)
                .isUnderlined()
        ),
        OccurrenceChunk(
            location = OccurrenceLocation(DelimitationType.REGEX("dolor"), OccurrencePosition.All),
            builder = OccurrenceChunkBuilder()
                .setColor(R.color.teal_700)
                .isUnderlined()
        )
    )
    .build()
    .create() as Spanded

textView.text = spanned
```

### 11. Make Words Clickable
Make the first occurrence of "ipsum" clickable with a purple color:

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor", textView)
    .setOccurrenceChunks(
        OccurrenceChunk(
            location = OccurrenceLocation(DelimitationType.REGEX("ipsum"), OccurrencePosition.First),
            builder = OccurrenceChunkBuilder()
                .setColor(R.color.purple_500)
                .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                    override fun onLinkClick(view: View) {
                        // Handle click
                    }
                })
        )
    )
    .build()
    .create() as Spanded

textView.text = spanned
```

### 12. Combine Multiple Styles
Apply bold, underline, teal color, and larger size to the first occurrence of "dolor":

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor", textView)
    .setOccurrenceLocation(
        OccurrenceLocation(DelimitationType.REGEX("dolor"), OccurrencePosition.First)
    )
    .setTextStyle(Typeface.BOLD)
    .isUnderlined()
    .setColor(R.color.teal_700)
    .setTextSize(R.dimen.test_default_text_size)
    .build()
    .create() as Spanded

textView.text = spanned
```

### 13. Case-Insensitive Matching
Underline the first occurrence of "lorem" (case-insensitive):

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor", textView)
    .setOccurrenceLocation(
        OccurrenceLocation(DelimitationType.REGEX("(?i)lorem"), OccurrencePosition.First)
    )
    .isUnderlined()
    .build()
    .create() as Spanded

textView.text = spanned
```

### 14. Highlight Email Addresses
Color all email addresses in a text purple:

```kotlin
val spanned = EasySpans.Builder(context, "Contact: user1@domain.com, user2@sub.domain.org", textView)
    .setOccurrenceChunks(
        OccurrenceChunk(
            location = OccurrenceLocation(
                DelimitationType.REGEX("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"),
                OccurrencePosition.All
            ),
            builder = OccurrenceChunkBuilder().setColor(R.color.purple_500)
        )
    )
    .build()
    .create() as Spanded

textView.text = spanned
```

### 15. Uppercase Transformation
Transform the first occurrence of "ipsum" to uppercase and make it bold:

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor", textView)
    .setOccurrenceLocation(
        OccurrenceLocation(DelimitationType.REGEX("ipsum"), OccurrencePosition.First)
    )
    .setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE)
    .setTextStyle(Typeface.BOLD)
    .build()
    .create() as Spanded

textView.text = spanned
```

### 16. Paragraph Background
Apply a purple background with padding to the entire text:

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor", textView)
    .setParagraphBackgroundColor(
        SequenceBackgroundColor(
            backgroundColor = R.color.purple_700,
            padding = R.dimen.test_background_padding,
            gravity = Gravity.CENTER
        )
    )
    .build()
    .create() as Spanded

textView.text = spanned
```

### 17. Subscript Text
Apply subscript to the first occurrence of "dolor":

```kotlin
val spanned = EasySpans.Builder(context, "Lorem ipsum dolor", textView)
    .setOccurrenceLocation(
        OccurrenceLocation(DelimitationType.REGEX("dolor"), OccurrencePosition.First)
    )
    .setScriptType(ScriptType.SUB)
    .build()
    .create() as Spanded

textView.text = spanned
```

For issues, feature requests, or contributions, refer to the project repository or contact the maintainers.

## License
```
   Copyright 2025 mamboa

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```