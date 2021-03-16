package com.athompson.cafe.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.athompson.cafe.Constants
import com.athompson.cafe.ui.activities.*
import com.athompson.cafe.ui.fragments.dashboard.DashboardFragment
import com.athompson.cafe.ui.fragments.menu.MenuFragment
import com.athompson.cafe.ui.fragments.organisations.OrganisationsFragment
import com.athompson.cafe.ui.fragments.venues.VenuesFragment
import com.athompson.cafelib.models.FoodMenuItem
import com.athompson.cafelib.models.Organisation
import com.athompson.cafelib.models.User
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.CafeQRApplication
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
                        "${user.firstName} ${user.lastName}"
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
                            is AddVenuesActivity -> {
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
                    is AddOrganisationActivity -> {
                        activity.imageUploadFailure()
                    }
                    is AddVenuesActivity -> {
                        activity.imageUploadFailure()
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
        mFireStore.collection(ORGANISATIONS)
            .whereEqualTo("userId", getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Organisations List", document.documents.toString())
                val orgList: ArrayList<Organisation> = ArrayList()
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
                when (fragment) {
                    is DashboardFragment -> {
                        fragment.failureOrganisationList(e)
                    }
                }
            }
    }


    fun getVenueItemsList(fragment: Fragment) {

        mFireStore.collection(VENUES)
            .whereEqualTo("organisationId", CafeQRApplication.selectedOrganisation?.uid)
            .get()
            .addOnSuccessListener { document ->

                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val venuesList: ArrayList<Venue> = ArrayList()
                for (i in document.documents) {

                    val venue = i.toObject(Venue::class.java)!!
                    venue.organisationId = i.id
                    venuesList.add(venue)
                }

                when (fragment) {
                    is DashboardFragment -> {
                        fragment.successVenuesList(venuesList)
                    }
                    is VenuesFragment -> {
                        fragment.successfulVenuesList(venuesList)
                    }
                }
            }
            .addOnFailureListener { e ->

                when (fragment) {
                    is DashboardFragment -> {
                        fragment.failureVenueList(e)
                    }
                    is VenuesFragment -> {
                        fragment.failureVenueList(e)
                    }
                }
            }
    }

    fun getMenuItemsList(fragment: MenuFragment) {
        mFireStore.collection(MENUS)
            .whereEqualTo("uid", CafeQRApplication.selectedVenue?.uid)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val menuList: ArrayList<FoodMenuItem> = ArrayList()
                for (i in document.documents) {

                    val menu = i.toObject(FoodMenuItem::class.java)
                    menu?.uid = i.id
                    if (menu != null) {
                        menuList.add(menu)
                    }
                }
                fragment.successfulMenuList(menuList)
            }
            .addOnFailureListener { e ->
                fragment.failureMenuList(e)
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

    fun deleteMenuItem(menuFragment: MenuFragment, menuItemID: String) {
        mFireStore.collection(MENUS)
            .document(menuItemID)
            .delete()
            .addOnSuccessListener {
                menuFragment.deleteMenuDeleteSuccess()
            }
            .addOnFailureListener { e ->
                menuFragment.hideProgressDialog()
                menuFragment.deleteMenuDeleteFailure(e)
            }
    }

    fun addMenuItem(addMenuItemActivity: AddMenuItemActivity, menu: FoodMenuItem) {
        mFireStore.collection(MENUS)
            .document()
            .set(menu, SetOptions.merge())
            .addOnSuccessListener {
                addMenuItemActivity.addMenuItemSuccess()
            }
            .addOnFailureListener { e ->
                addMenuItemActivity.hideProgressDialog()
                addMenuItemActivity.addMenuItemFailure()
            }
    }
}
