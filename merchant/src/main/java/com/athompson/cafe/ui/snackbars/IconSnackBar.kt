package  com.athompson.cafe.ui.snackbars

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.athompson.cafe.R
import com.athompson.cafelib.extensions.ResourceExtensions.asColor
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback

class IconSnackbar(
    parent: ViewGroup,
    content: IconSnackbarView
) : BaseTransientBottomBar<IconSnackbar>(parent, content, content) {


    companion object {
        fun make(viewGroup: ViewGroup, message: String, duration: Int): IconSnackbar {
            val customView = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.icon_snackbar,
                viewGroup,
                false
            ) as IconSnackbarView

            customView.message.text = message

            return IconSnackbar(viewGroup, customView).setDuration(duration)
        }
    }
}

class IconSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    var message: TextView

    init {
        View.inflate(context, R.layout.icon_snackbar_view, this)
        message = findViewById(R.id.message)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        AlphaAnimation(0F, 1F).apply {
            interpolator = DecelerateInterpolator()
            setDuration(500)
        }.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        AlphaAnimation(1F, 0F).apply {
            interpolator = AccelerateInterpolator()
            setDuration(500)
        }.start()
    }
}