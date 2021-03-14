package com.athompson.cafe.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.athompson.cafe.Constants
import com.athompson.cafelib.models.Menu
import com.athompson.cafelib.models.Organisation
import com.athompson.cafelib.models.User
import com.athompson.cafelib.models.Venue
import com.athompson.cafe.ui.activities.*
import com.athompson.cafe.ui.fragments.dashboard.DashboardFragment
import com.athompson.cafe.ui.fragments.organisations.OrganisationsFragment
import com.athompson.cafe.ui.fragments.venues.VenuesFragment
import com.athompson.cafelib.shared.SharedConstants.MENUS
import com.athompson.cafelib.shared.SharedConstants.ORGANISATIONS
import com.athompson.cafelib.shared.SharedConstants.USERS
import com.athompson.cafelib.shared.SharedConstants.VENUES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/**
 * A custom class where we will add the operation performed for the FireStore database.
 */
class FireStoreClass {

    // Access a Cloud Firestore instance.
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.userRegistrationFailure()
            }
    }

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(activity: Activity) {

        mFireStore.collection(USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                if (user != null) {
                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user?.firstName} ${user.lastName}"
                    )
                }
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        if (user != null) {
                            activity.userLoggedInSuccess(user)
                        }
                    }

                    is SettingsActivity -> {
                        if (user != null) {
                            activity.userDetailsSuccess(user)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }


    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFireStore.collection(USERS)
            .document(getCurrentUserID())

            .update(userHashMap)
            .addOnSuccessListener {

                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata?.reference?.downloadUrl.toString()
                )

                taskSnapshot.metadata?.reference?.downloadUrl
                    ?.addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddOrganisationActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            else -> {

                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName, exception.message, exception
                )
            }
    }

    fun deleteOrganisation(fragment: OrganisationsFragment, organisationId: String) {

        mFireStore.collection(ORGANISATIONS)
            .document(organisationId)
            .delete()
            .addOnSuccessListener {
                fragment.deleteOrganisationDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                fragment.deleteOrganisationDeleteFailure(e)
            }
    }


    fun addOrganisation(activity: AddOrganisationActivity, productInfo: Organisation) {

        mFireStore.collection(ORGANISATIONS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addOrganisationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                activity.addOrganisationFailure()
            }
    }



    fun getOrganisationList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(ORGANISATIONS)
            .whereEqualTo("userId", getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Organisations List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val orgList: ArrayList<Organisation> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {
                    val org = i.toObject(Organisation::class.java)
                    if (org != null) {
                        orgList.add(org)
                    }
                }

                when (fragment) {
                    is DashboardFragment -> {
                        fragment.successfulOrganisationsList(orgList)
                    }
                    is OrganisationsFragment -> {
                        fragment.successfulOrganisationsList(orgList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is DashboardFragment -> {
                        fragment.failureOrganisationList(e)
                    }
                }
            }
    }


    fun getVenueItemsList(fragment: DashboardFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(VENUES)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val venuesList: ArrayList<Venue> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val venue = i.toObject(Venue::class.java)!!
                    venue.organisationId = i.id
                    venuesList.add(venue)
                }

                // Pass the success result to the base fragment.
                fragment.successVenuesList(venuesList)
            }
            .addOnFailureListener { e ->
                fragment.failureVenueList(e)
            }
    }

    fun getMenusList(fragment: DashboardFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(MENUS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val menuList: ArrayList<Menu> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val menu = i.toObject(Menu::class.java)
                    menu?.organisationId = i.id
                    if (menu != null) {
                        menuList.add(menu)
                    }
                }

                // Pass the success result to the base fragment.
                fragment.successfulMenuList(menuList)
            }
            .addOnFailureListener { e ->
                fragment.failureMenuList(e)
            }
    }

    fun deleteVenue(venuesFragment: VenuesFragment, venueID: String) {

    }

}
