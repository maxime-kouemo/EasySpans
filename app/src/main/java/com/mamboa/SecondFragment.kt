package com.mamboa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.mamboa.easyspans.compose.DelimitationType
import com.mamboa.easyspans.compose.EasySpansClickableText
import com.mamboa.easyspans.compose.EasySpansCompose
import com.mamboa.easyspans.compose.OccurrenceLocation
import com.mamboa.easyspans.compose.OccurrencePosition
import com.mamboa.easyspans.compose.ScriptType
import com.mamboa.easyspans.compose.occurrenceChunk

class SecondFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                /*SecondScreen(
                    onNavigateToFirst = {
                        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                    }
                )*/
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    /* item {
                         ChemicalFormula()
                     }
                     item {
                         Spacer(modifier = Modifier.height(16.dp))
                     }
                     item {
                         FirstWordStyled()
                     }
                     item {
                         Spacer(modifier = Modifier.height(16.dp))
                     }*/
                    item {
                        ClickableWords()
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        UppercaseNthWord()
                    }
                    /*
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        MixedDelimiterStyles()
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        MonetaryValuesStyled()
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        LocalTestUppercaseNthWord()
                    }*/
                }
            }
        }
    }
}

@Composable
private fun SecondScreen(onNavigateToFirst: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onNavigateToFirst) {
            Text("Go to First Screen")
        }
    }
}

@Composable
fun ChemicalFormula() {
    val formula = "H₂SO₄" // Sulfuric acid
    val annotatedString = EasySpansCompose("Sulfuric acid H₂SO₄") {
        setColor(Color.White)
        setBackgroundColor(Color.DarkGray)
        setFontSize(16.sp)
        //setFontFamily(FontFamily(Font()) com.mamboa.legacy.R.font.ocean_summer)
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


@Composable
fun ClickableWords() {
    /*RoundedBackgroundText(
        text = "Hello World! Hello Compose!\nHello World! Hello Compose!\nHello World! Hello Compose!",
        backgroundColor = Color.Blue.copy(alpha = 0.5f),
        cornerRadius = 8.dp,
        horizontalPadding = 4.dp,
        verticalPadding = 6.dp,
        occurrenceLocation = OccurrenceLocation(
            delimitationType = DelimitationType.Boundary(" "),
            occurrencePosition = OccurrencePosition.Last
        )
    )*/
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

@Composable
fun UppercaseNthWord() {
    /*DashedDecorationText(
        text = "Hello World! This is a test.",
        dashedUnderline = true,
        dashedStrikethrough = true,
        dashPattern = DashPattern.DASH_DOT,
        dashWidth = 4.dp,
        dashGap = 2.dp,
        lineColor = Color.Blue,
        lineThickness = 1.dp,
        occurrenceLocation = OccurrenceLocation(
            delimitationType = DelimitationType.Boundary(" "),
            occurrencePosition = OccurrencePosition.First
        )
    )*/

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

@Composable
fun LocalTestUppercaseNthWord() {
    val text = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla nec tempus est. Vestibulum volutpat ipsum vitae urna congue, vitae facilisis est iaculis. Integer accumsan ex et nibh mollis, vitae malesuada lacus porttitor. Maecenas commodo turpis nec porttitor fringilla. Maecenas fermentum massa in pulvinar tempus. Phasellus at volutpat mi. Suspendisse faucibus vitae mi vel sollicitudin. Aenean sit amet malesuada ipsum, at vestibulum lectus. Aliquam erat volutpat. Praesent tempus nibh ac ante aliquet suscipit. Praesent ex lectus, dapibus non porttitor id, aliquet nec sem. Mauris ac fringilla augue, ac tincidunt enim. Proin vestibulum auctor mi vitae facilisis. Pellentesque fermentum, mauris a mattis efficitur, ligula enim lobortis eros, sed pulvinar felis dui nec augue. In eget dignissim quam, in blandit massa.

        Phasellus turpis mauris, faucibus vel hendrerit id, mollis ut ex. Etiam cursus nisl nec dapibus eleifend. Phasellus consectetur diam a nibh luctus, in tempor ante viverra. Morbi nec vulputate lorem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis in nunc venenatis, viverra est sed, feugiat magna. In et ullamcorper dolor. Phasellus iaculis sit amet leo id cursus. Etiam congue scelerisque quam, vel accumsan massa mattis vel. Sed diam diam, iaculis eget turpis eget, porta vestibulum ligula. Aliquam tincidunt finibus sem, nec faucibus neque vulputate eget. Nullam ultricies odio a felis egestas dictum.

        Ut eget pretium purus. Aliquam volutpat tristique metus, eget euismod tortor tempus a. Nunc non scelerisque nulla. Donec sit amet mi sit amet libero tristique pretium eget vitae libero. Maecenas tristique dictum tortor id pulvinar. Donec convallis porta tincidunt. Fusce pretium interdum rhoncus. In hac habitasse platea dictumst. Nam dictum non sapien sed sollicitudin. Maecenas eget massa vel felis condimentum ornare sit amet at est. Praesent egestas metus ut turpis convallis dapibus eu in lorem. Duis vel massa pretium, ultricies justo at, faucibus est. Mauris sed aliquam nulla. Aliquam dapibus quam id eleifend tempor. Nullam metus leo, porta eu erat condimentum, varius iaculis odio. Suspendisse potenti.

        Nunc semper aliquam aliquet. Pellentesque in mattis lorem. Sed finibus scelerisque egestas. Donec efficitur molestie velit, sagittis tincidunt turpis semper sed. Maecenas in quam eu turpis sodales laoreet vel vitae mauris. In pretium aliquet ante, at ullamcorper odio lobortis at. Aenean et felis eget augue placerat vulputate. Proin ac neque purus. Mauris malesuada tellus non orci rhoncus, nec convallis felis lobortis. Suspendisse ut bibendum ex. Nullam scelerisque porttitor orci id tincidunt. Sed sit amet malesuada quam, pretium congue nisl. Nunc urna purus, luctus et lectus in, sodales tempus quam. Sed auctor tempor facilisis. Nam ante quam, auctor et sem sed, feugiat volutpat mauris. Aenean elementum metus ut varius sagittis.

        Sed molestie egestas diam, quis dignissim diam efficitur pretium. Phasellus luctus ante ac eros consectetur accumsan. Quisque laoreet tincidunt tellus, vel auctor quam auctor quis. Sed rhoncus orci ac nunc ultricies faucibus. Praesent auctor, neque et interdum imperdiet, ante mauris egestas nisi, id auctor magna sapien non leo. Phasellus in felis ac lectus dapibus porttitor. Duis porta sit amet augue non imperdiet. Ut posuere vehicula congue. In maximus fermentum felis, id feugiat est elementum nec. Pellentesque feugiat dolor risus, id sodales erat auctor at. Sed congue dignissim erat, et pellentesque nunc dictum vel.
    """.trimIndent()
    val regex = "dolor"
    val magenta = Color(0xFFAB47BC)
    val test_default_text_size = 16.sp
    val annotatedString = EasySpansCompose(text) {
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
                        background = Color(0xFF26A69A),
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
        setTextCase { it.uppercase() }
    }

    // Store TextLayoutResult in a remembered state
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = annotatedString,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    textLayoutResult?.let { layoutResult ->
                        val position = layoutResult.getOffsetForPosition(offset)
                        val annotations =
                            annotatedString.getStringAnnotations(start = position, end = position)
                        annotations.firstOrNull()?.let { annotation ->
                            when (annotation.tag) {
                                "clickable" -> println("Clicked on: ${annotation.item} with tag ${annotation.tag}")
                                else -> println("Unknown click: ${annotation.item} with tag ${annotation.tag}")
                            }
                        }
                    }
                }
            },
        onTextLayout = { layoutResult ->
            textLayoutResult = layoutResult
        }
    )
}