package com.athompson.cafe.ui.activities

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.SharedConstants

class AddVenueActivityContract : ActivityResultContract<String, Venue?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        Toast.makeText(context, input, Toast.LENGTH_LONG).show()
        return Intent(context, AddVenuesActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Venue? {
        return when (resultCode) {
            RESULT_OK -> intent?.getParcelableExtra(SharedConstants.VENUE)
            else -> null
        }
    }
}
