package com.athompson.cafelib.extensions

import android.text.*
import android.text.style.*
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.athompson.cafelib.extensions.ResourceExtensions.asColor
import com.athompson.cafelib.shared.SharedConstants.ERROR_RETURN
import java.lang.NumberFormatException

object StringExtensions {

    fun String?.valid() : Boolean =
            this != null && !this.equals("null", true)
                    && this.trim().isNotEmpty()

    fun String?.convertDateString(): String? {
        val dateAndTime = this?.split(" ")
        val items = dateAndTime?.get(0)?.split("-")
        return if(items?.size?:0 > 2 )
            items?.get(2) + "/" + items?.get(1) + "/" + items?.get(0)
        else
            this
    }

    fun String?.safe(): String {
        if(this==null)
            return ""
        return when (this) {
            "string" -> ""
            "null" -> ""
            else -> this
        }
    }

    fun String?.toTitleCase(): String {

        if (this == null) {
            return ""
        }
        var space = true
        val builder = StringBuilder(this)
        val len = builder.length

        for (i in 0 until len) {
            val c = builder[i]
            if (space) {
                if (!Character.isWhitespace(c)) {
                    builder.setCharAt(i, Character.toTitleCase(c))
                    space = false
                }
            } else if (Character.isWhitespace(c)) {
                space = true
            } else {
                builder.setCharAt(i, Character.toLowerCase(c))
            }
        }
        return builder.toString()
    }

    fun SpannableString.withClickableSpan(clickablePart: String, onClickListener: () -> Unit): SpannableString {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) = onClickListener.invoke()
        }
        val clickablePartStart = indexOf(clickablePart)
        setSpan(clickableSpan,
            clickablePartStart,
            clickablePartStart + clickablePart.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    fun TextView.setColorOfSubstring(substring: String, color: Int) {
        try {
            val spannable = SpannableString(text)
            val start = text.indexOf(substring)
            spannable.setSpan(ForegroundColorSpan(color.asColor()), start, start + substring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            text = spannable
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun SpannableStringBuilder.spanText(span: Any): SpannableStringBuilder {
        setSpan(span, 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    private fun String.toSpannable() = SpannableStringBuilder(this)

    fun String.foregroundColor(@ColorInt color: Int): SpannableStringBuilder {
        val span = ForegroundColorSpan(color)
        return toSpannable().spanText(span)
    }

    fun String.backgroundColor(@ColorInt color: Int): SpannableStringBuilder {
        val span = BackgroundColorSpan(color)
        return toSpannable().spanText(span)
    }

    fun String.relativeSize(size: Float): SpannableStringBuilder {
        val span = RelativeSizeSpan(size)
        return toSpannable().spanText(span)
    }

    fun String.supserscript(): SpannableStringBuilder {
        val span = SuperscriptSpan()
        return toSpannable().spanText(span)
    }

    fun String.strike(): SpannableStringBuilder {
        val span = StrikethroughSpan()
        return toSpannable().spanText(span)
    }


    fun EditText.int(): Int {
        try {
            return this.text.toString().toInt()
        }catch (ex:NumberFormatException)
        {
            return ERROR_RETURN
        }
    }

    fun String.validEmail(): Boolean {
        if(this.isEmpty())
            return false
        val res = Patterns.EMAIL_ADDRESS.matcher(this).matches()
        return res
    }
}