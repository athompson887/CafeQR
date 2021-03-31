package com.athompson.cafe.extensions

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.EditDialogBinding
import com.athompson.cafe.databinding.EditDialogLongBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.reflect.KFunction3

object ViewExtensions {

    fun TextView.Edit(title:String?, hint:String, onChanged: KFunction3<View, String?, String?, Unit>) {
        val builder = MaterialAlertDialogBuilder(this.context, R.style.MaterialAlertDialog_Rounded)
        builder.setTitle(title)
        builder.setView( R.layout.edit_dialog)
        val binding = EditDialogBinding.inflate(LayoutInflater.from(context))
        binding.textInput.hint = hint
        binding.editText.setText(this.text)
        builder.setView(binding.root)
        builder.setPositiveButton("OK") {
                dialogInterface, i ->
            if(binding.editText.text.toString()!=this.text.toString()) {
                onChanged(this,this.text.toString(),binding.editText.text.toString())
                this.setText(binding.editText.text.toString())
            }
            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun TextView.EditLong(title:String?, hint:String, onChanged: KFunction3<View, String?, String?, Unit>) {
        val builder = MaterialAlertDialogBuilder(this.context,R.style.MaterialAlertDialog_Rounded)
        builder.setTitle(title)
        builder.setView( R.layout.edit_dialog_long)
        val binding = EditDialogLongBinding.inflate(LayoutInflater.from(context))
        binding.textInput.hint = hint
        binding.editText.setText(this.text)
        builder.setView(binding.root)
        builder.setPositiveButton("OK") {
                dialogInterface, i ->
            if(binding.editText.text.toString()!=this.text.toString()) {
                onChanged(this,this.text.toString(),binding.editText.text.toString())
                this.setText(binding.editText.text.toString())
            }
            dialogInterface.dismiss()
        }
        builder.show()
    }
}