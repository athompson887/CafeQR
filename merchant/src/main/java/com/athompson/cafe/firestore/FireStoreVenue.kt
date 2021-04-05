package com.athompson.cafe.firestore

import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.SharedConstants.VENUES
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2


class FireStoreVenue {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun getAll(
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


    fun delete(
        success: KFunction1<Int, Unit>,
        failure: KFunction2<Exception, Int, Unit>, venueID: String, position: Int
    ) {
        mFireStore.collection(VENUES)
            .document(venueID)
            .delete()
            .addOnSuccessListener {
                success(position)
            }
            .addOnFailureListener { e ->
                failure(e,position)
            }
    }

    fun add(success: KFunction0<Unit>, failure: KFunction1<Exception, Unit>, venue: Venue) {
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


    fun update(
        success: KFunction0<Unit>,
        failure: KFunction1<Exception, Unit>,
        id: String,
        venue: Venue
    ) {
        mFireStore.collection(VENUES)
            .document(id)
            .set(venue, SetOptions.merge())
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }
}
