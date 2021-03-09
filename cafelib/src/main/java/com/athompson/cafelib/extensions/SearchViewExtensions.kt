package com.athompson.cafelib.extensions

import android.graphics.Typeface
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.athompson.cafelib.R

object SearchViewExtensions {

    fun SearchView.setTypeFace(typeface: Typeface?) {
        val searchEditText = this.findViewById<View>(R.id.search_src_text) as EditText
        val searchText = searchEditText as TextView
        searchText.typeface = typeface
    }

    fun SearchView.setTextColourLight() {
        val searchEditText = this.findViewById<View>(R.id.search_src_text) as EditText
        searchEditText.setTextColor(ContextCompat.getColor(this.context, R.color.white))
        searchEditText.setHintTextColor(ContextCompat.getColor(this.context, R.color.white))
    }
}