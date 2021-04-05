package com.athompson.cafe.ui.progress

import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ProgressDialogViewBinding
import com.athompson.cafelib.extensions.ColorExtensions.colorToHexString
import com.athompson.cafelib.extensions.ContextExtensions.getColorFromAttr

class CafeQrProgress {

    lateinit var dialog: CustomDialog
    lateinit var binding: ProgressDialogViewBinding
    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        binding = ProgressDialogViewBinding.inflate(LayoutInflater.from(context))
        if (title != null) {
            binding.cpTitle.text = title
        }
        binding.cpCardview.setCardBackgroundColor(Color.parseColor("#70000000"))
        binding.cpTitle.setTextColor(context.getColorFromAttr( R.attr.colorOnPrimary))
        dialog = CustomDialog(context)
        dialog.setContentView(binding.root)
        dialog.show()
        return dialog
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            // Set Semi-Transparent Color for Dialog Background
            val hexColourString = context.getColorFromAttr( R.attr.colorPrimary).colorToHexString().substringAfter("#")
            window?.decorView?.rootView?.setBackgroundColor(Color.parseColor("#33".plus(hexColourString)))
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}