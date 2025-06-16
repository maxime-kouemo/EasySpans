package com.mamboa.easyspans.compose

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle

/**
 * A composable that displays an [AnnotatedString] and allows for precise clickable spans.
 *
 * @param annotatedString The string with annotations to display.
 * @param onAnnotationClick Callback invoked when an annotation is clicked, providing the tag and item.
 * @param modifier Modifier to be applied to the text.
 * @param style TextStyle to be applied to the text.
 */
@Composable
fun EasySpansClickableText(
    annotatedString: AnnotatedString,
    onAnnotationClick: (tag: String, item: String) -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    Text(
        text = annotatedString,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    textLayoutResult?.let { layoutResult ->
                        val position = layoutResult.getOffsetForPosition(offset)
                        val annotations =
                            annotatedString.getStringAnnotations(start = position, end = position)
                        annotations.firstOrNull()?.let { annotation ->
                            onAnnotationClick(annotation.tag, annotation.item)
                        }
                    }
                }
            },
        style = style,
        onTextLayout = { layoutResult ->
            textLayoutResult = layoutResult
        },
    )
}