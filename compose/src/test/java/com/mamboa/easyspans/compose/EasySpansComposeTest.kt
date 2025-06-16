package com.mamboa.easyspans.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EasySpansComposeTest {

    private val text = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla nec tempus est. Vestibulum volutpat ipsum vitae urna congue, vitae facilisis est iaculis. Integer accumsan ex et nibh mollis, vitae malesuada lacus porttitor. Maecenas commodo turpis nec porttitor fringilla. Maecenas fermentum massa in pulvinar tempus. Phasellus at volutpat mi. Suspendisse faucibus vitae mi vel sollicitudin. Aenean sit amet malesuada ipsum, at vestibulum lectus. Aliquam erat volutpat. Praesent tempus nibh ac ante aliquet suscipit. Praesent ex lectus, dapibus non porttitor id, aliquet nec sem. Mauris ac fringilla augue, ac tincidunt enim. Proin vestibulum auctor mi vitae facilisis. Pellentesque fermentum, mauris a mattis efficitur, ligula enim lobortis eros, sed pulvinar felis dui nec augue. In eget dignissim quam, in blandit massa.

        Phasellus turpis mauris, faucibus vel hendrerit id, mollis ut ex. Etiam cursus nisl nec dapibus eleifend. Phasellus consectetur diam a nibh luctus, in tempor ante viverra. Morbi nec vulputate lorem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis in nunc venenatis, viverra est sed, feugiat magna. In et ullamcorper dolor. Phasellus iaculis sit amet leo id cursus. Etiam congue scelerisque quam, vel accumsan massa mattis vel. Sed diam diam, iaculis eget turpis eget, porta vestibulum ligula. Aliquam tincidunt finibus sem, nec faucibus neque vulputate eget. Nullam ultricies odio a felis egestas dictum.

        Ut eget pretium purus. Aliquam volutpat tristique metus, eget euismod tortor tempus a. Nunc non scelerisque nulla. Donec sit amet mi sit amet libero tristique pretium eget vitae libero. Maecenas tristique dictum tortor id pulvinar. Donec convallis porta tincidunt. Fusce pretium interdum rhoncus. In hac habitasse platea dictumst. Nam dictum non sapien sed sollicitudin. Maecenas eget massa vel felis condimentum ornare sit amet at est. Praesent egestas metus ut turpis convallis dapibus eu in lorem. Duis vel massa pretium, ultricies justo at, faucibus est. Mauris sed aliquam nulla. Aliquam dapibus quam id eleifend tempor. Nullam metus leo, porta eu erat condimentum, varius iaculis odio. Suspendisse potenti.

        Nunc semper aliquam aliquet. Pellentesque in mattis lorem. Sed finibus scelerisque egestas. Donec efficitur molestie velit, sagittis tincidunt turpis semper sed. Maecenas in quam eu turpis sodales laoreet vel vitae mauris. In pretium aliquet ante, at ullamcorper odio lobortis at. Aenean et felis eget augue placerat vulputate. Proin ac neque purus. Mauris malesuada tellus non orci rhoncus, nec convallis felis lobortis. Suspendisse ut bibendum ex. Nullam scelerisque porttitor orci id tincidunt. Sed sit amet malesuada quam, pretium congue nisl. Nunc urna purus, luctus et lectus in, sodales tempus quam. Sed auctor tempor facilisis. Nam ante quam, auctor et sem sed, feugiat volutpat mauris. Aenean elementum metus ut varius sagittis.

        Sed molestie egestas diam, quis dignissim diam efficitur pretium. Phasellus luctus ante ac eros consectetur accumsan. Quisque laoreet tincidunt tellus, vel auctor quam auctor quis. Sed rhoncus orci ac nunc ultricies faucibus. Praesent auctor, neque et interdum imperdiet, ante mauris egestas nisi, id auctor magna sapien non leo. Phasellus in felis ac lectus dapibus porttitor. Duis porta sit amet augue non imperdiet. Ut posuere vehicula congue. In maximus fermentum felis, id feugiat est elementum nec. Pellentesque feugiat dolor risus, id sodales erat auctor at. Sed congue dignissim erat, et pellentesque nunc dictum vel.
    """.trimIndent()

    private val teal_700 = Color(0xFF00695C)
    private val magenta = Color(0xFFAB47BC) // Assuming this is the color used in the original test
    private val cyan = Color(0xFF26A69A)
    private val test_default_text_size = 16.sp // Assuming this is the default text size used in the original test

    @Test
    fun testSpans() {
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                setColor(teal_700) // Equivalent to teal_700
                setFontSize(test_default_text_size) // Assuming test_default_text_size is 16sp
                setFontWeight(FontWeight.Bold) // Equivalent to Typeface.BOLD
                setTextDecoration(
                    TextDecoration.combine(
                        listOf(
                            TextDecoration.LineThrough,
                            TextDecoration.Underline
                        )
                    )
                )
                setTextCase { it.uppercase() }
            }
            .build()

        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.size)
        val style = spanStyles[0].item
        assertEquals(teal_700, style.color)
        assertEquals(test_default_text_size, style.fontSize)
        assertEquals(FontWeight.Bold, style.fontWeight)
        assertEquals(
            TextDecoration.combine(
                listOf(
                    TextDecoration.LineThrough,
                    TextDecoration.Underline
                )
            ), style.textDecoration
        )
        assertEquals(text.uppercase(), annotatedString.text)
    }

    @Test
    fun testDelimiterSuccess() {
        val desiredWord = "dolor"
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(" "),
                            occurrencePosition = OccurrencePosition.Nth(2)
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        val startIndex = annotatedString.text.indexOf(desiredWord)
        val endIndex = startIndex + desiredWord.length
        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any { it.item.textDecoration == TextDecoration.Underline && it.start == startIndex && it.end == endIndex })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testDelimiterFailure() {
        val desiredWord = "dolor"
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(" "),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        val startIndex = annotatedString.text.indexOf(desiredWord)
        val endIndex = startIndex + desiredWord.length
        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.none { it.item.textDecoration == TextDecoration.Underline && it.start == startIndex && it.end == endIndex })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testRegexOccurrencePositionFirst() {
        val regex = "nec"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries[0].first &&
                    it.end == occurrenceBoundaries[0].last + 1
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testRegexOccurrencePositionNth() {
        val regex = "nec"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val occurrencePosition = 2
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.Nth(occurrencePosition)
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.size > occurrencePosition)
        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries[occurrencePosition].first &&
                    it.end == occurrenceBoundaries[occurrencePosition].last + 1
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testRegexOccurrencePositionIndexes() {
        val regex = "nec"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val occurrencePositionIndexes = listOf(1, 5, 8)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.Indices(
                                occurrencePositionIndexes
                            )
                        ),
                        styleBuilder = {
                            it.copy(
                                textDecoration = TextDecoration.combine(
                                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                                )
                            )
                        }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.size >= occurrencePositionIndexes.maxOrNull()!! + 1)
        val spanStyles = annotatedString.spanStyles
        assertEquals(
            occurrencePositionIndexes.size,
            spanStyles.count {
                it.item.textDecoration == TextDecoration.combine(
                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                )
            }
        )
        occurrencePositionIndexes.forEach { index ->
            assertTrue(spanStyles.any {
                it.item.textDecoration == TextDecoration.combine(
                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                ) &&
                        it.start == occurrenceBoundaries[index].first &&
                        it.end == occurrenceBoundaries[index].last + 1
            })
        }
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testRegexOccurrencePositionLast() {
        val regex = "nec"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val occurrencePosition = occurrenceBoundaries.size - 1
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.Last
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries[occurrencePosition].first &&
                    it.end == occurrenceBoundaries[occurrencePosition].last + 1
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testRegexOccurrencePositionAll() {
        val regex = "nec"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        styleBuilder = {
                            it.copy(
                                textDecoration = TextDecoration.combine(
                                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                                )
                            )
                        }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val spanStyles = annotatedString.spanStyles
        assertEquals(
            occurrenceBoundaries.size,
            spanStyles.count {
                it.item.textDecoration == TextDecoration.combine(
                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                )
            }
        )
        occurrenceBoundaries.forEachIndexed { index, range ->
            assertTrue(spanStyles.any {
                it.item.textDecoration == TextDecoration.combine(
                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                ) &&
                        it.start == range.first &&
                        it.end == range.last + 1
            })
        }
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testRegexOccurrencePositionFailure() {
        val regex = "Leonid master"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val tests = listOf(
            OccurrencePosition.First,
            OccurrencePosition.Nth(3),
            OccurrencePosition.Last,
            OccurrencePosition.All
        )

        tests.forEach { position ->
            val annotatedString = EasySpansComposeBuilder.create(text)
                .apply {
                    addOccurrenceChunk(
                        occurrenceChunk(
                            occurrenceLocation = OccurrenceLocation(
                                delimitationType = DelimitationType.Regex(regex),
                                occurrencePosition = position
                            ),
                            styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                        )
                    )
                }
                .build()

            assertTrue(occurrenceBoundaries.isEmpty())
            val spanStyles = annotatedString.spanStyles
            assertEquals(0, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
            assertEquals(text, annotatedString.text)
        }
    }

    @Test
    fun testDelimiterOccurrencePositionFirst() {
        val expectedWord = "Lorem"
        val delimiter = " "
        val occurrenceBoundaries = Utils.getBoundaryRanges(text, delimiter)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(delimiter),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries[0].first &&
                    it.end == occurrenceBoundaries[0].last + 1 &&
                    annotatedString.text.substring(it.start, it.end) == expectedWord
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testDelimiterOccurrencePositionNth() {
        val expectedWord = "consectetur"
        val delimiter = " "
        val occurrenceBoundaries = Utils.getBoundaryRanges(text, delimiter)
        val nthPosition = 5
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(delimiter),
                            occurrencePosition = OccurrencePosition.Nth(nthPosition)
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries[nthPosition].first &&
                    it.end == occurrenceBoundaries[nthPosition].last + 1 &&
                    annotatedString.text.substring(it.start, it.end) == expectedWord
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testDelimiterOccurrencePositionNthIndexes() {
        val expectedSentence1 = "Lorem"
        val expectedSentence2 = "consectetur"
        val expectedSentence3 = "elit."
        val occurrencePositionIndexes = listOf(0, 5, 7)
        val delimiter = " "
        val occurrenceBoundaries = Utils.getBoundaryRanges(text, delimiter)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(delimiter),
                            occurrencePosition = OccurrencePosition.Indices(
                                occurrencePositionIndexes
                            )
                        ),
                        styleBuilder = {
                            it.copy(
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold // Proxy for uppercase transformation
                            )
                        }
                    )
                )
            }
            .build()

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            occurrencePositionIndexes.size,
            spanStyles.count { it.item.textDecoration == TextDecoration.Underline && it.item.fontWeight == FontWeight.Bold }
        )
        occurrencePositionIndexes.forEachIndexed { i, index ->
            assertTrue(spanStyles.any {
                it.item.textDecoration == TextDecoration.Underline &&
                        it.item.fontWeight == FontWeight.Bold &&
                        it.start == occurrenceBoundaries[index].first &&
                        it.end == occurrenceBoundaries[index].last + 1 &&
                        annotatedString.text.substring(
                            it.start,
                            it.end
                        ) == listOf(expectedSentence1, expectedSentence2, expectedSentence3)[i]
            })
        }
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testDelimiterOccurrencePositionLast() {
        val expectedWord = "vel."
        val delimiter = " "
        val occurrenceBoundaries = Utils.getBoundaryRanges(text, delimiter)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(delimiter),
                            occurrencePosition = OccurrencePosition.Last
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries.last().first &&
                    it.end == occurrenceBoundaries.last().last + 1 &&
                    annotatedString.text.substring(it.start, it.end) == expectedWord
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testDelimiterOccurrencePositionAll() {
        val delimiter = " "
        val occurrenceBoundaries = Utils.getBoundaryRanges(text, delimiter)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(delimiter),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        styleBuilder = {
                            it.copy(
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold // Proxy for uppercase transformation
                            )
                        }
                    )
                )
            }
            .build()

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            occurrenceBoundaries.size,
            spanStyles.count { it.item.textDecoration == TextDecoration.Underline && it.item.fontWeight == FontWeight.Bold }
        )
        occurrenceBoundaries.forEach { range ->
            assertTrue(spanStyles.any {
                it.item.textDecoration == TextDecoration.Underline &&
                        it.item.fontWeight == FontWeight.Bold &&
                        it.start == range.first &&
                        it.end == range.last + 1
            })
        }
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testSingleLinkRegexPositionLast() {
        val regex = "nec"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val occurrencePosition = occurrenceBoundaries.size - 1
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.Last
                        ),
                        onClickTag = "clickable",
                        styleBuilder = {
                            it.copy(
                                color = teal_700,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any {
            it.start == occurrenceBoundaries[occurrencePosition].first &&
                    it.end == occurrenceBoundaries[occurrencePosition].last + 1 &&
                    it.tag == "clickable"
        })
        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.color == teal_700 && it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.color == teal_700 &&
                    it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries[occurrencePosition].first &&
                    it.end == occurrenceBoundaries[occurrencePosition].last + 1
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testSingleLinkBoundaryPositionFirst() {
        val expectedWord = "Lorem"
        val delimiter = " "
        val occurrenceBoundaries = Utils.getBoundaryRanges(text, delimiter)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(delimiter),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = {
                            it.copy(
                                color = teal_700,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    )
                )
            }
            .build()

        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any {
            it.start == occurrenceBoundaries[0].first &&
                    it.end == occurrenceBoundaries[0].last + 1 &&
                    it.tag == "clickable" &&
                    annotatedString.text.substring(it.start, it.end) == expectedWord
        })
        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.color == teal_700 && it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.color == teal_700 &&
                    it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries[0].first &&
                    it.end == occurrenceBoundaries[0].last + 1
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testMultipleLinkRegexPositionAll() {
        val regex1 = "consectetur"
        val regex2 = "Donec"
        val occurrenceBoundaries1 = Utils.getRegexMatchRanges(text, Regex(regex1))
        val occurrenceBoundaries2 = Utils.getRegexMatchRanges(text, Regex(regex2))
        val occurrenceBoundaries =
            (occurrenceBoundaries1 + occurrenceBoundaries2).sortedBy { it.first }
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex1),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "clickable1",
                        styleBuilder = {
                            it.copy(
                                color = teal_700,
                                textDecoration = TextDecoration.combine(
                                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                                )
                            )
                        }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex2),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "clickable2",
                        styleBuilder = {
                            it.copy(
                                color = teal_700,
                                textDecoration = TextDecoration.combine(
                                    listOf(TextDecoration.Underline, TextDecoration.LineThrough)
                                )
                            )
                        }
                    )
                )
            }
            .build()

        val annotations1 =
            annotatedString.getStringAnnotations("clickable1", 0, annotatedString.length)
        val annotations2 =
            annotatedString.getStringAnnotations("clickable2", 0, annotatedString.length)
        assertEquals(occurrenceBoundaries1.size, annotations1.size)
        assertEquals(occurrenceBoundaries2.size, annotations2.size)
        val spanStyles = annotatedString.spanStyles
        assertEquals(
            occurrenceBoundaries.size,
            spanStyles.count {
                it.item.color == teal_700 &&
                        it.item.textDecoration == TextDecoration.combine(
                    listOf(
                        TextDecoration.Underline,
                        TextDecoration.LineThrough
                    )
                )
            }
        )
        occurrenceBoundaries.forEach { range ->
            assertTrue(spanStyles.any {
                it.item.color == teal_700 &&
                        it.item.textDecoration == TextDecoration.combine(
                    listOf(
                        TextDecoration.Underline,
                        TextDecoration.LineThrough
                    )
                ) &&
                        it.start == range.first &&
                        it.end == range.last + 1
            })
            assertTrue(
                annotations1.any { it.start == range.first && it.end == range.last + 1 } ||
                        annotations2.any { it.start == range.first && it.end == range.last + 1 }
            )
        }
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testCombineBasicSpansWithRegexNth() {
        val regex = "ipsum"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val occurrencePosition = 1
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.Nth(occurrencePosition)
                        ),
                        styleBuilder = {
                            it.copy(
                                color = magenta,
                                fontStyle = FontStyle.Italic
                            )
                        } // Purple_500, Typeface.ITALIC
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.size > occurrencePosition)
        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.color == magenta && it.item.fontStyle == FontStyle.Italic })
        assertTrue(spanStyles.any {
            it.item.color == magenta &&
                    it.item.fontStyle == FontStyle.Italic &&
                    it.start == occurrenceBoundaries[occurrencePosition].first &&
                    it.end == occurrenceBoundaries[occurrencePosition].last + 1
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testCombineGlobalSpansWithLinkChunk() {
        val regex = "dolor"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                setFontSize(test_default_text_size) // Assuming test_default_text_size is 16sp
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = {
                            it.copy(
                                color = cyan,
                                textDecoration = null
                            )
                        } // Teal_200, no underline
                    )
                )
            }
            .build()

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.fontSize == test_default_text_size && it.start == 0 && it.end == text.length })
        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any {
            it.start == occurrenceBoundaries[0].first &&
                    it.end == occurrenceBoundaries[0].last + 1 &&
                    it.tag == "clickable"
        })
        assertEquals(
            1,
            spanStyles.count { it.item.color == cyan && it.item.textDecoration == null })
        assertTrue(spanStyles.any {
            it.item.color == cyan &&
                    it.item.textDecoration == null &&
                    it.start == occurrenceBoundaries[0].first &&
                    it.end == occurrenceBoundaries[0].last + 1
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testRegexCaseInsensitive() {
        val regex = "lorem"
        val caseInsensitiveRegex = "(?i)$regex"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(caseInsensitiveRegex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(caseInsensitiveRegex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        assertEquals(0, occurrenceBoundaries[0].first)
        assertEquals(
            "Lorem",
            text.substring(occurrenceBoundaries[0].first, occurrenceBoundaries[0].last + 1)
        )
        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertTrue(spanStyles.any {
            it.item.textDecoration == TextDecoration.Underline &&
                    it.start == occurrenceBoundaries[0].first &&
                    it.end == occurrenceBoundaries[0].last + 1
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testDelimiterNotFound() {
        val delimiter = "NonExistentDelimiterString"
        val occurrenceBoundaries = Utils.getBoundaryRanges(text, delimiter)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(delimiter),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isEmpty())
        val spanStyles = annotatedString.spanStyles
        assertEquals(0, spanStyles.count { it.item.textDecoration == TextDecoration.Underline })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testLinkChunkDefaults() {
        val regex = "elit"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = { it } // Default style
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any {
            it.start == occurrenceBoundaries[0].first &&
                    it.end == occurrenceBoundaries[0].last + 1 &&
                    it.tag == "clickable"
        })
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testEmptyText() {
        val emptyText = ""
        val annotatedString = EasySpansComposeBuilder.create(emptyText)
            .apply {
                setColor(teal_700)
                setTextDecoration(TextDecoration.Underline)
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("a"),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "clickable",
                        styleBuilder = { it }
                    )
                )
            }
            .build()

        assertEquals(emptyText, annotatedString.text)
        assertEquals(0, annotatedString.length)
        assertEquals(0, annotatedString.spanStyles.size)
        assertEquals(0, annotatedString.getStringAnnotations("clickable", 0, 0).size)
    }

    @Test
    fun testSingleLinkRegexNoBuilder() {
        val regex = "tempus"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                setFontSize(test_default_text_size)
                setColor(teal_700)
                setTextDecoration(TextDecoration.Underline)
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable"
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any { it.start == expectedStart && it.end == expectedEnd && it.tag == "clickable" })

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.fontSize == test_default_text_size && it.start == 0 && it.end == text.length })
        assertEquals(
            1,
            spanStyles.count { it.item.color == teal_700 && it.start == 0 && it.end == text.length })
        assertEquals(
            1,
            spanStyles.count { it.item.textDecoration == TextDecoration.Underline && it.start == 0 && it.end == text.length })

        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testMixedClickableAndNonClickableChunks() {
        val regex1 = "dolor"
        val regex2 = "ipsum"
        val occurrenceBoundaries1 = Utils.getRegexMatchRanges(text, Regex(regex1))
        val occurrenceBoundaries2 = Utils.getRegexMatchRanges(text, Regex(regex2))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                setFontWeight(FontWeight.Bold)
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex1),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = { it.copy(color = magenta) }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex2),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(fontSize = test_default_text_size) }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries1.isNotEmpty())
        assertTrue(occurrenceBoundaries2.isNotEmpty())

        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any { it.start == occurrenceBoundaries1[0].first && it.end == occurrenceBoundaries1[0].last + 1 })

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.fontSize == test_default_text_size && it.start == occurrenceBoundaries2[0].first && it.end == occurrenceBoundaries2[0].last + 1 })
        assertEquals(
            1,
            spanStyles.count { it.item.fontWeight == FontWeight.Bold && it.start == 0 && it.end == text.length })

        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testLinkChunkOverridesGlobalStyles() {
        val regex = "elit"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                setColor(teal_700)
                setTextDecoration(TextDecoration.Underline)
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = { it.copy(color = magenta, textDecoration = null) }
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any { it.start == expectedStart && it.end == expectedEnd })

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.color == teal_700 && it.item.textDecoration == TextDecoration.Underline && it.start == 0 && it.end == text.length })
        assertEquals(
            1,
            spanStyles.count { it.item.color == magenta && it.item.textDecoration == null && it.start == expectedStart && it.end == expectedEnd })

        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testInvalidOccurrenceChunkInputs() {
        val malformedRegex = "[a-z"
        var annotatedString: AnnotatedString? = null
        try {
            annotatedString = EasySpansComposeBuilder.create(text)
                .apply {
                    addOccurrenceChunk(
                        occurrenceChunk(
                            occurrenceLocation = OccurrenceLocation(
                                delimitationType = DelimitationType.Regex(malformedRegex),
                                occurrencePosition = OccurrencePosition.First
                            )
                        )
                    )
                }
                .build()
            assertTrue("Expected RegexSyntaxException", false)
        } catch (e: IllegalArgumentException) {
            assertEquals(null, annotatedString)
        }

        val emptyRegex = ""
        annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(emptyRegex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable"
                    )
                )
            }
            .build()

        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(0, annotations.size)
        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testLargeTextWithManyIndexes() {
        val largeText = text.repeat(10)
        val regex = "nec"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(largeText, Regex(regex))
        val chunks = (0 until 5).map { i ->
            occurrenceChunk(
                occurrenceLocation = OccurrenceLocation(
                    delimitationType = DelimitationType.Regex(regex),
                    occurrencePosition = OccurrencePosition.Nth(i)
                ),
                onClickTag = "click_$i",
                styleBuilder = { it.copy(color = cyan) }
            )
        }

        val annotatedString = EasySpansComposeBuilder.create(largeText)
            .apply {
                setFontWeight(FontWeight.Bold)
                setOccurrenceChunks(*chunks.toTypedArray())
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val annotations = (0 until 5).flatMap { i ->
            annotatedString.getStringAnnotations("click_$i", 0, annotatedString.length)
        }
        assertEquals(5, annotations.size)
        (0 until 5).forEach { i ->
            assertTrue(annotations.any {
                it.start == occurrenceBoundaries[i].first && it.end == occurrenceBoundaries[i].last + 1 && it.tag == "click_$i"
            })
        }

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.fontWeight == FontWeight.Bold && it.start == 0 && it.end == largeText.length })
        assertEquals(5, spanStyles.count { it.item.color == cyan })

        assertEquals(largeText, annotatedString.text)
    }

    @Test
    fun testClickableLinkDefaultBuilderStyles() {
        val regex = "tempus"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                setColor(teal_700)
                setTextDecoration(TextDecoration.Underline)
                setFontSize(test_default_text_size)
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable"
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any { it.start == expectedStart && it.end == expectedEnd })

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.color == teal_700 && it.start == 0 && it.end == text.length })
        assertEquals(
            1,
            spanStyles.count { it.item.textDecoration == TextDecoration.Underline && it.start == 0 && it.end == text.length })
        assertEquals(
            1,
            spanStyles.count { it.item.fontSize == test_default_text_size && it.start == 0 && it.end == text.length })

        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testOccurrenceChunkBuilderAllStyles() {
        val regex = "dolor"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = {
                            it.copy(
                                color = magenta,
                                background = cyan,
                                fontSize = test_default_text_size,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                textDecoration = TextDecoration.combine(
                                    listOf(
                                        TextDecoration.Underline,
                                        TextDecoration.LineThrough
                                    )
                                )
                            )
                        }
                    )
                )
                setScriptType(ScriptType.SUB)
                setTextCase { it.uppercase() }
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any { it.start == expectedStart && it.end == expectedEnd })

        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count {
            it.item.color == magenta &&
                    it.item.background == cyan &&
                    it.item.fontSize == test_default_text_size &&
                    it.item.fontWeight == FontWeight.Bold &&
                    it.item.fontStyle == FontStyle.Italic &&
                    it.item.textDecoration == TextDecoration.combine(
                listOf(
                    TextDecoration.Underline,
                    TextDecoration.LineThrough
                )
            ) &&
                    it.start == expectedStart && it.end == expectedEnd
        })
        assertEquals(
            1,
            spanStyles.count { it.item.baselineShift == BaselineShift.Subscript && it.start == 0 && it.end == text.length })

        assertEquals(text.uppercase(), annotatedString.text)
    }

    @Test
    fun testOverlappingRegexAndBoundaryChunks() {
        val regex = "ipsum"
        val boundary = " "
        val regexBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val boundaryBoundaries = Utils.getBoundaryRanges(text, boundary)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = { it.copy(color = magenta) }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(boundary),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(fontSize = test_default_text_size) }
                    )
                )
            }
            .build()

        assertTrue(regexBoundaries.isNotEmpty())
        assertTrue(boundaryBoundaries.isNotEmpty())

        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any { it.start == regexBoundaries[0].first && it.end == regexBoundaries[0].last + 1 })

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.color == magenta && it.start == regexBoundaries[0].first && it.end == regexBoundaries[0].last + 1 })
        assertEquals(
            1,
            spanStyles.count { it.item.fontSize == test_default_text_size && it.start == boundaryBoundaries[0].first && it.end == boundaryBoundaries[0].last + 1 })

        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testRegexClickableChunkAlone() {
        val regex = "ipsum"
        val regexBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = { it.copy(color = magenta) }
                    )
                )
            }
            .build()

        assertTrue(regexBoundaries.isNotEmpty())
        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(1, annotations.size)
        assertTrue(annotations.any { it.start == regexBoundaries[0].first && it.end == regexBoundaries[0].last + 1 })

        val spanStyles = annotatedString.spanStyles
        assertEquals(
            1,
            spanStyles.count { it.item.color == magenta && it.start == regexBoundaries[0].first && it.end == regexBoundaries[0].last + 1 })

        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testInvalidOccurrencePositionIndices() {
        val regex = "nec"
        val occurrenceBoundaries = Utils.getRegexMatchRanges(text, Regex(regex))
        val invalidIndices = listOf(100, 200)
        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.Indices(invalidIndices)
                        ),
                        onClickTag = "clickable"
                    )
                )
            }
            .build()

        assertTrue(occurrenceBoundaries.isNotEmpty())
        val annotations =
            annotatedString.getStringAnnotations("clickable", 0, annotatedString.length)
        assertEquals(0, annotations.size)

        assertEquals(text, annotatedString.text)
    }

    @Test
    fun testMonetaryPatternRegex() {
        val testText = "Price: 5 $, $10.50, 100.00 $"
        val regex = """(\d+\s?\$)|(\$\d+(\.\d{1,2})?)|(\d+\.\d{1,2}\s?\$)"""
        val occurrenceBoundaries = Utils.getRegexMatchRanges(testText, Regex(regex))
        val annotatedString = EasySpansComposeBuilder.create(testText)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        styleBuilder = { it.copy(color = teal_700) }
                    )
                )
            }
            .build()

        assertEquals(3, occurrenceBoundaries.size)
        val spanStyles = annotatedString.spanStyles
        assertEquals(3, spanStyles.count { it.item.color == teal_700 })
        occurrenceBoundaries.forEach { range ->
            assertTrue(spanStyles.any { it.item.color == teal_700 && it.start == range.first && it.end == range.last + 1 })
        }

        assertEquals(testText, annotatedString.text)
    }

    @Test
    fun testMultipleRegexAndBoundaryChunks() {
        val testText = "Lorem ipsum dolor ipsum"
        val regex1 = "ipsum"
        val regex2 = "dolor"
        val boundary = " "
        val regexBoundaries1 = Utils.getRegexMatchRanges(testText, Regex(regex1))
        val regexBoundaries2 = Utils.getRegexMatchRanges(testText, Regex(regex2))
        val boundaryBoundaries = Utils.getBoundaryRanges(testText, boundary)
        val annotatedString = EasySpansComposeBuilder.create(testText)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex1),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "clickable1",
                        styleBuilder = { it.copy(color = magenta) }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex2),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(fontSize = test_default_text_size) }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(boundary),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        styleBuilder = { it.copy(fontSize = test_default_text_size) }
                    )
                )
            }
            .build()

        assertTrue(regexBoundaries1.isNotEmpty())
        assertTrue(regexBoundaries2.isNotEmpty())
        assertTrue(boundaryBoundaries.isNotEmpty())

        val annotations =
            annotatedString.getStringAnnotations("clickable1", 0, annotatedString.length)
        assertEquals(2, annotations.size)
        regexBoundaries1.forEach { range ->
            assertTrue(annotations.any { it.start == range.first && it.end == range.last + 1 })
        }

        val spanStyles = annotatedString.spanStyles
        assertEquals(2, spanStyles.count { it.item.color == magenta })
        assertEquals(
            1,
            spanStyles.count { it.item.fontSize == test_default_text_size && it.start == regexBoundaries2[0].first && it.end == regexBoundaries2[0].last + 1 })
        assertEquals(
            1,
            spanStyles.count { it.item.fontSize == test_default_text_size && it.start == boundaryBoundaries[0].first && it.end == boundaryBoundaries[0].last + 1 })

        assertEquals(testText, annotatedString.text)
    }

    @Test
    fun testConsecutiveBoundaryDelimiters() {
        val testText = "Lorem  ipsum   dolor"
        val boundary = " "
        val boundaryBoundaries = Utils.getBoundaryRanges(testText, boundary)
        val annotatedString = EasySpansComposeBuilder.create(testText)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Boundary(boundary),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        styleBuilder = { it.copy(fontSize = test_default_text_size) }
                    )
                )
            }
            .build()

        assertEquals(3, boundaryBoundaries.size)
        val spanStyles = annotatedString.spanStyles
        assertEquals(3, spanStyles.count { it.item.fontSize == test_default_text_size })
        boundaryBoundaries.forEach { range ->
            assertTrue(spanStyles.any { it.item.fontSize == test_default_text_size && it.start == range.first && it.end == range.last + 1 })
        }

        assertEquals(testText, annotatedString.text)
    }

    @Test
    fun testReusedEasySpans() {
        val testText = "Lorem ipsum"
        val builder = EasySpansComposeBuilder.create(testText)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("ipsum"),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "clickable",
                        styleBuilder = { it.copy(color = magenta) }
                    )
                )
            }

        val annotatedString1 = builder.build()
        val annotatedString2 = builder.build()

        val annotations1 =
            annotatedString1.getStringAnnotations("clickable", 0, annotatedString1.length)
        val annotations2 =
            annotatedString2.getStringAnnotations("clickable", 0, annotatedString2.length)
        assertEquals(1, annotations1.size)
        assertEquals(1, annotations2.size)
        assertEquals(annotations1[0].start, annotations2[0].start)
        assertEquals(annotations1[0].end, annotations2[0].end)

        assertEquals(testText, annotatedString1.text)
        assertEquals(testText, annotatedString2.text)
    }

    @Test
    fun testMultipleOccurrenceChunksWithClickTags() {
        val text = "Lorem ipsum dolor sit amet"
        val regex1 = "dolor"
        val regex2 = "ipsum"
        val boundaries1 = Utils.getRegexMatchRanges(text, Regex(regex1))
        val boundaries2 = Utils.getRegexMatchRanges(text, Regex(regex2))
        assertTrue(boundaries1.isNotEmpty())
        assertTrue(boundaries2.isNotEmpty())

        val annotatedString = EasySpansComposeBuilder.create(text)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex(regex1),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "dolor_click",
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
                            delimitationType = DelimitationType.Regex(regex2),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        onClickTag = "ipsum_click",
                        styleBuilder = {
                            it.copy(
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    )
                )
                setTextCase { it.uppercase() }
            }
            .build()

        val dolorAnnotations = annotatedString.getStringAnnotations("dolor_click", 0, annotatedString.length)
        assertEquals(1, dolorAnnotations.size)
        assertEquals(boundaries1[0].first, dolorAnnotations[0].start)
        assertEquals(boundaries1[0].last + 1, dolorAnnotations[0].end)

        val ipsumAnnotations = annotatedString.getStringAnnotations("ipsum_click", 0, annotatedString.length)
        assertEquals(1, ipsumAnnotations.size)
        assertEquals(boundaries2[0].first, ipsumAnnotations[0].start)
        assertEquals(boundaries2[0].last + 1, ipsumAnnotations[0].end)

        assertEquals(text.uppercase(), annotatedString.text)
    }

    @Test
    fun testMultipleNonOverlappingClickTags() {
        val testText = "Click here or there!"
        val boundaries1 = Utils.getRegexMatchRanges(testText, Regex("here"))
        val boundaries2 = Utils.getRegexMatchRanges(testText, Regex("there"))
        val annotatedString = EasySpansComposeBuilder.create(testText)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("here"),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "click_here",
                        styleBuilder = { it.copy(color = Color.Blue, textDecoration = TextDecoration.Underline) }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("there"),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "click_there",
                        styleBuilder = { it.copy(color = Color.Green, textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        val hereAnnotations = annotatedString.getStringAnnotations("click_here", 0, annotatedString.length)
        val thereAnnotations = annotatedString.getStringAnnotations("click_there", 0, annotatedString.length)

        assertEquals(2, hereAnnotations.size) // Two "here" occurrences
        assertEquals(1, thereAnnotations.size) // One "there" occurrence

        // Verify "here" annotations (at 6-10
        assertTrue(hereAnnotations.any { it.start == boundaries1[0].first && it.end == boundaries1[0].last + 1 && it.item == "here" })

        // Verify "there" annotation (at 14-19)
        assertTrue(thereAnnotations.any { it.start == boundaries2[0].first && it.end == boundaries2[0].last + 1 && it.item == "there" })

        // Ensure no overlap: "there" range (14-19) should not have "click_here" tag
        assertFalse(hereAnnotations.any { it.start == boundaries2[0].first && it.end == boundaries2[0].last + 1 })

        // Verify styles
        val spanStyles = annotatedString.spanStyles
        assertEquals(2, spanStyles.count { it.item.color == Color.Blue }) // Two "here" with blue
        assertEquals(1, spanStyles.count { it.item.color == Color.Green }) // One "there" with green
    }

    @Test
    fun testThreeDistinctClickableChunks() {
        val testText = "Click here, there, or now!"
        val boundaries1 = Utils.getRegexMatchRanges(testText, Regex("\\bhere\\b")) // Use word boundaries
        val boundaries2 = Utils.getRegexMatchRanges(testText, Regex("\\bthere\\b"))
        val boundaries3 = Utils.getRegexMatchRanges(testText, Regex("\\bnow\\b"))
        val annotatedString = EasySpansComposeBuilder.create(testText)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("\\bhere\\b"), // Use word boundaries
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "click_here",
                        styleBuilder = { it.copy(color = Color.Blue, textDecoration = TextDecoration.Underline) }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("\\bthere\\b"),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "click_there",
                        styleBuilder = { it.copy(color = Color.Green, textDecoration = TextDecoration.Underline) }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("\\bnow\\b"),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "click_now",
                        styleBuilder = { it.copy(color = Color.Red, textDecoration = TextDecoration.Underline) }
                    )
                )
            }
            .build()

        // Verify annotations
        val hereAnnotations = annotatedString.getStringAnnotations("click_here", 0, annotatedString.length)
        val thereAnnotations = annotatedString.getStringAnnotations("click_there", 0, annotatedString.length)
        val nowAnnotations = annotatedString.getStringAnnotations("click_now", 0, annotatedString.length)

        assertEquals(1, hereAnnotations.size) // One "here"
        assertEquals(1, thereAnnotations.size) // One "there"
        assertEquals(1, nowAnnotations.size) // One "now"

        // Verify ranges and values
        assertTrue(hereAnnotations.any { it.start == boundaries1[0].first && it.end == boundaries1[0].last + 1 && it.item == "here" && it.tag == "click_here" })
        assertTrue(thereAnnotations.any { it.start == boundaries2[0].first && it.end == boundaries2[0].last + 1 && it.item == "there" && it.tag == "click_there" })
        assertTrue(nowAnnotations.any { it.start == boundaries3[0].first && it.end == boundaries3[0].last + 1 && it.item == "now" && it.tag == "click_now" })

        // Verify styles
        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.color == Color.Blue && it.start == boundaries1[0].first && it.end == boundaries1[0].last + 1 })
        assertEquals(1, spanStyles.count { it.item.color == Color.Green && it.start == boundaries2[0].first && it.end == boundaries2[0].last + 1 })
        assertEquals(1, spanStyles.count { it.item.color == Color.Red && it.start == boundaries3[0].first && it.end == boundaries3[0].last + 1 })

        assertEquals(testText, annotatedString.text)
    }

    @Test
    fun testUppercaseChunkTransformation() {
        val testText = "Click here, there, or now!"
        val boundaries1 = Utils.getRegexMatchRanges(testText, Regex("\\bhere\\b"))
        val boundaries2 = Utils.getRegexMatchRanges(testText, Regex("\\bthere\\b"))
        val boundaries3 = Utils.getRegexMatchRanges(testText, Regex("\\bnow\\b"))

        // Debug ranges
        println("here boundaries: $boundaries1")
        println("there boundaries: $boundaries2")
        println("now boundaries: $boundaries3")

        val annotatedString = EasySpansComposeBuilder.create(testText)
            .apply {
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("\\bhere\\b"),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "click_here",
                        styleBuilder = { it.copy(color = Color.Blue) }
                    )
                )
                addOccurrenceChunk(
                    occurrenceChunk(
                        occurrenceLocation = OccurrenceLocation(
                            delimitationType = DelimitationType.Regex("\\bthere\\b"),
                            occurrencePosition = OccurrencePosition.All
                        ),
                        onClickTag = "click_there",
                        styleBuilder = { it.copy(color = Color.Green) },
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
                        styleBuilder = { it.copy(color = Color.Red) }
                    )
                )
            }
            .build()

        // Verify text content
        assertEquals("Click here, THERE, or now!", annotatedString.text)

        // Verify annotations
        val hereAnnotations = annotatedString.getStringAnnotations("click_here", 0, annotatedString.length)
        val thereAnnotations = annotatedString.getStringAnnotations("click_there", 0, annotatedString.length)
        val nowAnnotations = annotatedString.getStringAnnotations("click_now", 0, annotatedString.length)

        assertEquals(1, hereAnnotations.size)
        assertEquals(1, thereAnnotations.size)
        assertEquals(1, nowAnnotations.size)

        assertTrue(hereAnnotations.any { it.start == boundaries1[0].first && it.end == boundaries1[0].last + 1 && it.item == "here" && it.tag == "click_here" })
        assertTrue(thereAnnotations.any { it.start == boundaries2[0].first && it.end == boundaries2[0].last + 1 && it.item == "THERE" && it.tag == "click_there" })
        assertTrue(nowAnnotations.any { it.start == boundaries3[0].first && it.end == boundaries3[0].last + 1 && it.item == "now" && it.tag == "click_now" })

        // Verify styles
        val spanStyles = annotatedString.spanStyles
        assertEquals(1, spanStyles.count { it.item.color == Color.Blue && it.start == boundaries1[0].first && it.end == boundaries1[0].last + 1 })
        assertEquals(1, spanStyles.count { it.item.color == Color.Green && it.start == boundaries2[0].first && it.end == boundaries2[0].last + 1 })
        assertEquals(1, spanStyles.count { it.item.color == Color.Red && it.start == boundaries3[0].first && it.end == boundaries3[0].last + 1 })
    }
}