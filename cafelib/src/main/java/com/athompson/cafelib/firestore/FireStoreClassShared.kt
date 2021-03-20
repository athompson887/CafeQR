package com.athompson.cafelib.firestore


import android.util.Log
import androidx.fragment.app.Fragment
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.shared.SharedConstants.ORGANISATIONS
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A custom class where we will add the operation performed for the FireStore database.
 */
class FireStoreClassShared {

    // Access a Cloud Firestore instance.
    private val mFireStore = FirebaseFirestore.getInstance()


    fun getOrganisationList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(ORGANISATIONS)
            .whereEqualTo("uid", "2311a62b-e154-494e-a68c-ef573a25d181")
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Organisations List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val orgList: ArrayList<CafeQrMenu> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {
                    val org = i.toObject(CafeQrMenu::class.java)
                    if (org != null) {
                        orgList.add(org)
                    }
                }

//                when (fragment) {
//                    is DashboardFragment -> {
//                        fragment.successfulOrganisationsList(orgList)
//                    }
//                    is OrganisationsFragment -> {
//                        fragment.successfulOrganisationsList(orgList)
//                    }
//                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                //   when (fragment) {
                //    is DashboardFragment -> {
                //      fragment.failureOrganisationList(e)
                //   }
                //  }
            }
    }


}
