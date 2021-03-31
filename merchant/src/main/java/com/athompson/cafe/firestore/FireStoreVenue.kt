package com.athompson.cafe.firestore

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.athompson.cafe.Constants
import com.athompson.cafe.ui.fragments.venues.VenuesFragment
import com.athompson.cafelib.models.User
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.SharedConstants
import com.athompson.cafelib.shared.SharedConstants.VENUES
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1


class FireStoreVenue {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun uploadImage(
        success: KFunction1<String, Unit>,
        failure: KFunction1<Exception, Unit>,
        imageFileURI: Uri,
        activity: Activity
    ) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageFileURI)
        )

        sRef.putFile(imageFileURI).addOnSuccessListener { taskSnapshot ->
            // The image upload is success
            Log.e("Firebase Image URL", taskSnapshot.metadata?.reference?.downloadUrl.toString())

            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                Log.e("Downloadable Image URL", uri.toString())

                success(uri.toString())
            }
        }
            .addOnFailureListener { exception ->
                failure(exception)
            }
    }

    fun getVenues(
        success: KFunction1<ArrayList<Venue>, Unit>,
        failure: (Exception) -> Unit
    ) {

        mFireStore.collection(VENUES)
            .get()
            .addOnSuccessListener { document ->

                val venuesList: ArrayList<Venue> = ArrayList()
                for (i in document.documents) {

                    val venue = i.toObject(Venue::class.java)!!
                    venue.id = i.id
                    venuesList.add(venue)
                }

                success(venuesList)
            }
            .addOnFailureListener { e ->

                failure(e)
            }
    }


    fun deleteVenue(   success: KFunction0<Unit>,
                       failure: (Exception) -> Unit, venueID: String) {
        mFireStore.collection(VENUES)
            .document(venueID)
            .delete()
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e ->
               failure(e)
            }
    }

    fun addVenue(success: KFunction0<Unit>, failure: KFunction1<Exception, Unit>, venue: Venue) {
        mFireStore.collection(VENUES)
            .document()
            .set(venue, SetOptions.merge())
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }


    fun updateVenue(
        success: KFunction1<Venue, Unit>,
        failure: KFunction1<Exception, Unit>,
        id:String,
        venueHashMap: HashMap<String, Any>
    ) {
        mFireStore.collection(VENUES)
            .document(id)
            .update(venueHashMap)
            .addOnSuccessListener {
                success(Venue())
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }

}
