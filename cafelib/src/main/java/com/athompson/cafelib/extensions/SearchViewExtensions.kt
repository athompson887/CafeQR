package com.athompson.cafelib.extensions

import android.graphics.Typeface
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.athompson.cafelib.R
import com.athompson.cafelib.extensions.ResourceExtensions.asColor

object SearchViewExtensions {

    fun SearchView.setTypeFace(typeface: Typeface?) {
        val searchEditText = this.findViewById<View>(R.id.search_src_text) as EditText
        val searchText = searchEditText as TextView
        searchText.typeface = typeface
    }

    fun SearchView.setTextColourLight() {
        val searchEditText = this.findViewById<View>(R.id.search_src_text) as EditText
        searchEditText.setTextColor(R.color.white.asColor())
        searchEditText.setHintTextColor(R.color.white.asColor())
    }
}