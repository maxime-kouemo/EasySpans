# EasySpansCompose

`EasySpansCompose` is a Jetpack Compose library that simplifies the creation of styled and annotated text, inspired by the original `EasySpans` library. It provides a fluent builder API to apply global and occurrence-specific styles to text, supporting delimiters (boundary strings or regex), clickable annotations, text transformations (e.g., uppercase), and advanced styling options like superscript/subscript and background colors.

## Setup
Add to your `build.gradle`:
```kotlin
dependencies {
  implementation(project(":compose")) // see the repo's app's settings.gradle for the moment, the library version is coming soon
}
```

## Features

- **Global Styling**: Apply styles such as color, background color, font size, weight, style, family, text decoration, and baseline shift (superscript/subscript) to the entire text.
- **Occurrence-Based Styling**: Style specific text segments based on position (`First`, `Last`, `Nth`, `Indices`, or `All`) and delimitation (boundary string or regex).
- **Flexible Chunk Configuration**: Add multiple occurrence chunks with `addOccurrenceChunk` or set a list of chunks with `setOccurrenceChunks` for precise control over styled segments.
- **Clickable Annotations**: Add clickable regions with custom tags for handling user interactions.
- **Text Transformation**: Apply global text transformations (e.g., uppercase, lowercase) before styling, or per-chunk transformations (e.g., uppercase specific matches).
- **Superscript/Subscript Support**: Use `ScriptType` to apply superscript or subscript styles via baseline shift.
- **Background Color**: Set background colors for the entire text or specific segments.
- **Robust Delimitation**: Use boundary strings or regex patterns to identify text segments, with proper handling of non-existent delimiters (no styling applied).
- **Immutable and Stable**: Uses `@Immutable` and `@Stable` annotations for Compose optimization.
- **DSL-Like API**: Fluent builder and `occurrenceChunk` DSL for readable configuration.

## Understanding OccurrenceLocation

`OccurrenceLocation` pinpoints the text segments to apply spans to by combining a `Delimiter` (how to split or match text) and an `OccurrencePosition` (which segments to style after processing). Here’s how each component works:

### Delimiter
Specifies how text should be delimited for span operations:

- **REGEX**: Matches text using a regular expression; spans are applied to each regex match.
  - **Example**: For `val sentence = "The cat stole the bacon, but the cat returned."` and `val value = "cat"`, a regex pattern `"cat"` identifies matches at positions 4-6 ("cat" in "The cat") and 25-27 ("cat" in "the cat"). Styles are applied to these matches based on the specified `OccurrencePosition`.
  - **OccurrencePosition for REGEX**:
    - **First**: The first regex match.
      - Example: For `"The cat stole the bacon, but the cat returned."` with regex `"cat"`, styles "cat" at positions 4-6.
    - **Nth(n: Int)**: The nth regex match (0-based index).
      - Example: `Nth(1)` with regex `"cat"` styles "cat" at positions 25-27.
    - **Indices(indices: List<Int>)**: Specific indexed regex matches (0-based).
      - Example: `Indices(0, 1)` with regex `"cat"` styles "cat" at 4-6 and 25-27.
    - **All**: All regex matches.
      - Example: With regex `"cat"`, styles both "cat" instances at 4-6 and 25-27.
    - **Last**: The last regex match.
      - Example: With regex `"cat"`, styles "cat" at 25-27.
- **BOUNDARY**: Splits text by a boundary string; spans are applied to the segments between delimiters (not the delimiters themselves).
  - **Example**: Using the same sentence with a boundary `" "`, splits into segments: "The", "cat", "stole", "the", "bacon,", "but", "the", "cat", "returned.". Styles are applied to these segments based on the specified `OccurrencePosition`.
  - **OccurrencePosition for BOUNDARY**:
    - **First**: The first segment.
      - Example: For `"The cat stole the bacon, but the cat returned."` with boundary `" "`, styles "The".
    - **Nth(n: Int)**: The nth segment (0-based index).
      - Example: `Nth(1)` with boundary `" "` styles "cat".
    - **Indices(indices: List<Int>)**: Specific indexed segments (0-based).
      - Example: `Indices(0, 2)` with boundary `" "` styles "The" and "stole".
    - **All**: All segments.
      - Example: With boundary `" "`, styles "The", "cat", "stole", "the", "bacon,", "but", "the", "cat", "returned.".
    - **Last**: The last segment.
      - Example: With boundary `" "`, styles "returned.".

This combination allows precise control over which parts of the text receive styling or annotations.

## Usage Examples

Below are examples demonstrating how to use `EasySpansCompose` in Jetpack Compose. Images of the expected output are included as placeholders; replace the URLs with actual image paths in your repository.

### Example 1: Global Styling with Background and Subscript
Apply a global style with a background color and subscript for a chemical formula.

```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.mamboa.easyspans.compose.*

@Composable
fun ChemicalFormula() {
  val formula = "H₂SO₄" // Sulfuric acid
  val annotatedString = EasySpansCompose("Sulfuric acid H₂SO₄") {
    setColor(Color.White)
    setBackgroundColor(Color.DarkGray)
    setFontSize(16.sp)
    setScriptType(ScriptType.SUB)
    setOccurrenceLocation(
      OccurrenceLocation(
        delimitationType = DelimitationType.Regex(formula),
        occurrencePosition = OccurrencePosition.All
      )
    )
  }
  Text(text = annotatedString)
}
```

**Output**: The text "H₂SO₄" is white with a dark gray background, 16sp font size, and subscripted.

![Chemical Formula Output](./demo_files/chemical_formula.png)
*Image Description*: The text "H₂SO₄" appears in white on a dark gray background, with all characters in a 16sp font size and subscripted, resembling a chemical formula.

### Example 2: Styling the First Occurrence with setOccurrenceChunks
Style the first word in a space-separated text using `setOccurrenceChunks`.

```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import com.mamboa.easyspans.compose.*

@Composable
fun FirstWordStyled() {
    val annotatedString = EasySpansCompose("Hello World! Hello Compose!") {
        setColor(Color.Black)
        addOccurrenceChunk(
            occurrenceChunk(
                occurrenceLocation = OccurrenceLocation(
                    delimitationType = DelimitationType.Boundary(" "),
                    occurrencePosition = OccurrencePosition.First
                ),
                styleBuilder = {
                    it.copy(
                        color = Color.Red,
                        textDecoration = TextDecoration.Underline
                    )
                }
            )
        )
    }
    Text(text = annotatedString)
}
```

**Output**: The first "Hello" is red and underlined; the rest is black.

![First Word Styled Output](https://example.com/first_word_styled.png)
*Image Description*: The text "Hello World! Hello Compose!" is displayed, with "Hello" in red and underlined, and the remaining text in black.

### Example 3: Clickable Text with Multiple addOccurrenceChunk and Uppercase Transformation
Make specific words clickable using regex, adding multiple occurrence chunks to style, annotate, and transform matches (e.g., uppercase).

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import com.mamboa.easyspans.compose.*

@Composable
fun ClickableWords() {
    val annotatedString = EasySpansCompose("Click here, there, or now!") {
        setColor(Color.Black)
        setOccurrenceChunks(
            occurrenceChunk(
                occurrenceLocation = OccurrenceLocation(
                    delimitationType = DelimitationType.Regex("\\bhere\\b"),
                    occurrencePosition = OccurrencePosition.All
                ),
                onClickTag = "click_here",
                styleBuilder = {
                    it.copy(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
                }
            ),
            occurrenceChunk(
                occurrenceLocation = OccurrenceLocation(
                    delimitationType = DelimitationType.Regex("\\bthere\\b"),
                    occurrencePosition = OccurrencePosition.All
                ),
                onClickTag = "click_there",
                styleBuilder = {
                    it.copy(
                        color = Color.Green,
                        textDecoration = TextDecoration.Underline
                    )
                },
                textTransform = { it.uppercase() }
            ),
            occurrenceChunk(
                occurrenceLocation = OccurrenceLocation(
                    delimitationType = DelimitationType.Regex("\\bnow\\b"),
                    occurrencePosition = OccurrencePosition.All
                ),
                onClickTag = "click_now",
                styleBuilder = {
                    it.copy(
                        color = Color.Red,
                        textDecoration = TextDecoration.Underline
                    )
                }
            )
        )
    }

    EasySpansClickableText(
        annotatedString = annotatedString,
        onAnnotationClick = { tag, item ->
            when (tag) {
                "click_here" -> println("Clicked on: $item (tag: $tag)")
                "click_there" -> println("Clicked on: $item (tag: $tag)")
                "click_now" -> println("Clicked on: $item (tag: $tag)")
                else -> println("Unknown click: $item (tag: $tag)")
            }
        },
        style = MaterialTheme.typography.bodyLarge,
    )
}
```

**Output**: "here" is blue and clickable, "THERE" is green, uppercase, and clickable, "now" is red and clickable, all underlined. Clicking each word prints its tag and item (e.g., "Clicked on: THERE (tag: click_there)").

![Clickable Words Output](https://example.com/clickable_words.png)
*Image Description*: The text "Click here, THERE, or now!" is displayed, with "here" in blue and underlined, "THERE" in green, uppercase, and underlined, "now" in red and underlined, and the rest in black. Clicking "here", "THERE", or "now" triggers a console log with the respective tag and item.

### Example 4: Text Case Transformation with Nth Occurrence
Transform text to uppercase and style the second occurrence of a space-separated segment.

```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mamboa.easyspans.compose.*

@Composable
fun UppercaseNthWord() {
  val annotatedString = EasySpansCompose("hello world! hello compose!") {
    setTextCase { it.uppercase() }
    setColor(Color.Black)
    addOccurrenceChunk(
      occurrenceChunk(
        occurrenceLocation = OccurrenceLocation(
          delimitationType = DelimitationType.Boundary(" "),
          occurrencePosition = OccurrencePosition.Nth(1)
        ),
        styleBuilder = { it.copy(color = Color.Magenta, background = Color.Yellow) }
      )
    )
  }
  Text(text = annotatedString)
}
```

**Output**: The text is uppercase ("HELLO WORLD! HELLO COMPOSE!"), with "WORLD!" in magenta on a yellow background.

![Uppercase Nth Word Output](https://example.com/uppercase_nth_word.png)
*Image Description*: The text "HELLO WORLD! HELLO COMPOSE!" is displayed in uppercase, with "WORLD!" in magenta on a yellow background and the rest in black.

### Example 5: Styling Multiple Segments with Mixed Delimiters
Style multiple text segments using a combination of `BOUNDARY` and `REGEX` delimiters with multiple `addOccurrenceChunk` calls.

```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import com.mamboa.easyspans.compose.*

@Composable
fun MixedDelimiterStyles() {
    val annotatedString = EasySpansCompose("The cat runs, the dog jumps.") {
        setColor(Color.Black)
        setOccurrenceChunks(
            occurrenceChunk(
                occurrenceLocation = OccurrenceLocation(
                    delimitationType = DelimitationType.Boundary(" "),
                    occurrencePosition = OccurrencePosition.Indices(0, 4)
                ),
                styleBuilder = {
                    it.copy(
                        color = Color.Red,
                        textDecoration = TextDecoration.Underline
                    )
                }
            ),

            occurrenceChunk(
                occurrenceLocation = OccurrenceLocation(
                    delimitationType = DelimitationType.Regex("(cat|dog)"),
                    occurrencePosition = OccurrencePosition.All
                ),
                styleBuilder = { it.copy(background = Color.Cyan) }
            )
        )
    }
    Text(text = annotatedString)
}
```

**Output**: The words "The" and "the" (indices 0 and 4 with boundary " ") are red and underlined, while "cat" and "dog" (regex matches) have a cyan background, with overlapping styles applied where relevant.

![Mixed Delimiter Styles Output](https://example.com/mixed_delimiter_styles.png)
*Image Description*: The text "The cat runs, the dog jumps." is displayed, with "The" and "the" in red and underlined, "cat" and "dog" on a cyan background, and the rest in black. Where styles overlap (e.g., "cat"), both red/underline and cyan background are visible.

### Example 6: Styling Monetary Values with Regex
Style monetary values in English and French formats using a regex pattern to make them bold and red.

```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.mamboa.easyspans.compose.*

@Composable
fun MonetaryValuesStyled() {
    val MONETARY_PATTERN = "(\\d+([.,]?\\d+)?\\s\\\$)|(\\\$\\d+([.,]?\\d+)?)"
    val annotatedString =
        EasySpansCompose("The item costs $5.50. L'article coûte 5,50 $. Prices are $5 or 5 $.") {
            setColor(Color.Black)
            addOccurrenceChunk(
                occurrenceChunk(
                    occurrenceLocation = OccurrenceLocation(
                        delimitationType = DelimitationType.Regex(MONETARY_PATTERN),
                        occurrencePosition = OccurrencePosition.All
                    ),
                    styleBuilder = { it.copy(color = Color.Red, fontWeight = FontWeight.Bold) }
                )
            )
        }
    Text(
        text = annotatedString,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}
```

**Output**: Monetary values "$5.50", "5,50 $", "$5", and "5 $" are bold and red; the rest of the text is black.

![Monetary Values Output](https://example.com/monetary_values.png)
*Image Description*: The text "The item costs $5.50. L'article coûte 5,50 $. Prices are $5 or 5 $." is displayed, with "$5.50", "5,50 $", "$5", and "5 $" in bold red, and the remaining text in black.

## EasySpansClickableText

`EasySpansClickableText` is a dedicated composable that simplifies rendering clickable `AnnotatedString` text created by `EasySpansCompose`. It handles tap gestures to detect clicks on annotated regions and provides a callback to process the clicked annotation’s tag and item. This composable encapsulates the logic for managing `TextLayoutResult` and detecting tap positions, making it reusable and easy to integrate.

### Features
- **Tap Detection**: Identifies taps on annotated text regions using `detectTapGestures`.
- **Callback Handling**: Invokes a callback with the clicked annotation’s tag and item (e.g., `"click_there"`, `"THERE"`).
- **Customizable Styling**: Supports custom `Modifier` and `TextStyle` for layout and text appearance.
- **Stable State Management**: Uses `remember` to maintain `TextLayoutResult` state efficiently.
- **Seamless Integration**: Works with any `AnnotatedString` from `EasySpansCompose` that includes string annotations.

### Usage Example
Below is an example of using `EasySpansClickableText` with `EasySpansCompose` to create clickable text with feedback via `Toast`.

```kotlin
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mamboa.easyspans.compose.*

@Composable
fun SampleClickableTextScreen() {
  val context = LocalContext.current
  val annotatedString = EasySpansCompose("Click here, there, or now!") {
    setColor(Color.Black)
    addOccurrenceChunk(
      occurrenceChunk(
        occurrenceLocation = OccurrenceLocation(
          delimitationType = DelimitationType.Regex("\\bhere\\b"),
          occurrencePosition = OccurrencePosition.All
        ),
        onClickTag = "click_here",
        styleBuilder = {
          it.copy(
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
          )
        }
      )
    )
    addOccurrenceChunk(
      occurrenceChunk(
        occurrenceLocation = OccurrenceLocation(
          delimitationType = DelimitationType.Regex("\\bthere\\b"),
          occurrencePosition = OccurrencePosition.All
        ),
        onClickTag = "click_there",
        styleBuilder = {
          it.copy(
            color = Color.Green,
            textDecoration = TextDecoration.Underline
          )
        },
        textTransform = { it.uppercase() }
      )
    )
    addOccurrenceChunk(
      occurrenceChunk(
        occurrenceLocation = OccurrenceLocation(
          delimitationType = DelimitationType.Regex("\\bnow\\b"),
          occurrencePosition = OccurrencePosition.All
        ),
        onClickTag = "click_now",
        styleBuilder = {
          it.copy(
            color = Color.Red,
            textDecoration = TextDecoration.Underline
          )
        }
      )
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Tap the colored words below:",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(bottom = 16.dp)
    )
    EasySpansClickableText(
      annotatedString = annotatedString,
      onAnnotationClick = { tag, item ->
        Toast.makeText(
          context,
          "Clicked on: $item (tag: $tag)",
          Toast.LENGTH_SHORT
        ).show()
      },
      style = MaterialTheme.typography.bodyLarge
    )
  }
}
```

**Output**: The text "Click here, THERE, or now!" is displayed, with "here" in blue, "THERE" in green and uppercase, and "now" in red, all underlined. Tapping each word shows a toast with the item and tag (e.g., "Clicked on: THERE (tag: click_there)").

![Sample Clickable Text Output](https://example.com/sample_clickable_text.png)
*Image Description*: The text "Click here, THERE, or now!" is centered with "here" in blue, "THERE" in green and uppercase, and "now" in red, all underlined. A toast appears when tapping a word, showing the clicked item and tag.

## API Overview

- **EasySpansComposeBuilder**: The main builder class for configuring text styles and annotations.
  - **Global Styles**:
    - `setColor`, `setBackgroundColor`: Set text and background colors.
    - `setFontSize`, `setFontWeight`, `setFontStyle`, `setFontFamily`: Configure font properties.
    - `setTextDecoration`: Apply text decorations (e.g., underline, strikethrough).
    - `setScriptType`: Set superscript or subscript via `ScriptType` (`SUPER`, `SUB`, `NONE`).
    - `setTextCase`: Transform the entire text (e.g., `{ it.uppercase() }`).
  - **Occurrence Styling**:
    - `addOccurrenceChunk`: Add a single styled or annotated chunk for specific text segments, optionally with a `textTransform` (e.g., `{ it.uppercase() }`).
    - `setOccurrenceChunks`: Set a list of chunks, replacing existing ones, for batch configuration.
    - `setOccurrenceLocation`: Configure a single occurrence chunk with default styling (less common, prefer `addOccurrenceChunk`).
- **OccurrenceLocation**: Defines how and where to split text for styling (see "Understanding OccurrenceLocation" above).
- **occurrenceChunk**: DSL helper for creating `OccurrenceChunk` instances with location, optional click tag, style builder, and optional `textTransform` for per-chunk text transformations (e.g., uppercase).
- **EasySpansClickableText**: A composable for rendering clickable `AnnotatedString` text with tap detection.
  - Parameters:
    - `annotatedString`: The `AnnotatedString` with annotations (e.g., from `EasySpansCompose`).
    - `onAnnotationClick`: Callback `(tag: String, item: String) -> Unit` for handling taps on annotated regions.
    - `modifier`: Optional `Modifier` for layout customization.
    - `style`: Optional `TextStyle` for text appearance.

## Notes

- **Dependencies**: Requires Jetpack Compose. Styling logic is integrated into `EasySpansComposeBuilder`.
- **Performance**: Optimized with cached regex patterns, efficient range handling, and `@Immutable`/`@Stable` annotations for Compose recomposition.
- **Clickable Text**: Use `EasySpansClickableText` to handle click events on annotated regions, checking annotations with `getStringAnnotations`. This is preferred over raw `Text` with manual tap detection.
- **Delimiter Handling**: Non-existent delimiters result in no styling, ensuring robust behavior (e.g., no accidental styling of the entire text).
- **API Flexibility**: Use `addOccurrenceChunk` for incremental chunk addition or `setOccurrenceChunks` for bulk configuration.
- **Text Transformations**: Per-chunk `textTransform` (e.g., `{ it.uppercase() }`) allows fine-grained control, as seen in `ClickableWords` where "there" becomes "THERE".
- **Package**: Available in `com.mamboa.easyspanscompose`, distinct from the original `EasySpans` in `com.mamboa.easyspans`.
- **Images**: The placeholder image URLs (e.g., `https://example.com/chemical_formula.png`) should be replaced with actual image paths in your project repository or hosting service. For example, if using GitHub, place images in a folder like `docs/images/` and update links to `https://github.com/your-repo/easyspanscompose/raw/main/docs/images/chemical_formula.png`.

### Unit Tests for Further Details

For a deeper understanding of `EasySpansCompose`’s functionality, including edge cases, styling combinations, and clickable annotations, refer to the unit tests included in the project repository. These tests cover various scenarios, such as global and occurrence-based styling, regex and boundary delimiters, text transformations, and click event handling. Examples like `testUppercaseChunkTransformation` demonstrate specific behaviors, such as applying uppercase transformations to text chunks. The unit tests are located in the `test` directory (e.g., `src/test/kotlin/com/mamboa/compose`) and provide detailed, executable examples of the library’s capabilities. Reviewing these tests can help developers verify expected behavior and explore advanced usage patterns.

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
