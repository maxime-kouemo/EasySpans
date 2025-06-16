package com.mamboa.easyspans.legacy.helper

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Specifies how text should be delimited for span operations.
 *
 * - [REGEX]: Match by regular expression; applies spans to each regex match.
 *   Example: val sentence = "The cat stole the bacon."
 *                val value = "cat"
 *                The text transformations will be done on the "cat" according to the occurrences
 *                parameter in the builder
 * - [BOUNDARY]: Split text by boundary string or pattern; applies spans to delimited segments.
 *  Example: with the example above, "cat" will be untouched. The transformations may apply on
 *            either "The " or " stole the bacon."
 *
 * - [NONE]: No delimitation—operate on the entire sequence.
 */
sealed class DelimitationType {

    /**
     * Use a regex pattern to match delimited segments in the text.
     * @property value The regex string.
     * @property compiledRegexPattern The compiled Java Pattern.
     * @property regex Kotlin Regex equivalent.
     */
    data class REGEX(val value: String) : DelimitationType() {
        /**
         * The compiled Java Pattern for the regex string.
         */
        val compiledRegexPattern: Pattern? = try {
            Pattern.compile(value)
        } catch (e: PatternSyntaxException) {
            null
        }

        /**
         * The Kotlin Regex equivalent of the compiled Java Pattern.
         */
        val regex: Regex? = compiledRegexPattern?.toRegex()
    }

    /**
     * Use a literal string as the boundary delimiter (splitting on this string).
     * @property value The boundary string (plain or regex).
     * @property compiledRegexPattern The compiled Java Pattern for splitting.
     */
    data class BOUNDARY(val value: String) : DelimitationType() {
        /**
         * The compiled Java Pattern for the boundary string.
         */
        val compiledRegexPattern: Pattern = Pattern.compile(value)
    }

    /**
     * No delimitation—operate on the whole input.
     */
    object NONE : DelimitationType()
}