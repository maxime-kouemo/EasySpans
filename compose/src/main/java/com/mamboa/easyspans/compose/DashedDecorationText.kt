package com.mamboa.easyspans.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class DashPattern {
    LINE,
    DOT,
    DASH,
    DASH_DOT,
    CUSTOM
}

@Composable
internal fun DashedDecorationText(
    text: String,
    modifier: Modifier = Modifier,
    dashedUnderline: Boolean = false,
    dashedStrikethrough: Boolean = false,
    dashPattern: DashPattern = DashPattern.DOT,
    dashWidth: Dp = 4.dp,
    dashGap: Dp = 2.dp,
    lineColor: Color = Color.Black,
    lineThickness: Dp = 1.dp,
    customPattern: List<Float>? = null,
    occurrenceLocation: OccurrenceLocation
) {
    var textLayoutResult: TextLayoutResult? = null

    val density = LocalDensity.current

    val pattern = with(density) {
        when (dashPattern) {
            DashPattern.LINE -> floatArrayOf(Float.POSITIVE_INFINITY, 0f)
            DashPattern.DOT -> floatArrayOf(2f, dashGap.toPx())
            DashPattern.DASH -> floatArrayOf(dashWidth.toPx(), dashGap.toPx())
            DashPattern.DASH_DOT -> floatArrayOf(
                dashWidth.toPx(),
                dashGap.toPx(),
                2f,
                dashGap.toPx()
            )
            DashPattern.CUSTOM -> customPattern?.toFloatArray()
                ?: floatArrayOf(dashWidth.toPx(), dashGap.toPx())
        }
    }

    val annotatedString = EasySpansCompose(text) {
        addOccurrenceChunk(
            occurrenceChunk(
                occurrenceLocation = occurrenceLocation,
                styleBuilder = { it }
            )
        )
    }

    Text(
        text = annotatedString,
        modifier = modifier.drawBehind {
            textLayoutResult?.let { layoutResult ->
                val ranges = Utils.getOccurrenceRanges(occurrenceLocation, text)
                val strokeWidthPx = lineThickness.toPx()
                val pathEffect = PathEffect.dashPathEffect(pattern)

                for (range in ranges) {
                    val start = range.first
                    val end = range.last + 1
                    val startLine = layoutResult.getLineForOffset(start)
                    val endLine = layoutResult.getLineForOffset(end)

                    for (lineNum in startLine..endLine) {
                        val lineStart = if (lineNum == startLine) {
                            layoutResult.getHorizontalPosition(start, true)
                        } else {
                            layoutResult.getLineLeft(lineNum)
                        }

                        val lineEnd = if (lineNum == endLine) {
                            layoutResult.getHorizontalPosition(end, true)
                        } else {
                            layoutResult.getLineRight(lineNum)
                        }

                        val baseline = layoutResult.getLineBottom(lineNum)

                        if (dashedUnderline) {
                            drawLine(
                                color = lineColor,
                                start = Offset(lineStart, baseline + strokeWidthPx),
                                end = Offset(lineEnd, baseline + strokeWidthPx),
                                strokeWidth = strokeWidthPx,
                                pathEffect = pathEffect
                            )
                        }

                        if (dashedStrikethrough) {
                            val middle = (layoutResult.getLineTop(lineNum) +
                                    layoutResult.getLineBottom(lineNum)) / 2
                            drawLine(
                                color = lineColor,
                                start = Offset(lineStart, middle),
                                end = Offset(lineEnd, middle),
                                strokeWidth = strokeWidthPx,
                                pathEffect = pathEffect
                            )
                        }
                    }
                }
            }
        },
        onTextLayout = { textLayoutResult = it }
    )
}
