package com.athompson.cafe.firestore

import android.util.Log
import com.athompson.cafe.ui.activities.AddVenuesActivity
import com.athompson.cafe.ui.fragments.dashboard.DashboardFragment
import com.athompson.cafe.ui.fragments.venues.VenuesFragment
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.CafeQRApplication
import com.athompson.cafelib.shared.SharedConstants.VENUES
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KFunction1


class FireStoreVenue {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun getVenueItemsList(
        success: KFunction1<ArrayList<Venue>, Unit>,
        failure: (Exception) -> Unit
    ) {

        mFireStore.collection(VENUES)
            .whereEqualTo("organisationId", CafeQRApplication.selectedCafeQrMenu?.uid)
            .get()
            .addOnSuccessListener { document ->

                val venuesList: ArrayList<Venue> = ArrayList()
                for (i in document.documents) {

                    val venue = i.toObject(Venue::class.java)!!
                    //venue.organisationId = i.id
                    venuesList.add(venue)
                }

                success(venuesList)
            }
            .addOnFailureListener { e ->

                failure(e)
            }
    }


    fun deleteVenue(venuesFragment: VenuesFragment, venueID: String) {
        mFireStore.collection(VENUES)
            .document(venueID)
            .delete()
            .addOnSuccessListener {
                venuesFragment.deleteVenueDeleteSuccess()
            }
            .addOnFailureListener { e ->
                venuesFragment.hideProgressDialog()
                venuesFragment.deleteVenueDeleteFailure(e)
            }
    }

    fun addVenue(addVenuesActivity: AddVenuesActivity, venue: Venue) {
        mFireStore.collection(VENUES)
            .document()
            .set(venue, SetOptions.merge())
            .addOnSuccessListener {
                addVenuesActivity.addVenueSuccess()
            }
            .addOnFailureListener { e ->
                addVenuesActivity.hideProgressDialog()
                addVenuesActivity.addVenueFailure()
            }
    }
}
