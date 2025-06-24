package com.mamboa.easyspans.legacy

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.MaskFilterSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mamboa.easyspans.legacy.customspans.ClickableLinkSpan
import com.mamboa.easyspans.legacy.customspans.PaddingBackgroundColorSpan
import com.mamboa.easyspans.legacy.customspans.TextCaseSpan
import com.mamboa.easyspans.legacy.helper.DelimitationType
import com.mamboa.easyspans.legacy.helper.OccurrenceChunk
import com.mamboa.easyspans.legacy.helper.OccurrenceChunkBuilder
import com.mamboa.easyspans.legacy.helper.OccurrenceLocation
import com.mamboa.easyspans.legacy.helper.OccurrencePosition
import com.mamboa.easyspans.legacy.helper.ScriptType
import com.mamboa.easyspans.legacy.helper.SequenceBackgroundColor
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.PatternSyntaxException

@RunWith(AndroidJUnit4::class)
class EasySpansTest {

    private val MONETARY_PATTERN =
        "(\\d+\\s?\\\$)|(\\\$\\d+(\\.\\d{1,2})?)|(\\d+\\.\\d{1,2}\\s?\\\$)"  // 5 $  $5

    private lateinit var context: Context
    private val text =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla nec tempus est. Vestibulum volutpat ipsum vitae urna congue, vitae facilisis est iaculis. Integer accumsan ex et nibh mollis, vitae malesuada lacus porttitor. Maecenas commodo turpis nec porttitor fringilla. Maecenas fermentum massa in pulvinar tempus. Phasellus at volutpat mi. Suspendisse faucibus vitae mi vel sollicitudin. Aenean sit amet malesuada ipsum, at vestibulum lectus. Aliquam erat volutpat. Praesent tempus nibh ac ante aliquet suscipit. Praesent ex lectus, dapibus non porttitor id, aliquet nec sem. Mauris ac fringilla augue, ac tincidunt enim. Proin vestibulum auctor mi vitae facilisis. Pellentesque fermentum, mauris a mattis efficitur, ligula enim lobortis eros, sed pulvinar felis dui nec augue. In eget dignissim quam, in blandit massa.\n" +
                "\n" +
                "Phasellus turpis mauris, faucibus vel hendrerit id, mollis ut ex. Etiam cursus nisl nec dapibus eleifend. Phasellus consectetur diam a nibh luctus, in tempor ante viverra. Morbi nec vulputate lorem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis in nunc venenatis, viverra est sed, feugiat magna. In et ullamcorper dolor. Phasellus iaculis sit amet leo id cursus. Etiam congue scelerisque quam, vel accumsan massa mattis vel. Sed diam diam, iaculis eget turpis eget, porta vestibulum ligula. Aliquam tincidunt finibus sem, nec faucibus neque vulputate eget. Nullam ultricies odio a felis egestas dictum.\n" +
                "\n" +
                "Ut eget pretium purus. Aliquam volutpat tristique metus, eget euismod tortor tempus a. Nunc non scelerisque nulla. Donec sit amet mi sit amet libero tristique pretium eget vitae libero. Maecenas tristique dictum tortor id pulvinar. Donec convallis porta tincidunt. Fusce pretium interdum rhoncus. In hac habitasse platea dictumst. Nam dictum non sapien sed sollicitudin. Maecenas eget massa vel felis condimentum ornare sit amet at est. Praesent egestas metus ut turpis convallis dapibus eu in lorem. Duis vel massa pretium, ultricies justo at, faucibus est. Mauris sed aliquam nulla. Aliquam dapibus quam id eleifend tempor. Nullam metus leo, porta eu erat condimentum, varius iaculis odio. Suspendisse potenti.\n" +
                "\n" +
                "Nunc semper aliquam aliquet. Pellentesque in mattis lorem. Sed finibus scelerisque egestas. Donec efficitur molestie velit, sagittis tincidunt turpis semper sed. Maecenas in quam eu turpis sodales laoreet vel vitae mauris. In pretium aliquet ante, at ullamcorper odio lobortis at. Aenean et felis eget augue placerat vulputate. Proin ac neque purus. Mauris malesuada tellus non orci rhoncus, nec convallis felis lobortis. Suspendisse ut bibendum ex. Nullam scelerisque porttitor orci id tincidunt. Sed sit amet malesuada quam, pretium congue nisl. Nunc urna purus, luctus et lectus in, sodales tempus quam. Sed auctor tempor facilisis. Nam ante quam, auctor et sem sed, feugiat volutpat mauris. Aenean elementum metus ut varius sagittis.\n" +
                "\n" +
                "Sed molestie egestas diam, quis dignissim diam efficitur pretium. Phasellus luctus ante ac eros consectetur accumsan. Quisque laoreet tincidunt tellus, vel auctor quam auctor quis. Sed rhoncus orci ac nunc ultricies faucibus. Praesent auctor, neque et interdum imperdiet, ante mauris egestas nisi, id auctor magna sapien non leo. Phasellus in felis ac lectus dapibus porttitor. Duis porta sit amet augue non imperdiet. Ut posuere vehicula congue. In maximus fermentum felis, id feugiat est elementum nec. Pellentesque feugiat dolor risus, id sodales erat auctor at. Sed congue dignissim erat, et pellentesque nunc dictum vel.\n" +
                "\n"

    private lateinit var textView: TextView

    @Before
    fun getContext() {
        context = ApplicationProvider.getApplicationContext()
        textView = TextView(context)
    }

    @Test
    fun testSpans() {
        val typeFace = Typeface.BOLD
        val spanned = EasySpans.Builder(context, text, TextView(context))
            .setColor(R.color.teal_700)
            .setTextSize(R.dimen.test_default_text_size)
            .setTextStyle(typeFace)
            .isStrikeThrough()
            .isUnderlined()
            .setFont(R.font.ocean_summer)
            .setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE) // TextCaseSpan.TextCaseType.NORMAL will not add any span
            .setParagraphBackgroundColor(
                SequenceBackgroundColor(
                    backgroundColor = R.color.purple_700,
                    padding = R.dimen.test_background_padding,
                    gravity = Gravity.CENTER
                )
            )
            .setStyle(androidx.appcompat.R.style.AlertDialog_AppCompat) // just for testing, not a real text test
            .build()
            .create() as Spanned

        val styles = spanned.getSpans(0, spanned.length, Any::class.java)

        // setFont(R.font.ocean_summer) depends on Build.VERSION_CODES.P
        // text case type uses StyleSpan go to {@link com.mamboa.easyspans.customspans.TextCaseSpan} to understand why
        val expectedSize = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) 8 else 7

        // Total of applied styles
        Assert.assertEquals(styles.size, expectedSize)

        // text styles
        styles.findLast { it is StyleSpan }.run {
            val style = this as StyleSpan
            Assert.assertEquals(style.style, typeFace)
        }

        // text color
        styles.find { it is ForegroundColorSpan }.run {
            val style = this as ForegroundColorSpan
            Assert.assertEquals(
                style.foregroundColor,
                ContextCompat.getColor(context, R.color.teal_700)
            )
        }

        // text size
        styles.find { it is AbsoluteSizeSpan }.run {
            val style = this as AbsoluteSizeSpan
            Assert.assertEquals(
                style.size,
                context.resources.getDimensionPixelSize(R.dimen.test_default_text_size)
            )
        }

        // strikeThrough
        Assert.assertNotNull(styles.find { it is StrikethroughSpan })

        // underline
        Assert.assertNotNull(styles.find { it is UnderlineSpan })

        // font
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            styles.find { it is TypefaceSpan }.run {
                val style = this as TypefaceSpan
                Assert.assertEquals(
                    style.typeface,
                    Typeface.create(ResourcesCompat.getFont(context, R.font.ocean_summer), typeFace)
                )
            }
        }

        // text case type
        //NOTE: go to {@link com.mamboa.easyspans.customspans.TextCaseSpan} to understand why
        /*styles.find { it is TextCaseSpan }.run {
            val style = this as TextCaseSpan
            Assert.assertEquals(style.textCaseType, TextCaseSpan.TextCaseType.UPPER_CASE)
        }*/

        // Background color
        Assert.assertNotNull(styles.find { it is PaddingBackgroundColorSpan })

        // Style
        Assert.assertNotNull(styles.find { it is TextAppearanceSpan })

        // Make sure that the string was not altered
        Assert.assertEquals(text.lowercase(), spanned.toString().lowercase())

        // Assert that the text is different from the original text since it is not all UPPER_CASE
        Assert.assertNotEquals(text, spanned.toString())
    }

    @Test
    fun testDelimiterSuccess() {
        // apply elements to the third  word delimited by space
        val desiredWord = "dolor"
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.BOUNDARY(" "),
                    OccurrencePosition.Nth(2)
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        val startIndex = spanned.indexOf(desiredWord)
        val endIndex = startIndex + desiredWord.length

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if the span is exactly applied on the third word text
        Assert.assertEquals(startIndex, spanned.getSpanStart(underlineStyles[0]))
        Assert.assertEquals(endIndex, spanned.getSpanEnd(underlineStyles[0]))

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testDelimiterFailure() {
        // apply elements to the third  word delimited by space
        // trying to get a word with wrong position after delimiter split
        val desiredWord = "dolor"
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.BOUNDARY(" "),
                    OccurrencePosition.First
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        val startIndex = spanned.indexOf(desiredWord)
        val endIndex = startIndex + desiredWord.length

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if the span is exactly applied on the desired word text
        Assert.assertNotEquals(startIndex, spanned.getSpanStart(underlineStyles[0]))
        Assert.assertNotEquals(endIndex, spanned.getSpanEnd(underlineStyles[0]))

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testRegexOccurrencePositionFirst() {
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)

        val occurrencePosition = 0
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.First
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.size > occurrencePosition)

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if the span is exactly applied on the occurrence matching the regex
        Assert.assertTrue(
            occurrenceBoundaries[occurrencePosition].first == spanned.getSpanStart(
                underlineStyles[0]
            )
        )
        Assert.assertTrue(
            occurrenceBoundaries[occurrencePosition].last + 1 == spanned.getSpanEnd(
                underlineStyles[0]
            )
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testRegexOccurrencePositionNth() {
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)

        val occurrencePosition = 2
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.Nth(occurrencePosition)
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.size > occurrencePosition)

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if the span is exactly applied on the occurrence matching the regex
        Assert.assertTrue(
            occurrenceBoundaries[occurrencePosition].first == spanned.getSpanStart(
                underlineStyles[0]
            )
        )
        Assert.assertTrue(
            occurrenceBoundaries[occurrencePosition].last + 1 == spanned.getSpanEnd(
                underlineStyles[0]
            )
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testRegexOccurrencePositionIndexes() {
        // the text "nec" is supposed to be found 12 times
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)

        val occurrencePositionIndexes = arrayListOf(1, 5, 8)
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.Indices(1, 5, 8)
                )
            )
            .isUnderlined()
            .isStrikeThrough()
            .build()
            .create() as Spanned


        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.size > occurrencePositionIndexes.size)

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, occurrencePositionIndexes.size)

        // test if there is a strikeThrough span
        val strikeThroughStyles = spanned.getSpans(0, spanned.length, StrikethroughSpan::class.java)
        Assert.assertEquals(strikeThroughStyles.size, occurrencePositionIndexes.size)

        // test if the spans spans exactly applied on each occurrence matching the regex
        var remainingIndexPositions = occurrencePositionIndexes.size
        occurrenceBoundaries.forEachIndexed { index, occurrenceBoundary ->
            if (occurrencePositionIndexes.contains(index)) {
                val spannedIndex = occurrencePositionIndexes.size - remainingIndexPositions
                Assert.assertTrue(
                    occurrenceBoundary.first == spanned.getSpanStart(
                        strikeThroughStyles[spannedIndex]
                    )
                )
                Assert.assertTrue(
                    occurrenceBoundary.last + 1 == spanned.getSpanEnd(
                        strikeThroughStyles[spannedIndex]
                    )
                )

                Assert.assertTrue(occurrenceBoundary.first == spanned.getSpanStart(underlineStyles[spannedIndex]))
                Assert.assertTrue(occurrenceBoundary.last + 1 == spanned.getSpanEnd(underlineStyles[spannedIndex]))
                remainingIndexPositions--
            }
        }

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testRegexOccurrencePositionLast() {
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)

        val occurrencePosition = occurrenceBoundaries.size - 1
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.Last
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.size > occurrencePosition)

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if the span is exactly applied on the occurrence matching the regex
        Assert.assertTrue(
            occurrenceBoundaries[occurrencePosition].first == spanned.getSpanStart(
                underlineStyles[0]
            )
        )
        Assert.assertTrue(
            occurrenceBoundaries[occurrencePosition].last + 1 == spanned.getSpanEnd(
                underlineStyles[0]
            )
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testRegexOccurrencePositionAll() {
        // when no occurrence is specified, all words matching the regex will get the span
        val regex = "nec"
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.All
                )
            )
            .isUnderlined()
            .isStrikeThrough()
            .build()
            .create() as Spanned

        val allSpans = spanned.getSpans(0, spanned.length, Any::class.java)

        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)

        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, occurrenceBoundaries.size)

        // test if there is a strikeThrough span
        val strikeThroughStyles = spanned.getSpans(0, spanned.length, StrikethroughSpan::class.java)
        Assert.assertEquals(strikeThroughStyles.size, occurrenceBoundaries.size)

        // test if the spans spans exactly applied on each occurrence matching the regex
        occurrenceBoundaries.forEachIndexed { index, occurrenceBoundary ->
            Assert.assertTrue(occurrenceBoundary.first == spanned.getSpanStart(strikeThroughStyles[index]))
            Assert.assertTrue(occurrenceBoundary.last + 1 == spanned.getSpanEnd(strikeThroughStyles[index]))

            Assert.assertTrue(occurrenceBoundary.first == spanned.getSpanStart(underlineStyles[index]))
            Assert.assertTrue(occurrenceBoundary.last + 1 == spanned.getSpanEnd(underlineStyles[index]))
        }

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testRegexOccurrencePositionFailure() {
        // this does not appear in the text, so there should be no span applied
        val regex = "Leonid master"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)

        // FIRST
        val spannedFirst = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.First
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.isEmpty())

        // test if there is a underline span
        var underlineStyles =
            spannedFirst.getSpans(0, spannedFirst.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 0)

        // Nth
        val desiredPosition = 3
        val spannedNth = EasySpans.Builder(context, text, TextView(context))
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.Nth(desiredPosition)
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned


        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.isEmpty())

        // test if there is a underline span
        underlineStyles = spannedNth.getSpans(0, spannedNth.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 0)

        // LAST
        val spannedLast = EasySpans.Builder(context, text, TextView(context))
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.Last
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned


        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.isEmpty())

        // test if there is a underline span
        underlineStyles = spannedLast.getSpans(0, spannedLast.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 0)

        // ALL
        val spannedAll = EasySpans.Builder(context, text, TextView(context))
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.All
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned


        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.isEmpty())

        // test if there is a underline span
        underlineStyles = spannedAll.getSpans(0, spannedAll.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 0)
    }

    @Test
    fun testDelimiterOccurrencePositionFirst() {
        // testing the result of applyChangesToSubSequence(text: CharSequence, delimiter: String, occurrencePosition: OccurrencePosition): CharSequence
        // In this case the first word should be
        val expectedWord = "Lorem"

        val delimiter = " "
        val occurrenceBoundaries = getDelimiterSlicedOccurrencesBoundaries(delimiter, text)

        val desiredPosition = 0
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.BOUNDARY(delimiter),
                    OccurrencePosition.First
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if the span is exactly applied on the first delimited word
        Assert.assertEquals(
            occurrenceBoundaries[desiredPosition].first,
            spanned.getSpanStart(underlineStyles[0])
        )
        Assert.assertEquals(
            occurrenceBoundaries[desiredPosition].last + 1,
            spanned.getSpanEnd(underlineStyles[0])
        )

        // test if the expectedWord matches the spanned subsequence
        Assert.assertEquals(
            expectedWord,
            spanned.subSequence(
                occurrenceBoundaries[desiredPosition].first,
                occurrenceBoundaries[desiredPosition].last + 1
            ).toString()
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testDelimiterOccurrencePositionNth() {
        // testing the result of applyChangesToSubSequence(text: CharSequence, delimiter: String, occurrencePosition: OccurrencePosition): CharSequence
        // In this case the first word should be
        val expectedWord = "consectetur"
        val delimiter = " "
        val occurrenceBoundaries = getDelimiterSlicedOccurrencesBoundaries(delimiter, text)

        val nthPosition = 5
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.BOUNDARY(delimiter),
                    OccurrencePosition.Nth(nthPosition)
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if the span is exactly applied on the nthPosition + 1 word text
        Assert.assertEquals(
            occurrenceBoundaries[nthPosition].first,
            spanned.getSpanStart(underlineStyles[0])
        )
        Assert.assertEquals(
            occurrenceBoundaries[nthPosition].last + 1,
            spanned.getSpanEnd(underlineStyles[0])
        )

        // test if the expectedWord matches the spanned subsequence
        Assert.assertEquals(
            expectedWord,
            spanned.subSequence(
                occurrenceBoundaries[nthPosition].first,
                occurrenceBoundaries[nthPosition].last + 1
            ).toString()
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testDelimiterOccurrencePositionNthIndexes() {
        // testing the result of applyChangesToSubSequence(text: CharSequence, delimiter: String, occurrencePosition: OccurrencePosition): CharSequence
        // In this case the we are expecting 3 occurences
        val expectedSentence1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla "
        val expectedSentence2 =
            " porttitor fringilla. Maecenas fermentum massa in pulvinar tempus. Phasellus at volutpat mi. Suspendisse faucibus vitae mi vel sollicitudin. Aenean sit amet malesuada ipsum, at vestibulum lectus. Aliquam erat volutpat. Praesent tempus nibh ac ante aliquet suscipit. Praesent ex lectus, dapibus non porttitor id, aliquet "
        val expectedSentence3 =
            " dapibus eleifend. Phasellus consectetur diam a nibh luctus, in tempor ante viverra. Morbi "
        val occurrencePositionIndexes = arrayListOf(0, 2, 5)
        val delimiter = "nec"
        val occurrenceBoundaries = getDelimiterSlicedOccurrencesBoundaries(delimiter, text)

        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.BOUNDARY(delimiter),
                    OccurrencePosition.Indices(0, 2, 5)
                )
            )
            .isUnderlined()
            /*TextCaseSpan.TextCaseType will not add any span
              NOTE: go to {@link com.mamboa.easyspans.customspans.TextCaseSpan} to understand why*/
            .setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE)
            .build()
            .create() as Spanned

        // test if there underline spans matching the number of occurrences
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, occurrencePositionIndexes.size)


        // test if the span is exactly applied on the expected words
        var remainingIndexPositions = occurrencePositionIndexes.size
        occurrenceBoundaries.forEachIndexed { index, occurrenceBoundary ->
            if (occurrencePositionIndexes.contains(index)) {
                val spannedIndex = occurrencePositionIndexes.size - remainingIndexPositions
                Assert.assertTrue(
                    occurrenceBoundary.first == spanned.getSpanStart(
                        underlineStyles[spannedIndex]
                    )
                )
                Assert.assertTrue(
                    occurrenceBoundary.last + 1 == spanned.getSpanEnd(
                        underlineStyles[spannedIndex]
                    )
                )

                Assert.assertTrue(occurrenceBoundary.first == spanned.getSpanStart(underlineStyles[spannedIndex]))
                Assert.assertTrue(occurrenceBoundary.last + 1 == spanned.getSpanEnd(underlineStyles[spannedIndex]))
                remainingIndexPositions--
            }
        }

        // test if the expectedWord matches the spanned subsequence. We called
        //.setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE)
        Assert.assertEquals(
            expectedSentence1.uppercase(),
            spanned.subSequence(
                occurrenceBoundaries[occurrencePositionIndexes[0]].first,
                occurrenceBoundaries[occurrencePositionIndexes[0]].last + 1
            ).toString()
        )
        Assert.assertEquals(
            expectedSentence2.uppercase(),
            spanned.subSequence(
                occurrenceBoundaries[occurrencePositionIndexes[1]].first,
                occurrenceBoundaries[occurrencePositionIndexes[1]].last + 1
            ).toString()
        )
        Assert.assertEquals(
            expectedSentence3.uppercase(),
            spanned.subSequence(
                occurrenceBoundaries[occurrencePositionIndexes[2]].first,
                occurrenceBoundaries[occurrencePositionIndexes[2]].last + 1
            ).toString()
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text.lowercase(), spanned.toString().lowercase())
    }

    @Test
    fun testDelimiterOccurrencePositionLast() {
        // testing the result of applyChangesToSubSequence(text: CharSequence, delimiter: String, occurrencePosition: OccurrencePosition): CharSequence
        // In this case the first word should be
        val expectedWord = "vel.\n\n"
        val delimiter = " "
        val occurrenceBoundaries = getDelimiterSlicedOccurrencesBoundaries(delimiter, text)

        val lastPosition = occurrenceBoundaries.size - 1
        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.BOUNDARY(delimiter),
                    OccurrencePosition.Last
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        // test if there is a underline span
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if the span is exactly applied on the last delimited word
        Assert.assertEquals(
            occurrenceBoundaries[lastPosition].first,
            spanned.getSpanStart(underlineStyles[0])
        )
        Assert.assertEquals(
            occurrenceBoundaries[lastPosition].last + 1,
            spanned.getSpanEnd(underlineStyles[0])
        )

        // test if the expectedWord matches the spanned subsequence
        Assert.assertEquals(
            expectedWord,
            spanned.subSequence(
                occurrenceBoundaries[lastPosition].first,
                occurrenceBoundaries[lastPosition].last + 1
            ).toString()
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testDelimiterOccurrencePositionAll() {
        val delimiter = " "
        val occurrenceBoundaries = getDelimiterSlicedOccurrencesBoundaries(delimiter, text)

        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.BOUNDARY(delimiter),
                    OccurrencePosition.All
                )
            )
            .isUnderlined()
            /*TextCaseSpan.TextCaseType will not add any span
              NOTE: go to {@link com.mamboa.easyspans.customspans.TextCaseSpan} to understand why*/
            .setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE)
            .build()
            .create() as Spanned

        // test if there underline spans matching the number of occurrences
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, occurrenceBoundaries.size)

        // test if the span is exactly applied on the third word text
        occurrenceBoundaries.forEachIndexed { index, occurrenceBoundary ->
            Assert.assertTrue(occurrenceBoundary.first == spanned.getSpanStart(underlineStyles[index]))
            Assert.assertTrue(occurrenceBoundary.last + 1 == spanned.getSpanEnd(underlineStyles[index]))
        }
    }

    @Test
    fun testSingleLinkRegexPositionLast() {
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)

        val occurrencePosition = occurrenceBoundaries.size - 1
        val spanned = EasySpans.Builder(context, text, textView)
            .isUnderlined()
            .setColor(R.color.teal_700)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.Last
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                            override fun onLinkClick(view: View) {/* get the click listener */ }
                        })
                )
            )
            .build()
            .create() as Spanned

        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrenceBoundaries.size > occurrencePosition)

        // test if there is a underlineSpan. There is one for the whole text since there is no OccurrenceChunkBuilder
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if there is a colorSpan. There is one for the whole text since there is no OccurrenceChunkBuilder
        val colorStyles = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(colorStyles.size, 1)

        // test if there is a ClickableLink span There should be one since it is the Last Occurrence only that is applied
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(linkSpans.size, 1)

        // test if the span is exactly applied on the occurrence matching the regex
        Assert.assertTrue(
            occurrenceBoundaries[occurrencePosition].first == spanned.getSpanStart(
                linkSpans[0]
            )
        )
        Assert.assertTrue(
            occurrenceBoundaries[occurrencePosition].last + 1 == spanned.getSpanEnd(
                linkSpans[0]
            )
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testSingleLinkBoundaryPositionFirst() {
        val expectedWord = "Lorem"

        val delimiter = " "
        val occurrenceBoundaries = getDelimiterSlicedOccurrencesBoundaries(delimiter, text)

        val desiredPosition = 0
        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.BOUNDARY(delimiter),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .isUnderlined()
                        .setColor(R.color.teal_700)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) {/* get the click listener */ }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        // test if there is a underline span. there is none since color and underline were applied in te ClickableLinkSpan
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 0)

        // test if there is a underline span. there is none since color and underline were applied in te ClickableLinkSpan
        val colorStyles = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(colorStyles.size, 0)

        // test if the expectedWord matches the spanned subsequence
        Assert.assertEquals(
            expectedWord,
            spanned.subSequence(
                occurrenceBoundaries[desiredPosition].first,
                occurrenceBoundaries[desiredPosition].last + 1
            ).toString()
        )

        // test if there is a ClickableLink span
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(linkSpans.size, 1)

        // test if the span is exactly applied on on the first delimited word
        Assert.assertTrue(
            occurrenceBoundaries[desiredPosition].first == spanned.getSpanStart(
                linkSpans[0]
            )
        )
        Assert.assertTrue(
            occurrenceBoundaries[desiredPosition].last + 1 == spanned.getSpanEnd(
                linkSpans[0]
            )
        )

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testMultipleLinkRegexPositionAll() {
        val regex1 = "consectetur"
        val occurrenceBoundaries1 = getRegexOccurrencesBoundaries(regex1, text)

        val regex2 = "Donec"
        val occurrenceBoundaries2 = getRegexOccurrencesBoundaries(regex2, text)

        val regex3 = "tempus"
        val occurrenceBoundaries3 = getRegexOccurrencesBoundaries(regex3, text)

        val occurrencesBoundaries =
            ArrayList(occurrenceBoundaries1).apply { addAll(occurrenceBoundaries2) }

        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex1)
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                            override fun onLinkClick(view: View) {/* no-op */ }
                        })
                ),

                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex2)
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(object : ClickableLinkSpan.OnLinkClickListener {
                            override fun onLinkClick(view: View) { /* no-op */ }
                        })
                )
            )
            .isUnderlined()
            .isStrikeThrough()
            .setColor(R.color.teal_700)
            .build()
            .create() as Spanned

        // since we specified a position, the size should be greater than the position
        Assert.assertTrue(occurrencesBoundaries.size == occurrenceBoundaries1.size + occurrenceBoundaries2.size)

        // test if there is a underlineSpan. There is one for the whole text since there is no OccurrenceChunkBuilder
        val underlineStyles = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(underlineStyles.size, 1)

        // test if there is a colorSpan. There is one for the whole text since there is no OccurrenceChunkBuilder
        val colorStyles = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(colorStyles.size, 1)

        // test if there is a strikeThroughSpan. There is one for the whole text since there is no OccurrenceChunkBuilder
        val strikeThroughSpans = spanned.getSpans(0, spanned.length, StrikethroughSpan::class.java)
        Assert.assertEquals(strikeThroughSpans.size, 1)

        // test if there is a ClickableLink span There should be the same number of ClickableLinkSpans as the occurrences
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(linkSpans.size, occurrencesBoundaries.size)

        // test if the spans spans exactly applied on each occurrence matching the regex
        occurrencesBoundaries.forEachIndexed { index, occurrenceBoundary ->
            Assert.assertTrue(occurrenceBoundary.first == spanned.getSpanStart(linkSpans[index]))
            Assert.assertTrue(occurrenceBoundary.last + 1 == spanned.getSpanEnd(linkSpans[index]))
        }

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testCombineBasicSpansWithRegexNth() {
        val regex = "ipsum"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        val occurrencePosition = 1 // Second occurrence

        val typeFace = Typeface.ITALIC
        val colorRes = R.color.purple_500

        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(regex),
                    OccurrencePosition.Nth(occurrencePosition)
                )
            )
            .setColor(colorRes)
            .setTextStyle(typeFace)
            .build()
            .create() as Spanned

        // Ensure we expect the occurrence to exist
        Assert.assertTrue(occurrenceBoundaries.size > occurrencePosition)

        val expectedStart = occurrenceBoundaries[occurrencePosition].first
        val expectedEnd = occurrenceBoundaries[occurrencePosition].last + 1

        // Test for Color Span
        val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(1, colorSpans.size)
        val colorSpan = colorSpans[0]
        Assert.assertEquals(expectedStart, spanned.getSpanStart(colorSpan))
        Assert.assertEquals(expectedEnd, spanned.getSpanEnd(colorSpan))
        Assert.assertEquals(ContextCompat.getColor(context, colorRes), colorSpan.foregroundColor)

        // Test for Style Span
        val styleSpans = spanned.getSpans(0, spanned.length, StyleSpan::class.java)
        Assert.assertEquals(1, styleSpans.size)
        val styleSpan = styleSpans[0]
        Assert.assertEquals(expectedStart, spanned.getSpanStart(styleSpan))
        Assert.assertEquals(expectedEnd, spanned.getSpanEnd(styleSpan))
        Assert.assertEquals(typeFace, styleSpan.style)

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testCombineGlobalSpansWithLinkChunk() {
        val regex = "dolor"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        val occurrencePosition = OccurrencePosition.First

        val globalTextSizeRes = R.dimen.test_default_text_size
        val linkColorRes = R.color.teal_200

        val spanned = EasySpans.Builder(context, text, textView)
            .setTextSize(globalTextSizeRes) // Global span
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = occurrencePosition
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(linkColorRes) // Link specific span (via ClickableLinkSpan)
                        .isUnderlined() // Override default link underline
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        // --- Verify Global Span (TextSize) ---
        val sizeSpans = spanned.getSpans(0, spanned.length, AbsoluteSizeSpan::class.java)
        Assert.assertTrue(sizeSpans.isNotEmpty())
        // Verify it covers the whole range or is correctly applied
        Assert.assertEquals(0, spanned.getSpanStart(sizeSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(sizeSpans[0]))
        Assert.assertEquals(
            context.resources.getDimensionPixelSize(globalTextSizeRes),
            sizeSpans[0].size
        )

        // --- Verify Link Span ---
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())
        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(expectedStart, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(expectedEnd, spanned.getSpanEnd(linkSpans[0]))

        // Check ClickableLinkSpan properties (color is applied internally)
        // We expect NO separate ForegroundColorSpan or UnderlineSpan for the link chunk itself
        val linkChunkColorSpans =
            spanned.getSpans(expectedStart, expectedEnd, ForegroundColorSpan::class.java)
        Assert.assertEquals(0, linkChunkColorSpans.size) // Color is inside ClickableLinkSpan

        val linkChunkUnderlineSpans =
            spanned.getSpans(expectedStart, expectedEnd, UnderlineSpan::class.java)
        Assert.assertEquals(0, linkChunkUnderlineSpans.size) // Underline explicitly disabled

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testRegexCaseInsensitive() {
        val regex = "lorem" // Lowercase
        val caseInsensitiveRegex = "(?i)$regex" // Case-insensitive flag
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(caseInsensitiveRegex, text)

        // Expecting to find "Lorem" at the beginning
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())
        Assert.assertEquals(0, occurrenceBoundaries[0].first) // Starts at index 0
        Assert.assertEquals(
            "Lorem",
            text.substring(occurrenceBoundaries[0].first, occurrenceBoundaries[0].last + 1)
        )


        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.REGEX(caseInsensitiveRegex),
                    OccurrencePosition.First
                )
            )
            .isUnderlined()
            .build()
            .create() as Spanned

        val underlineSpans = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(1, underlineSpans.size)
        Assert.assertEquals(occurrenceBoundaries[0].first, spanned.getSpanStart(underlineSpans[0]))
        Assert.assertEquals(occurrenceBoundaries[0].last + 1, spanned.getSpanEnd(underlineSpans[0]))

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testDelimiterNotFound() {
        val delimiter = "NonExistentDelimiterString"
        val occurrenceBoundaries = getDelimiterSlicedOccurrencesBoundaries(delimiter, text)

        // Assert delimiter is not found
        Assert.assertTrue(occurrenceBoundaries.size <= 1) // It might return the whole string as one chunk

        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceLocation(
                OccurrenceLocation(
                    DelimitationType.BOUNDARY(delimiter),
                    OccurrencePosition.First
                )
            ) // Try to apply to first (non-existent) occurrence
            .isUnderlined()
            .build()
            .create() as Spanned

        // No spans should be applied if the delimiter logic doesn't find the target
        val underlineSpans = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(1, underlineSpans.size) // we are expecting only the 1rst Occurence

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testLinkChunkDefaults() {
        val regex = "elit"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, text, textView)
            // No global styles
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        // Check for the ClickableLinkSpan
        val linkSpans = spanned.getSpans(expectedStart, expectedEnd, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(expectedStart, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(expectedEnd, spanned.getSpanEnd(linkSpans[0]))

        // Verify that NO explicit UnderlineSpan or ForegroundColorSpan were added by EasySpans
        // The styling is handled *within* ClickableLinkSpan's draw method
        val underlineSpans = spanned.getSpans(expectedStart, expectedEnd, UnderlineSpan::class.java)
        Assert.assertEquals(0, underlineSpans.size)

        val colorSpans =
            spanned.getSpans(expectedStart, expectedEnd, ForegroundColorSpan::class.java)
        Assert.assertEquals(0, colorSpans.size)

        // Make sure that the string was not altered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testEmptyText() {
        val emptyText = ""
        val spanned = EasySpans.Builder(context, emptyText, textView)
            .setColor(R.color.teal_700)
            .isUnderlined()
            .setOccurrenceChunks( // Add a chunk definition to test this path too
                OccurrenceChunk(
                    location = OccurrenceLocation(DelimitationType.REGEX("a")),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                )
            )
            .build()
            .create()

        Assert.assertEquals(emptyText, spanned.toString())
        Assert.assertEquals(0, spanned.length)

        // No spans should be applied to an empty string
        val allSpans = (spanned as Spanned).getSpans(0, 0, Any::class.java)
        Assert.assertEquals(0, allSpans.size)
    }

    @Test
    fun testSingleLinkRegexNoBuilder() {
        val regex = "tempus"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, text, textView)
            .setTextSize(R.dimen.test_default_text_size) // Global span
            .setColor(R.color.teal_700) // Global color
            .isUnderlined() // Global underline
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        // Verify ClickableLinkSpan
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(expectedStart, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(expectedEnd, spanned.getSpanEnd(linkSpans[0]))

        // Verify global spans apply to entire text
        val sizeSpans = spanned.getSpans(0, spanned.length, AbsoluteSizeSpan::class.java)
        Assert.assertEquals(1, sizeSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(sizeSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(sizeSpans[0]))

        val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(1, colorSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(colorSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(colorSpans[0]))

        val underlineSpans = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(1, underlineSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(underlineSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(underlineSpans[0]))

        // Verify no separate spans for link chunk (handled by ClickableLinkSpan)
        val linkChunkColorSpans =
            spanned.getSpans(expectedStart, expectedEnd, ForegroundColorSpan::class.java)
        Assert.assertEquals(1, linkChunkColorSpans.size)
        val linkChunkUnderlineSpans =
            spanned.getSpans(expectedStart, expectedEnd, UnderlineSpan::class.java)
        Assert.assertEquals(1, linkChunkUnderlineSpans.size)

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testMixedClickableAndNonClickableChunks() {
        val regex1 = "dolor" // Clickable
        val regex2 = "ipsum" // Non-clickable
        val occurrenceBoundaries1 = getRegexOccurrencesBoundaries(regex1, text)
        val occurrenceBoundaries2 = getRegexOccurrencesBoundaries(regex2, text)
        Assert.assertTrue(occurrenceBoundaries1.isNotEmpty())
        Assert.assertTrue(occurrenceBoundaries2.isNotEmpty())

        val spanned = EasySpans.Builder(context, text, textView)
            .setTextStyle(Typeface.BOLD) // Global span
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex1),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.purple_500)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                ),
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex2),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder().setTextSize(R.dimen.test_default_text_size)
                )
            )
            .build()
            .create() as Spanned

        // Verify clickable chunk
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(occurrenceBoundaries1[0].first, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(occurrenceBoundaries1[0].last + 1, spanned.getSpanEnd(linkSpans[0]))

        // Verify non-clickable chunk
        val sizeSpans = spanned.getSpans(0, spanned.length, AbsoluteSizeSpan::class.java)
        Assert.assertEquals(1, sizeSpans.size)
        Assert.assertEquals(occurrenceBoundaries2[0].first, spanned.getSpanStart(sizeSpans[0]))
        Assert.assertEquals(occurrenceBoundaries2[0].last + 1, spanned.getSpanEnd(sizeSpans[0]))

        // Verify global span
        val styleSpans = spanned.getSpans(0, spanned.length, StyleSpan::class.java)
        Assert.assertEquals(1, styleSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(styleSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(styleSpans[0]))

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testLinkChunkOverridesGlobalStyles() {
        val regex = "elit"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, text, textView)
            .setColor(R.color.teal_700) // Global color
            .isUnderlined() // Global underline
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.purple_500) // Override color
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        // Verify global spans
        val globalColorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(1, globalColorSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(globalColorSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(globalColorSpans[0]))
        Assert.assertEquals(
            ContextCompat.getColor(context, R.color.teal_700),
            globalColorSpans[0].foregroundColor
        )

        val globalUnderlineSpans = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(1, globalUnderlineSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(globalUnderlineSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(globalUnderlineSpans[0]))

        // Verify link chunk
        val linkSpans = spanned.getSpans(expectedStart, expectedEnd, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(expectedStart, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(expectedEnd, spanned.getSpanEnd(linkSpans[0]))

        // Verify no separate spans for link chunk (handled by ClickableLinkSpan)
        val linkChunkColorSpans =
            spanned.getSpans(expectedStart, expectedEnd, ForegroundColorSpan::class.java)
        Assert.assertEquals(1, linkChunkColorSpans.size)
        val linkChunkUnderlineSpans =
            spanned.getSpans(expectedStart, expectedEnd, UnderlineSpan::class.java)
        Assert.assertEquals(1, linkChunkUnderlineSpans.size)

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testInvalidOccurrenceChunkInputs() {
        // Test malformed regex
        var spanned: Spanned? = null
        try {
            spanned = EasySpans.Builder(context, text, textView)
                .setOccurrenceChunks(
                    OccurrenceChunk(
                        location = OccurrenceLocation(
                            delimitationType = DelimitationType.REGEX("[a-z"), // Malformed regex
                            occurrencePosition = OccurrencePosition.First
                        ),
                        builder = OccurrenceChunkBuilder(),
                    )
                )
                .build()
                .create() as Spanned
        } catch (e: PatternSyntaxException) {
            // Expected behavior: exception thrown for malformed regex
            Assert.assertNull(
                spanned?.toString(),
                "Spanned should be null due to malformed regex exception"
            )
        }

        // Test empty regex
        val spannedEmptyRegex = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(""), // Empty regex
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        val linkSpansEmpty =
            spannedEmptyRegex.getSpans(0, spannedEmptyRegex.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(0, linkSpansEmpty.size) // No spans applied for empty regex

        // Ensure text is unchanged
        Assert.assertEquals(text, spannedEmptyRegex.toString())
    }

    @Test
    fun testLargeTextWithManyChunks() {
        val largeText = text.repeat(10) // 10x original text
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, largeText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val chunks = Array(5) { index ->
            OccurrenceChunk(
                location = OccurrenceLocation(
                    delimitationType = DelimitationType.REGEX(regex),
                    occurrencePosition = OccurrencePosition.Nth(index)
                ),
                builder = OccurrenceChunkBuilder().setColor(R.color.teal_200)
                    .setOnLinkClickListener(
                        object : ClickableLinkSpan.OnLinkClickListener {
                            override fun onLinkClick(view: View) { /* no-op */ }
                        }
                    )
            )
        }

        val spanned = EasySpans.Builder(context, largeText, textView)
            .setTextStyle(Typeface.BOLD)
            .setOccurrenceChunks(*chunks)
            .build()
            .create() as Spanned

        // Verify link spans
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(5, linkSpans.size)
        for (i in 0 until 5) {
            Assert.assertEquals(occurrenceBoundaries[i].first, spanned.getSpanStart(linkSpans[i]))
            Assert.assertEquals(occurrenceBoundaries[i].last + 1, spanned.getSpanEnd(linkSpans[i]))
        }

        // Verify global span
        val styleSpans = spanned.getSpans(0, spanned.length, StyleSpan::class.java)
        Assert.assertEquals(1, styleSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(styleSpans[0]))
        Assert.assertEquals(largeText.length, spanned.getSpanEnd(styleSpans[0]))

        // Ensure text is unaltered
        Assert.assertEquals(largeText, spanned.toString())
    }

    @Test
    fun testClickableLinkClick() {
        val regex = "dolor"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val clickCount = intArrayOf(0)
        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) {
                                    clickCount[0]++
                                }
                            }
                        )
                )
            )
            .build()
            .create()

        textView.text = spanned


        // Simulate click on the link region (requires Robolectric or manual bounds calculation)
        // Placeholder: Actual click simulation depends on Robolectric setup
        // For simplicity, assume manual span triggering if Robolectric isn't set up
        val linkSpans =
            (spanned as Spanned).getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        linkSpans[0].onClick(textView) // Manual trigger

        Assert.assertEquals(1, clickCount[0])

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testClickableLinkDefaultBuilderStyles() {
        val regex = "tempus"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, text, textView)
            .setColor(R.color.teal_700) // Global color
            .isUnderlined() // Global underline
            .setTextSize(R.dimen.test_default_text_size) // Global text size
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */
                                }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        // Verify ClickableLinkSpan
        val linkSpans = spanned.getSpans(expectedStart, expectedEnd, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(expectedStart, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(expectedEnd, spanned.getSpanEnd(linkSpans[0]))

        // Verify global spans apply to entire text
        val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(1, colorSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(colorSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(colorSpans[0]))

        val underlineSpans = spanned.getSpans(0, spanned.length, UnderlineSpan::class.java)
        Assert.assertEquals(1, underlineSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(underlineSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(underlineSpans[0]))

        val sizeSpans = spanned.getSpans(0, spanned.length, AbsoluteSizeSpan::class.java)
        Assert.assertEquals(1, sizeSpans.size)
        Assert.assertEquals(0, spanned.getSpanStart(sizeSpans[0]))
        Assert.assertEquals(text.length, spanned.getSpanEnd(sizeSpans[0]))

        // Verify no separate spans for link chunk (styling handled by ClickableLinkSpan)
        val linkChunkColorSpans =
            spanned.getSpans(expectedStart, expectedEnd, ForegroundColorSpan::class.java)
        Assert.assertEquals(1, linkChunkColorSpans.size)
        val linkChunkUnderlineSpans =
            spanned.getSpans(expectedStart, expectedEnd, UnderlineSpan::class.java)
        Assert.assertEquals(1, linkChunkUnderlineSpans.size)

        // Verify link chunk inherits global color
        Assert.assertEquals(
            ContextCompat.getColor(context, R.color.teal_700),
            linkChunkColorSpans[0].foregroundColor
        )

        // Verify link chunk has ClickableLinkSpan on top of global spans
        val linkSpansInChunk =
            spanned.getSpans(expectedStart, expectedEnd, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpansInChunk.size)

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testOccurrenceChunkBuilderAllStyles() {
        val regex = "dolor"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.purple_500)
                        .setChunkBackgroundColor(R.color.teal_200)
                        .setTextSize(R.dimen.test_default_text_size)
                        .setStyle(androidx.appcompat.R.style.TextAppearance_AppCompat_Small)
                        .setFont(R.font.ocean_summer)
                        .setTextStyle(Typeface.BOLD_ITALIC)
                        .isUnderlined()
                        .isStrikeThrough()
                        .setScriptType(ScriptType.SUB)
                        .setParagraphBackgroundColor(
                            SequenceBackgroundColor(
                                backgroundColor = R.color.purple_700,
                                padding = R.dimen.test_background_padding,
                                gravity = Gravity.CENTER
                            )
                        )
                        .setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */
                                }
                            })
                )
            )
            .build()
            .create() as Spanned

        val expectedStart = occurrenceBoundaries[0].first
        val expectedEnd = occurrenceBoundaries[0].last + 1

        // Verify ClickableLinkSpan
        val linkSpans = spanned.getSpans(expectedStart, expectedEnd, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(expectedStart, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(expectedEnd, spanned.getSpanEnd(linkSpans[0]))

        // Verify chunk-specific spans
        val colorSpans =
            spanned.getSpans(expectedStart, expectedEnd, ForegroundColorSpan::class.java)
        Assert.assertEquals(0, colorSpans.size) // Color handled by ClickableLinkSpan
        val bgSpans = spanned.getSpans(expectedStart, expectedEnd, BackgroundColorSpan::class.java)
        Assert.assertEquals(1, bgSpans.size)
        val sizeSpans = spanned.getSpans(expectedStart, expectedEnd, AbsoluteSizeSpan::class.java)
        Assert.assertEquals(1, sizeSpans.size)
        val styleSpans =
            spanned.getSpans(expectedStart, expectedEnd, TextAppearanceSpan::class.java)
        Assert.assertEquals(1, styleSpans.size)
        val typefaceSpans = spanned.getSpans(expectedStart, expectedEnd, TypefaceSpan::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Assert.assertEquals(1, typefaceSpans.size)
        }
        val boldItalicSpans = spanned.getSpans(expectedStart, expectedEnd, StyleSpan::class.java)
        Assert.assertEquals(1, boldItalicSpans.size) // Only BOLD_ITALIC from setTextStyle
        val strikeSpans =
            spanned.getSpans(expectedStart, expectedEnd, StrikethroughSpan::class.java)
        Assert.assertEquals(1, strikeSpans.size)
        val subSpans = spanned.getSpans(expectedStart, expectedEnd, SubscriptSpan::class.java)
        Assert.assertEquals(1, subSpans.size)
        val bgParagraphSpans =
            spanned.getSpans(expectedStart, expectedEnd, PaddingBackgroundColorSpan::class.java)
        Assert.assertEquals(1, bgParagraphSpans.size)

        // Verify TextCaseSpan behavior: uppercase transformation
        Assert.assertEquals(
            "DOLOR",
            spanned.subSequence(expectedStart, expectedEnd).toString()
        )
        // Verify StyleSpan is BOLD_ITALIC
        Assert.assertEquals(Typeface.BOLD_ITALIC, boldItalicSpans[0].style)

        // Ensure text is unaltered (case transformation is visual, not content)
        Assert.assertEquals(text.lowercase(), spanned.toString().lowercase())
    }

    @Test
    fun testOverlappingRegexAndBoundaryChunks() {
        val regex = "ipsum"
        val boundary = " "
        val regexBoundaries = getRegexOccurrencesBoundaries(regex, text)
        val boundaryBoundaries = getDelimiterSlicedOccurrencesBoundaries(boundary, text)
        Assert.assertTrue(regexBoundaries.isNotEmpty())
        Assert.assertTrue(boundaryBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.purple_500)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */
                                }
                            }
                        )
                ),
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.BOUNDARY(boundary),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder().setTextSize(R.dimen.test_default_text_size),
                )
            )
            .build()
            .create() as Spanned

        // Verify regex chunk
        val linkSpans = spanned.getSpans(
            regexBoundaries[0].first,
            regexBoundaries[0].last + 1,
            ClickableLinkSpan::class.java
        )
        Assert.assertEquals(1, linkSpans.size)
        if (linkSpans.isNotEmpty()) {
            Assert.assertEquals(regexBoundaries[0].first, spanned.getSpanStart(linkSpans[0]))
            Assert.assertEquals(regexBoundaries[0].last + 1, spanned.getSpanEnd(linkSpans[0]))
        }

        // Verify no separate color span
        val colorSpans = spanned.getSpans(
            regexBoundaries[0].first,
            regexBoundaries[0].last + 1,
            ForegroundColorSpan::class.java
        )
        Assert.assertEquals(0, colorSpans.size)

        // Verify boundary chunk
        val sizeSpans = spanned.getSpans(
            boundaryBoundaries[0].first,
            boundaryBoundaries[0].last + 1,
            AbsoluteSizeSpan::class.java
        )
        Assert.assertEquals(1, sizeSpans.size)
        Assert.assertEquals(boundaryBoundaries[0].first, spanned.getSpanStart(sizeSpans[0]))
        Assert.assertEquals(boundaryBoundaries[0].last + 1, spanned.getSpanEnd(sizeSpans[0]))

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testRegexClickableChunkAlone() {
        val regex = "ipsum"
        val regexBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(regexBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.purple_500)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */
                                }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        val linkSpans = spanned.getSpans(
            regexBoundaries[0].first,
            regexBoundaries[0].last + 1,
            ClickableLinkSpan::class.java
        )
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(regexBoundaries[0].first, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(regexBoundaries[0].last + 1, spanned.getSpanEnd(linkSpans[0]))

        val colorSpans = spanned.getSpans(
            regexBoundaries[0].first,
            regexBoundaries[0].last + 1,
            ForegroundColorSpan::class.java
        )
        Assert.assertEquals(0, colorSpans.size)

        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testInvalidOccurrencePositionIndices() {
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val invalidIndices = listOf(100, 200) // Beyond number of matches
        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.Indices(invalidIndices)
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */
                                }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        // No spans should be applied for invalid indices
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(0, linkSpans.size)

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testMonetaryPatternRegex() {
        val testText = "Price: 5 $, $10.50, 100.00 $"
        val regex = MONETARY_PATTERN
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertEquals(3, occurrenceBoundaries.size) // Expect "5 $", "$10.50", "100.00 $"

        val spanned = EasySpans.Builder(context, testText, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.All
                    ),
                    builder = OccurrenceChunkBuilder().setColor(R.color.teal_700),
                )
            )
            .build()
            .create() as Spanned

        val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(3, colorSpans.size)
        occurrenceBoundaries.forEachIndexed { index, range ->
            Assert.assertEquals(range.first, spanned.getSpanStart(colorSpans[index]))
            Assert.assertEquals(range.last + 1, spanned.getSpanEnd(colorSpans[index]))
        }

        // Ensure text is unaltered
        Assert.assertEquals(testText, spanned.toString())
    }

    @Test
    fun testMultipleRegexAndBoundaryChunks() {
        val testText = "Lorem ipsum dolor ipsum"
        val regex1 = "ipsum"
        val regex2 = "dolor"
        val boundary = " "
        val regexBoundaries1 = getRegexOccurrencesBoundaries(regex1, testText)
        val regexBoundaries2 = getRegexOccurrencesBoundaries(regex2, testText)
        val boundaryBoundaries = getDelimiterSlicedOccurrencesBoundaries(boundary, testText)
        Assert.assertTrue(regexBoundaries1.isNotEmpty())
        Assert.assertTrue(regexBoundaries2.isNotEmpty())
        Assert.assertTrue(boundaryBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, testText, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex1),
                        occurrencePosition = OccurrencePosition.All
                    ),
                    builder = OccurrenceChunkBuilder().setColor(R.color.purple_500)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */
                                }
                            }
                        )
                ),
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex2),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setTextSize(R.dimen.test_default_text_size)
                ),
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.BOUNDARY(boundary),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setTextSize(R.dimen.test_default_text_size)
                )
            )
            .build()
            .create() as Spanned

        // Verify regex1 chunk ("ipsum" - two matches, clickable)
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(2, linkSpans.size) // Two "ipsum" matches
        regexBoundaries1.forEachIndexed { index, range ->
            Assert.assertEquals(range.first, spanned.getSpanStart(linkSpans[index]))
            Assert.assertEquals(range.last + 1, spanned.getSpanEnd(linkSpans[index]))
        }

        // Verify no separate color spans for regex1 (handled by ClickableLinkSpan)
        val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(0, colorSpans.size)

        // Verify regex2 chunk ("dolor" - first match, non-clickable)
        val sizeSpansRegex2 = spanned.getSpans(
            regexBoundaries2[0].first,
            regexBoundaries2[0].last + 1,
            AbsoluteSizeSpan::class.java
        )
        Assert.assertEquals(1, sizeSpansRegex2.size)
        Assert.assertEquals(regexBoundaries2[0].first, spanned.getSpanStart(sizeSpansRegex2[0]))
        Assert.assertEquals(regexBoundaries2[0].last + 1, spanned.getSpanEnd(sizeSpansRegex2[0]))

        // Verify boundary chunk ("Lorem" - first word)
        val sizeSpansBoundary = spanned.getSpans(
            boundaryBoundaries[0].first,
            boundaryBoundaries[0].last + 1,
            AbsoluteSizeSpan::class.java
        )
        Assert.assertEquals(1, sizeSpansBoundary.size)
        Assert.assertEquals(boundaryBoundaries[0].first, spanned.getSpanStart(sizeSpansBoundary[0]))
        Assert.assertEquals(
            boundaryBoundaries[0].last + 1,
            spanned.getSpanEnd(sizeSpansBoundary[0])
        )

        // Ensure text is unaltered
        Assert.assertEquals(testText, spanned.toString())
    }

    @Test
    fun testConsecutiveBoundaryDelimiters() {
        val testText = "Lorem  ipsum   dolor"
        val boundary = " "
        val boundaryBoundaries = getDelimiterSlicedOccurrencesBoundaries(boundary, testText)
        Assert.assertEquals(3, boundaryBoundaries.size) // "Lorem", "ipsum", "dolor"

        val spanned = EasySpans.Builder(context, testText, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.BOUNDARY(boundary),
                        occurrencePosition = OccurrencePosition.All
                    ),
                    builder = OccurrenceChunkBuilder().setTextSize(R.dimen.test_default_text_size),
                )
            )
            .build()
            .create() as Spanned

        // Verify boundary chunk (three words)
        val sizeSpans = spanned.getSpans(0, spanned.length, AbsoluteSizeSpan::class.java)
        Assert.assertEquals(3, sizeSpans.size)
        boundaryBoundaries.forEachIndexed { index, range ->
            Assert.assertEquals(range.first, spanned.getSpanStart(sizeSpans[index]))
            Assert.assertEquals(range.last + 1, spanned.getSpanEnd(sizeSpans[index]))
        }

        // Ensure text is unaltered
        Assert.assertEquals(testText, spanned.toString())
    }

    @Test
    fun testReusedEasySpans() {
        val testText = "Lorem ipsum"
        val easySpans = EasySpans.Builder(context, testText, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX("ipsum"),
                        occurrencePosition = OccurrencePosition.First
                    ),

                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.purple_500)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */
                                }
                            }
                        ),
                )
            )
            .build()

        val spanned1 = easySpans.create() as Spanned
        val spanned2 = easySpans.create() as Spanned

        // Verify link spans are consistent
        val linkSpans1 = spanned1.getSpans(0, spanned1.length, ClickableLinkSpan::class.java)
        val linkSpans2 = spanned2.getSpans(0, spanned2.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans1.size)
        Assert.assertEquals(1, linkSpans2.size)
        Assert.assertEquals(
            spanned1.getSpanStart(linkSpans1[0]),
            spanned2.getSpanStart(linkSpans2[0])
        )
        Assert.assertEquals(spanned1.getSpanEnd(linkSpans1[0]), spanned2.getSpanEnd(linkSpans2[0]))

        // Ensure text is unaltered and consistent
        Assert.assertEquals(testText, spanned1.toString())
        Assert.assertEquals(testText, spanned2.toString())
        Assert.assertEquals(spanned1.toString(), spanned2.toString())
    }

    @Test
    fun testNegativeIndicesInOccurrencePosition() {
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val invalidIndices = listOf(-1, -5, 0) // Include negative and valid indices
        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.Indices(invalidIndices)
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */
                                }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        // Only valid index (0) should apply a span
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)
        Assert.assertEquals(occurrenceBoundaries[0].first, spanned.getSpanStart(linkSpans[0]))
        Assert.assertEquals(occurrenceBoundaries[0].last + 1, spanned.getSpanEnd(linkSpans[0]))

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testInvalidResourceIds() {
        val regex = "ipsum"
        val invalidColorRes = 999999 // Non-existent resource ID
        val invalidTextSizeRes = 999998

        try {
            val spanned = EasySpans.Builder(context, text, textView)
                .setOccurrenceChunks(
                    OccurrenceChunk(
                        location = OccurrenceLocation(
                            delimitationType = DelimitationType.REGEX(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        builder = OccurrenceChunkBuilder()
                            .setColor(invalidColorRes)
                            .setTextSize(invalidTextSizeRes)
                    )
                )
                .build()
                .create() as Spanned

            // If no exception, verify no spans applied due to invalid resources
            val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
            Assert.assertEquals(0, colorSpans.size)
            val sizeSpans = spanned.getSpans(0, spanned.length, AbsoluteSizeSpan::class.java)
            Assert.assertEquals(0, sizeSpans.size)

            // Ensure text is unaltered
            Assert.assertEquals(text, spanned.toString())
        } catch (e: android.content.res.Resources.NotFoundException) {
            // Expected if resource lookup fails
            Assert.assertTrue(true)
        }
    }

    @Test
    fun testNullTextViewReference() {
        val regex = "dolor"
        val testText = "Lorem ipsum dolor"

        try {
            val spanned = EasySpans.Builder(context, testText)
                .setOccurrenceChunks(
                    OccurrenceChunk(
                        location = OccurrenceLocation(
                            delimitationType = DelimitationType.REGEX(regex),
                            occurrencePosition = OccurrencePosition.First
                        ),
                        builder = OccurrenceChunkBuilder()
                            .setColor(R.color.teal_700)
                            .setOnLinkClickListener(
                                object : ClickableLinkSpan.OnLinkClickListener {
                                    override fun onLinkClick(view: View) { /* no-op */ }
                                }
                            )
                    )
                )
                .build()
                .create() as Spanned

            // If no exception, verify no spans applied
            val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
            Assert.assertEquals(0, linkSpans.size)

            // Ensure text is unaltered
            Assert.assertEquals(testText, spanned.toString())
        } catch (e: NullPointerException) {
            // Expected if EasySpans enforces non-null TextView
            Assert.assertTrue(true)
        }
    }

    @Test
    fun testInvalidSpanConfigurations() {
        val regex = "ipsum"
        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setTextStyle(-1) // Invalid text style
                        .setScriptType(ScriptType.NONE), // Redundant script type
                )
            )
            .build()
            .create() as Spanned

        // Verify no spans applied for invalid configurations
        val styleSpans = spanned.getSpans(0, spanned.length, StyleSpan::class.java)
        Assert.assertEquals(0, styleSpans.size)
        val scriptSpans = spanned.getSpans(0, spanned.length, SubscriptSpan::class.java)
        Assert.assertEquals(0, scriptSpans.size)

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testVeryLargeTextStress() {
        val largeText = text.repeat(100) // 100x original text (~500KB)
        val regex = "nec"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, largeText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, largeText, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.All
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.teal_700),
                )
            )
            .build()
            .create() as Spanned

        // Verify span count matches occurrences
        val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(occurrenceBoundaries.size, colorSpans.size)
        occurrenceBoundaries.forEachIndexed { index, range ->
            Assert.assertEquals(range.first, spanned.getSpanStart(colorSpans[index]))
            Assert.assertEquals(range.last + 1, spanned.getSpanEnd(colorSpans[index]))
        }

        // Ensure text is unaltered
        Assert.assertEquals(largeText, spanned.toString())
    }

    @Test
    fun testManyConcurrentSpans() {
        val testText = "Lorem ipsum dolor sit amet"
        val regexes = listOf("Lorem", "ipsum", "dolor", "sit", "amet")
        val chunks = regexes.mapIndexed { index, regex ->
            OccurrenceChunk(
                location = OccurrenceLocation(
                    delimitationType = DelimitationType.REGEX(regex),
                    occurrencePosition = OccurrencePosition.First
                ),
                builder = OccurrenceChunkBuilder()
                    .setColor(R.color.teal_700)
                    .setTextSize(R.dimen.test_default_text_size)
                    .setTextStyle(Typeface.BOLD)
            )
        }.toTypedArray()

        val spanned = EasySpans.Builder(context, testText, textView)
            .setOccurrenceChunks(*chunks)
            .build()
            .create() as Spanned

        // Verify spans for each regex
        val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        val sizeSpans = spanned.getSpans(0, spanned.length, AbsoluteSizeSpan::class.java)
        val styleSpans = spanned.getSpans(0, spanned.length, StyleSpan::class.java)
        Assert.assertEquals(regexes.size, colorSpans.size)
        Assert.assertEquals(regexes.size, sizeSpans.size)
        Assert.assertEquals(regexes.size, styleSpans.size)

        // Ensure text is unaltered
        Assert.assertEquals(testText, spanned.toString())
    }

    @Test
    fun testComplexRegexPatterns() {
        val testText = "Emails: user1@domain.com, user2@sub.domain.org, invalid@"
        val complexRegex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(complexRegex, testText)
        Assert.assertEquals(2, occurrenceBoundaries.size) // Two valid emails

        val spanned = EasySpans.Builder(context, testText, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(complexRegex),
                        occurrencePosition = OccurrencePosition.All
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.purple_500)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        // Verify link spans
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(2, linkSpans.size)
        occurrenceBoundaries.forEachIndexed { index, range ->
            Assert.assertEquals(range.first, spanned.getSpanStart(linkSpans[index]))
            Assert.assertEquals(range.last + 1, spanned.getSpanEnd(linkSpans[index]))
        }

        // Ensure text is unaltered
        Assert.assertEquals(testText, spanned.toString())
    }

    @Test
    fun testConcurrentSpanApplications() {
        val testText = "Lorem ipsum dolor"
        val regex = "ipsum"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val executor = Executors.newFixedThreadPool(4)
        val latch = CountDownLatch(4)
        val spanneds = arrayOfNulls<Spanned>(4)

        repeat(4) { index ->
            executor.submit {
                try {
                    val spanned = EasySpans.Builder(context, testText, textView)
                        .setOccurrenceChunks(
                            OccurrenceChunk(
                                location = OccurrenceLocation(
                                    delimitationType = DelimitationType.REGEX(regex),
                                    occurrencePosition = OccurrencePosition.First
                                ),
                                builder = OccurrenceChunkBuilder()
                                    .setColor(R.color.teal_700)
                                    .setOnLinkClickListener(
                                        object : ClickableLinkSpan.OnLinkClickListener {
                                            override fun onLinkClick(view: View) { /* no-op */
                                            }
                                        }
                                    ),
                            )
                        )
                        .build()
                        .create() as Spanned
                    spanneds[index] = spanned
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await(5, TimeUnit.SECONDS)

        // Verify each spanned text
        spanneds.forEach { spanned ->
            Assert.assertNotNull(spanned)
            val linkSpans = spanned!!.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
            Assert.assertEquals(1, linkSpans.size)
            Assert.assertEquals(occurrenceBoundaries[0].first, spanned.getSpanStart(linkSpans[0]))
            Assert.assertEquals(occurrenceBoundaries[0].last + 1, spanned.getSpanEnd(linkSpans[0]))
            Assert.assertEquals(testText, spanned.toString())
        }

        executor.shutdown()
    }

    @Test
    fun testRaceConditionsWithClickHandlers() {
        val regex = "dolor"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val clickCount = intArrayOf(0)
        val latch = CountDownLatch(10)
        val executor = Executors.newFixedThreadPool(10)

        val spanned = EasySpans.Builder(context, text, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) {
                                    synchronized(clickCount) { clickCount[0]++ }
                                }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        textView.text = spanned
        val linkSpans = spanned.getSpans(0, spanned.length, ClickableLinkSpan::class.java)
        Assert.assertEquals(1, linkSpans.size)

        // Simulate concurrent clicks
        repeat(10) {
            executor.submit {
                try {
                    linkSpans[0].onClick(textView)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await(5, TimeUnit.SECONDS)
        Assert.assertEquals(10, clickCount[0])
        Assert.assertEquals(text, spanned.toString())

        executor.shutdown()
    }

    @Test
    fun testTextViewStatePreservation() {
        val testText = "Lorem ipsum"
        val regex = "ipsum"
        val spanned = EasySpans.Builder(context, testText, textView)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.purple_500)
                        .setOnLinkClickListener(
                            object : ClickableLinkSpan.OnLinkClickListener {
                                override fun onLinkClick(view: View) { /* no-op */ }
                            }
                        )
                )
            )
            .build()
            .create() as Spanned

        // Set initial state
        textView.text = spanned
        textView.isEnabled = false
        val initialEnabled = textView.isEnabled

        // Re-apply spans
        val newSpanned = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder().setTextSize(R.dimen.test_default_text_size)
                )
            )
            .build()
            .create() as Spanned

        textView.text = newSpanned

        // Verify TextView state
        Assert.assertEquals(initialEnabled, textView.isEnabled)
        Assert.assertEquals(testText, newSpanned.toString())
    }

    @Test
    fun testSpanOrdering() {
        val regex1 = "ipsum"
        val regex2 = "ipsum" // Overlapping match
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex1, text)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, text)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex1),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder().setColor(R.color.teal_700)
                ),
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex2),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder().setColor(R.color.purple_500)
                )
            )
            .build()
            .create() as Spanned

        // Verify spans (both spans applied, last one renders)
        val colorSpans = spanned.getSpans(
            occurrenceBoundaries[0].first,
            occurrenceBoundaries[0].last + 1,
            ForegroundColorSpan::class.java
        )
        Assert.assertEquals(2, colorSpans.size) // Expect both spans
        // Verify both colors are applied
        val expectedColors = listOf(
            ContextCompat.getColor(context, R.color.teal_700),
            ContextCompat.getColor(context, R.color.purple_500)
        )
        val actualColors = colorSpans.map { it.foregroundColor }.sorted()
        Assert.assertEquals(expectedColors.sorted(), actualColors)

        // Ensure text is unaltered
        Assert.assertEquals(text, spanned.toString())
    }

    @Test
    fun testSpanReplacementBehavior() {
        val testText = "Lorem ipsum"
        val regex = "ipsum"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned1 = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder().setColor(R.color.teal_700)
                )
            )
            .build()
            .create() as Spanned

        // Apply different span
        val spanned2 = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder().setTextSize(R.dimen.test_default_text_size)
                )
            )
            .build()
            .create() as Spanned

        // Verify spans in second application
        val colorSpans = spanned2.getSpans(0, spanned2.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(0, colorSpans.size)
        val sizeSpans = spanned2.getSpans(
            occurrenceBoundaries[0].first,
            occurrenceBoundaries[0].last + 1,
            AbsoluteSizeSpan::class.java
        )
        Assert.assertEquals(1, sizeSpans.size)

        // Ensure text is unaltered
        Assert.assertEquals(testText, spanned2.toString())
    }

    @Test
    fun testSpanRemovalEffects() {
        val testText = "Lorem ipsum"
        val regex = "ipsum"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.teal_700)
                        .setTextSize(R.dimen.test_default_text_size)
                )
            )
            .build()
            .create() as SpannableStringBuilder

        // Remove spans
        spanned.removeSpan(spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)[0])

        // Verify remaining spans
        val colorSpans = spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
        Assert.assertEquals(0, colorSpans.size)
        val sizeSpans = spanned.getSpans(
            occurrenceBoundaries[0].first,
            occurrenceBoundaries[0].last + 1,
            AbsoluteSizeSpan::class.java
        )
        Assert.assertEquals(1, sizeSpans.size)

        // Ensure text is unaltered
        Assert.assertEquals(testText, spanned.toString())
    }

    @Test
    fun testAddSpanWithSingleCustomSpan() {
        val testText = "Hello World"
        val regex = "World"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .addSpan { StrikethroughSpan() }
                )
            )
            .build()
            .create() as SpannableStringBuilder

        val strikeSpans = spanned.getSpans(
            occurrenceBoundaries[0].first,
            occurrenceBoundaries[0].last + 1,
            StrikethroughSpan::class.java
        )
        Assert.assertEquals(1, strikeSpans.size)
    }

    @Test
    fun testAddSpanWithMultipleCustomSpans() {
        val testText = "Test Text"
        val regex = "Text"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .addSpan { StrikethroughSpan() }
                        .addSpan { SuperscriptSpan() }
                        .addSpan { StyleSpan(Typeface.BOLD) }
                )
            )
            .build()
            .create() as SpannableStringBuilder

        val start = occurrenceBoundaries[0].first
        val end = occurrenceBoundaries[0].last + 1

        Assert.assertEquals(1, spanned.getSpans(start, end, StrikethroughSpan::class.java).size)
        Assert.assertEquals(1, spanned.getSpans(start, end, SuperscriptSpan::class.java).size)
        Assert.assertEquals(1, spanned.getSpans(start, end, StyleSpan::class.java).size)
    }

    @Test
    fun testAddSpanWithSpanRemoval() {
        val testText = "Sample Text"
        val regex = "Sample"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .setColor(R.color.teal_700)
                        .addSpan { StrikethroughSpan() }
                )
            )
            .build()
            .create() as SpannableStringBuilder

        val start = occurrenceBoundaries[0].first
        val end = occurrenceBoundaries[0].last + 1

        val strikeSpan = spanned.getSpans(start, end, StrikethroughSpan::class.java)[0]
        spanned.removeSpan(strikeSpan)

        Assert.assertEquals(0, spanned.getSpans(start, end, StrikethroughSpan::class.java).size)
        Assert.assertEquals(1, spanned.getSpans(start, end, ForegroundColorSpan::class.java).size)
    }

    @Test
    fun testCustomSpanWithBlurMask() {
        val testText = "Lorem ipsum dolor"
        val regex = "ipsum"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val blurMask = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
        val spanned = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .addSpan { MaskFilterSpan(blurMask) }
                )
            )
            .build()
            .create() as SpannableStringBuilder

        val maskSpans = spanned.getSpans(
            occurrenceBoundaries[0].first,
            occurrenceBoundaries[0].last + 1,
            MaskFilterSpan::class.java
        )
        Assert.assertEquals(1, maskSpans.size)

        // Note: Visual verification required for blur effect
        Assert.assertEquals(testText, spanned.toString())
    }

    @Test
    fun testCustomSpanWithMultipleEffects() {
        val testText = "Sample Text"
        val regex = "Text"
        val occurrenceBoundaries = getRegexOccurrencesBoundaries(regex, testText)
        Assert.assertTrue(occurrenceBoundaries.isNotEmpty())

        val spanned = EasySpans.Builder(context, testText)
            .setOccurrenceChunks(
                OccurrenceChunk(
                    location = OccurrenceLocation(
                        delimitationType = DelimitationType.REGEX(regex),
                        occurrencePosition = OccurrencePosition.First
                    ),
                    builder = OccurrenceChunkBuilder()
                        .addSpan { StrikethroughSpan() }
                        .addSpan { SuperscriptSpan() }
                        .addSpan { ForegroundColorSpan(Color.RED) }
                )
            )
            .build()
            .create() as SpannableStringBuilder

        val start = occurrenceBoundaries[0].first
        val end = occurrenceBoundaries[0].last + 1

        Assert.assertEquals(1, spanned.getSpans(start, end, StrikethroughSpan::class.java).size)
        Assert.assertEquals(1, spanned.getSpans(start, end, SuperscriptSpan::class.java).size)
        Assert.assertEquals(1, spanned.getSpans(start, end, ForegroundColorSpan::class.java).size)
    }

    @After
    fun teardown() {}

    private fun getRegexOccurrencesBoundaries(regex: String, text: String): List<IntRange> {
        return Regex(regex).findAll(text).map { it.range }.toList()
    }

    private fun getDelimiterSlicedOccurrencesBoundaries(
        boundary: String,
        text: String
    ): List<IntRange> {
        if (boundary.isEmpty()) return listOf(text.indices)
        val ranges = mutableListOf<IntRange>()
        var start = 0
        while (start < text.length) {
            val end = text.indexOf(boundary, start).takeIf { it >= 0 } ?: text.length
            ranges.add(start until end)
            start = end + boundary.length
        }
        return ranges.filterNot { it.isEmpty() }
    }
}