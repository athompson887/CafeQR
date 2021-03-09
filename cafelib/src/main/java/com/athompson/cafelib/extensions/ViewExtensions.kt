package com.athompson.cafelib.extensions

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.athompson.cafelib.extensions.ViewExtensions.show

object ViewExtensions {

    fun MenuItem.show(){
        this.setVisible(true)
    }

    fun MenuItem.hide() {
        this.setVisible(false)
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