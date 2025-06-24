package com.mamboa

import android.graphics.BlurMaskFilter
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.MaskFilterSpan
import android.text.style.StrikethroughSpan
import android.text.style.SuperscriptSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mamboa.databinding.FragmentFirstBinding
import com.mamboa.easyspans.legacy.EasySpans
import com.mamboa.easyspans.legacy.customspans.ClickableLinkSpan
import com.mamboa.easyspans.legacy.customspans.TextCaseSpan
import com.mamboa.easyspans.legacy.helper.DelimitationType
import com.mamboa.easyspans.legacy.helper.OccurrenceChunk
import com.mamboa.easyspans.legacy.helper.OccurrenceChunkBuilder
import com.mamboa.easyspans.legacy.helper.OccurrenceLocation
import com.mamboa.easyspans.legacy.helper.OccurrencePosition

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val text =
        "Lorem ipsum dolor sit amet, google adipiscing elit. Nulla nec tempus est. Vestibulum volutpat ipsum vitae urna congue, vitae facilisis est iaculis. Integer accumsan ex et nibh mollis, vitae malesuada lacus porttitor. Maecenas commodo turpis nec porttitor fringilla. Maecenas fermentum massa in pulvinar tempus. Phasellus at volutpat mi. Suspendisse faucibus vitae mi vel sollicitudin. Aenean sit amet malesuada ipsum, at vestibulum lectus. Aliquam erat volutpat. Praesent tempus nibh ac ante aliquet suscipit. Praesent ex lectus, dapibus non porttitor id, aliquet nec sem. Mauris ac fringilla augue, ac tincidunt enim. Proin vestibulum auctor mi vitae facilisis. Pellentesque fermentum, mauris a mattis efficitur, ligula enim lobortis eros, sed pulvinar felis dui nec augue. In eget dignissim quam, in blandit massa.\n" +
                "\n" +
                "Phasellus turpis mauris, faucibus vel hendrerit id, mollis ut ex. Etiam cursus nisl nec dapibus eleifend. Phasellus google diam a nibh luctus, in tempor ante viverra. Morbi nec vulputate lorem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis in nunc venenatis, viverra est sed, feugiat magna. In et ullamcorper dolor. Phasellus iaculis sit amet leo id cursus. Etiam congue scelerisque quam, vel accumsan massa mattis vel. Sed diam diam, iaculis eget turpis eget, porta vestibulum ligula. Aliquam tincidunt finibus sem, nec faucibus neque vulputate eget. Nullam ultricies odio a felis egestas dictum.\n" +
                "\n" +
                "Ut eget pretium purus. Aliquam volutpat tristique metus, eget euismod tortor tempus a. Nunc non scelerisque nulla. Trader sit amet mi sit amet libero tristique pretium eget vitae libero. Maecenas tristique dictum tortor id pulvinar. Trader convallis porta tincidunt. Fusce pretium interdum rhoncus. In hac habitasse platea dictumst. Nam dictum non sapien sed sollicitudin. Maecenas eget massa vel felis condimentum ornare sit amet at est. Praesent egestas metus ut turpis convallis dapibus eu in lorem. Duis vel massa pretium, ultricies justo at, faucibus est. Mauris sed aliquam nulla. Aliquam dapibus quam id eleifend tempor. Nullam metus leo, porta eu erat condimentum, varius iaculis odio. Suspendisse potenti.\n" +
                "\n" +
                "Nunc semper aliquam aliquet. Pellentesque in mattis lorem. Sed finibus scelerisque egestas. Trader efficitur molestie velit, sagittis tincidunt turpis semper sed. Maecenas in quam eu turpis sodales laoreet vel vitae mauris. In pretium aliquet ante, at ullamcorper odio lobortis at. Aenean et felis eget augue placerat vulputate. Proin ac neque purus. Mauris malesuada tellus non orci rhoncus, nec convallis felis lobortis. Suspendisse ut bibendum ex. Nullam scelerisque porttitor orci id tincidunt. Sed sit amet malesuada quam, pretium congue nisl. Nunc urna purus, luctus et lectus in, sodales tempus quam. Sed auctor tempor facilisis. Nam ante quam, auctor et sem sed, feugiat volutpat mauris. Aenean elementum metus ut varius sagittis.\n" +
                "\n" +
                "Sed molestie egestas diam, quis dignissim diam efficitur pretium. Phasellus luctus ante ac eros google accumsan. Quisque laoreet tincidunt tellus, vel auctor quam auctor quis. Sed rhoncus orci ac nunc ultricies faucibus. Praesent auctor, neque et interdum imperdiet, ante mauris egestas nisi, id auctor magna sapien non leo. Phasellus in felis ac lectus dapibus porttitor. Duis porta sit amet augue non imperdiet. Ut posuere vehicula congue. In maximus fermentum felis, id feugiat est elementum nec. Pellentesque feugiat dolor risus, id sodales erat auctor at. Sed congue dignissim erat, et pellentesque nunc dictum vel.\n" +
                "\n"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        test2()

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun test1() {
        val googleRegex = "google"
        val trader = "Trader"
        val tempus = "tempus"
        val regex4 = "turpis"
        val blurMask =  BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)

        Log.d("EasySpans", "Input text: $text")
        binding.textviewFirst.text =
            EasySpans.Builder(requireContext(), text, binding.textviewFirst)
                .setOccurrenceLocation(
                    OccurrenceLocation(
                        DelimitationType.NONE,
                        OccurrencePosition.First
                    )
                )
                .setOccurrenceChunks(
                    // Independent
                    /*OccurrenceChunk(
                        location = OccurrenceLocation(
                            delimitationType = DelimitationType.BOUNDARY("\n\n"),
                            occurrencePosition = OccurrencePosition.NTH_INDEXES(1,4)
                        ),
                        builder = OccurrenceChunkBuilder()
                            .setTextSize(R.dimen.test_default_text_size)
                            .setTextStyle(Typeface.BOLD)
                            .setScriptType(ScriptType.SUPER)
                            .isStrikeThrough(true)
                            .isUnderlined(true).setFont(R.font.ocean_summer)
                            .setColor(R.color.white)
                            .setChunkBackgroundColor(R.color.black)
                            //.setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE)
                            .setParagraphBackgroundColor(
                                SequenceBackgroundColor(
                                    backgroundColor = R.color.teal_200,
                                    padding = R.dimen.test_background_padding,
                                    gravity = Gravity.START
                                )
                            )
                    ),*/

                    // LINK_INDEPENDENT
                    OccurrenceChunk(
                        location = OccurrenceLocation(
                            DelimitationType.REGEX(tempus),
                            OccurrencePosition.All // OccurrencePosition.All
                        ),
                        builder = OccurrenceChunkBuilder()
                            .setColor(android.R.color.holo_red_dark)
//                            .isStrikeThrough()
//                            .setScriptType(com.mamboa.easyspans.legacy.helper.ScriptType.SUPER)
                            //.addSpan { ForegroundColorSpan(ContextCompat.getColor(this@FirstFragment.requireContext(), android.R.color.holo_red_dark)) }
                            .addSpan { StrikethroughSpan() }
                            .addSpan { SuperscriptSpan() }
                            .addSpan { MaskFilterSpan(blurMask) }
                            .setOnLinkClickListener(
                                object : ClickableLinkSpan.OnLinkClickListener {
                                    override fun onLinkClick(view: View) {
                                        Snackbar.make(
                                            binding.root,
                                            "$tempus clicked",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            ),
                    ),

                    OccurrenceChunk(
                        location = OccurrenceLocation(
                            DelimitationType.REGEX(googleRegex),
                            OccurrencePosition.All
                        ),
                        builder = OccurrenceChunkBuilder()
                            .setColor(android.R.color.holo_blue_dark)
                            .setOnLinkClickListener(
                                object : ClickableLinkSpan.OnLinkClickListener {
                                    override fun onLinkClick(view: View) {
                                        Snackbar.make(
                                            binding.root,
                                            "$googleRegex clicked",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                    ),
                    // INDEPENDENT
                     OccurrenceChunk(
                         location = OccurrenceLocation(
                             DelimitationType.REGEX(regex4),
                             OccurrencePosition.All//NTH_INDEXES(1, 4)
                         ),
                         builder = OccurrenceChunkBuilder()
                             .setTextSize(R.dimen.test_text_size)
                             .setTextStyle(Typeface.BOLD)
                             //.setScriptType(ScriptType.SUPER)
//                             .setFont(com.mamboa.easyspans.legacy.R.font.ocean_summer)
                             .setColor(R.color.purple_500)
                             .setChunkBackgroundColor(R.color.teal_700)
                             .setTextCaseType(TextCaseSpan.TextCaseType.LOWER_CASE)
                             /*.setParagraphBackgroundColor(
                                 SequenceBackgroundColor(
                                     backgroundColor = R.color.purple_700,
                                     padding = R.dimen.test_background_padding,
                                     gravity = Gravity.CENTER
                                 )
                             )*/
                             .setOnLinkClickListener(
                                 object : ClickableLinkSpan.OnLinkClickListener {
                                     override fun onLinkClick(view: View) {
                                         Snackbar.make(
                                             binding.root,
                                             "$regex4 clicked",
                                             Snackbar.LENGTH_SHORT
                                         ).show()
                                     }
                                 }
                             )
                     ),
                    // LINK_COMMON
                    OccurrenceChunk(
                        location = OccurrenceLocation(
                            DelimitationType.REGEX(trader),
                            OccurrencePosition.All
                        ),
                        builder = OccurrenceChunkBuilder()
                            .setOnLinkClickListener(
                                object : ClickableLinkSpan.OnLinkClickListener {
                                    override fun onLinkClick(view: View) {
                                        Snackbar.make(
                                            binding.root,
                                            "$trader clicked",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                       /* builder = OccurrenceChunkBuilder()
                            .setTextSize(R.dimen.test_text_size)
                            .setTextStyle(Typeface.BOLD)
                            .isStrikeThrough()
                            .isUnderlined()
                            .setColor(android.R.color.holo_green_dark)
                            .setTextCaseType(TextCaseSpan.TextCaseType.UPPER_CASE),*/
                    ),

                )
//                .isUnderlined()
//                .isStrikeThrough()
//                .setColor(R.color.red)
//                .setChunkBackgroundColor(R.color.teal_200)
//                .setTextStyle(Typeface.BOLD)
                .setTextSize(R.dimen.test_default_text_size)
//                .setFont(R.font.ocean_summer)
                .build()
                .create()
    }

    fun test2() {
        val text = "Praesent tempus nibh ac ante aliquet suscipit. Praesent ex lectus, dapibus non porttitor id, aliquet nec sem."
        val tempus = "tempus"
        val blurMask = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
        binding.textviewFirst.text =
            EasySpans.Builder(requireContext(), text, binding.textviewFirst)
                .addSpan { UnderlineSpan()  }
                .setOccurrenceChunks(
                    OccurrenceChunk(
                        location = OccurrenceLocation(
                            DelimitationType.REGEX(tempus),
                            OccurrencePosition.All
                        ),
                        builder = OccurrenceChunkBuilder()
                            .setColor(android.R.color.holo_red_dark)
                            .addSpan { StrikethroughSpan() }
                            .addSpan { MaskFilterSpan(blurMask) }
                            .setOnLinkClickListener(
                                object : ClickableLinkSpan.OnLinkClickListener {
                                    override fun onLinkClick(view: View) {
                                        Snackbar.make(
                                            binding.root,
                                            "$tempus clicked",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            ),
                    )
                )
                .build()
                .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}