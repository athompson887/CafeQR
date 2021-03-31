package com.athompson.cafelib.extensions

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object ViewExtensions {

    fun RecyclerView.setLayoutManagerVertical()
    {
        this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    }
    fun RecyclerView.setLayoutManagerHorizontal()
    {
        this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
    }


    fun RecyclerView.showVerticalDividers() {
        this.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    fun RecyclerView.showHorizontalDividers() {
        this.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.HORIZONTAL
            )
        )
    }

    fun MenuItem.show(){
        this.isVisible = true
    }

    fun MenuItem.hide() {
        this.isVisible = false
    }

    fun EditText.trimmed(): String {
       return this.text.toString().trim { it <= ' ' }
    }
    fun EditText.isEmpty(): Boolean {
        val tr = this.trimmed()
        return tr.isEmpty()
    }

    fun TextView?.trimmed(): String {
        return this?.text.toString().trim()
    }


    fun View.show(){
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.INVISIBLE
    }

    fun View.remove(){
        this.visibility = View.GONE
    }

    fun View.hideKeyboard(): Boolean {
        try {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        } catch (ignored: RuntimeException) { }
        return false
    }

    fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.requestFocus()
        imm.showSoftInput(this, 0)
    }
}