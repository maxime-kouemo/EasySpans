package com.mamboa.easyspans.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedBackgroundText(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    cornerRadius: Dp = 4.dp,
    horizontalPadding: Dp = 8.dp,
    verticalPadding: Dp = 4.dp,
    occurrenceLocation: OccurrenceLocation,
    selectable: Boolean = true
) {
    var textLayoutResult: TextLayoutResult? = null

    val annotatedString = EasySpansCompose(text) {
        addOccurrenceChunk(
            occurrenceChunk(
                occurrenceLocation = occurrenceLocation,
                styleBuilder = { it }
            )
        )
    }

    Box(modifier = modifier) {
        if (selectable) {
            SelectionContainer {
                Text(
                    text = annotatedString,
                    modifier = Modifier.drawBehind {
                        textLayoutResult?.let { layoutResult ->
                            val ranges = Utils.getOccurrenceRanges(occurrenceLocation, text)

                            for (range in ranges) {
                                val start = range.first
                                val end = range.last + 1
                                val lineRanges = layoutResult.getLineRangesForRange(start, end)

                                lineRanges.forEach { (lineStart, lineEnd, top, bottom) ->
                                    val left = lineStart - horizontalPadding.toPx()
                                    val right = lineEnd + horizontalPadding.toPx()
                                    val topY = top - verticalPadding.toPx()
                                    val bottomY = bottom + verticalPadding.toPx()

                                    drawRoundRect(
                                        color = backgroundColor,
                                        topLeft = Offset(left, topY),
                                        size = androidx.compose.ui.geometry.Size(
                                            right - left,
                                            bottomY - topY
                                        ),
                                        cornerRadius = CornerRadius(cornerRadius.toPx())
                                    )
                                }
                            }
                        }
                    },
                    onTextLayout = { textLayoutResult = it }
                )
            }
        } else {
            Text(
                text = annotatedString,
                modifier = Modifier.drawBehind {
                    textLayoutResult?.let { layoutResult ->
                        val ranges = Utils.getOccurrenceRanges(occurrenceLocation, text)

                        for (range in ranges) {
                            val start = range.first
                            val end = range.last + 1
                            val lineRanges = layoutResult.getLineRangesForRange(start, end)

                            lineRanges.forEach { (lineStart, lineEnd, top, bottom) ->
                                val left = lineStart - horizontalPadding.toPx()
                                val right = lineEnd + horizontalPadding.toPx()
                                val topY = top - verticalPadding.toPx()
                                val bottomY = bottom + verticalPadding.toPx()

                                drawRoundRect(
                                    color = backgroundColor,
                                    topLeft = Offset(left, topY),
                                    size = androidx.compose.ui.geometry.Size(
                                        right - left,
                                        bottomY - topY
                                    ),
                                    cornerRadius = CornerRadius(cornerRadius.toPx())
                                )
                            }
                        }
                    }
                },
                onTextLayout = { textLayoutResult = it }
            )
        }
    }
}

private fun TextLayoutResult.getLineRangesForRange(start: Int, end: Int): List<LineRange> {
    val startLine = getLineForOffset(start)
    val endLine = getLineForOffset(end)

    return (startLine..endLine).map { lineIndex ->
        val lineStart = getHorizontalPosition(
            offset = if (lineIndex == startLine) start else getLineStart(lineIndex),
            usePrimaryDirection = true
        )
        val lineEnd = getHorizontalPosition(
            offset = if (lineIndex == endLine) end else getLineEnd(lineIndex),
            usePrimaryDirection = true
        )
        LineRange(
            lineStart = lineStart,
            lineEnd = lineEnd,
            top = getLineTop(lineIndex),
            bottom = getLineBottom(lineIndex)
        )
    }
}

private data class LineRange(
    val lineStart: Float,
    val lineEnd: Float,
    val top: Float,
    val bottom: Float
)
