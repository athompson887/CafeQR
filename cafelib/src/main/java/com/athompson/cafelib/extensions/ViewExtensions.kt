package com.athompson.cafelib.extensions

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.athompson.cafelib.extensions.ViewExtensions.show

object ViewExtensions {

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