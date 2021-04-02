package com.athompson.cafe.extensions

import android.graphics.drawable.Drawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ChooseDialogBinding
import com.athompson.cafe.databinding.EditDialogBinding
import com.athompson.cafe.databinding.EditDialogLongBinding
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.DoubleExtensions.priceValue
import com.athompson.cafelib.extensions.DoubleExtensions.toPrice
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.reflect.KFunction3

object ViewExtensions {

    fun ImageView.setImage(imageUrl:String?,drawable: Int)
    {
        if (imageUrl.isNullOrEmpty())
            this.setImageResource(drawable)
        else
            GlideLoader(context).loadImagePicture(imageUrl, this)
    }

    fun TextView.choose(title:String?, hint:String, list: Array<String>, onChanged: KFunction3<View, String?, String?, Unit>) {

        val initialValue = this.text.toString()
        var result = initialValue
        val builder = MaterialAlertDialogBuilder(this.context, R.style.MaterialAlertDialog_Rounded)
        builder.setTitle(title)
        builder.setView( R.layout.choose_dialog)
        val binding = ChooseDialogBinding.inflate(LayoutInflater.from(context))
        binding.autocomplete.hint = hint
        binding.autocomplete.setText(initialValue,false)
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, list)
        binding.autocomplete.setAdapter(adapter)
        binding.autocomplete.setOnItemClickListener{ _: AdapterView<*>, view1: View, position: Int, id: Long ->
            result = list[position]
        }
        builder.setView(binding.root)
        builder.setPositiveButton("OK") {
                dialogInterface, i ->
            if(initialValue!=result) {
                this.text = result
                onChanged(this,initialValue,initialValue)
            }
            dialogInterface.dismiss()
        }
        builder.show()
    }
    fun TextView.edit(title:String?, hint:String, onChanged: KFunction3<View, String?, String?, Unit>,inputType:Int=InputType.TYPE_CLASS_TEXT) {
        val builder = MaterialAlertDialogBuilder(this.context, R.style.MaterialAlertDialog_Rounded)
        val initialValue = this.text.toString()
        builder.setTitle(title)
        builder.setView( R.layout.edit_dialog)
        val binding = EditDialogBinding.inflate(LayoutInflater.from(context))
        binding.textInput.hint = hint
        binding.editText.inputType = inputType
        binding.editText.setText(this.text)
        builder.setView(binding.root)
        builder.setPositiveButton("OK") {
                dialogInterface, i ->
            if(binding.editText.text.toString()!=this.text.toString()) {
                this.text = binding.editText.text.toString()
                onChanged(this,initialValue,binding.editText.text.toString())
            }
            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun TextView.editCurrency(title:String?, hint:String, onChanged: KFunction3<View, String?, String?, Unit>) {
        val builder = MaterialAlertDialogBuilder(this.context, R.style.MaterialAlertDialog_Rounded)
        val initialValue = this.text.toString()
        builder.setTitle(title)
        builder.setView( R.layout.edit_dialog)
        val binding = EditDialogBinding.inflate(LayoutInflater.from(context))
        binding.textInput.hint = hint
        binding.editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.editText.setText(this.text)
        builder.setView(binding.root)
        builder.setPositiveButton("OK") {
                dialogInterface, i ->
            if(binding.editText.text.toString()!=this.text.toString()) {
                val textValue = binding.editText.text.toString().trimStart('?')
                this.text = textValue.priceValue().toPrice()
                onChanged(this,initialValue,this.text.toString())
            }
            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun TextView.editLong(title:String?, hint:String, onChanged: KFunction3<View, String?, String?, Unit>) {
        val builder = MaterialAlertDialogBuilder(this.context,R.style.MaterialAlertDialog_Rounded)
        val initialValue = this.text.toString()
        builder.setTitle(title)
        builder.setView( R.layout.edit_dialog_long)
        val binding = EditDialogLongBinding.inflate(LayoutInflater.from(context))
        binding.textInput.hint = hint
        binding.editText.setText(this.text)
        builder.setView(binding.root)
        builder.setPositiveButton("OK") {
                dialogInterface, i ->
            if(binding.editText.text.toString()!=this.text.toString()) {
                this.text = binding.editText.text.toString()
                onChanged(this,initialValue,binding.editText.text.toString())
            }
            dialogInterface.dismiss()
        }
        builder.show()
    }
}