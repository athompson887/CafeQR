package com.athompson.cafe.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.athompson.cafe.Constants
import com.athompson.cafe.ui.activities.RegisterActivity
import com.athompson.cafe.ui.activities.UserProfileActivity
import com.athompson.cafelib.models.User
import com.athompson.cafelib.shared.CafeQRApplication
import com.athompson.cafelib.shared.SharedConstants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.reflect.KFunction1


class FireStoreUser {

    private val mFireStore = FirebaseFirestore.getInstance()

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


    private fun getCurrentUserID(): String {
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
    fun getUserDetails(success: KFunction1<User, Unit>, failure: KFunction1<Exception, Unit>) {

        mFireStore.collection(USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)

                val sharedPreferences = CafeQRApplication.appInstance.getSharedPreferences(
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
                if (user != null) {
                    success(user)
                }
            }
            .addOnFailureListener { e ->
                failure(e)
            }


    }

    fun updateUserProfileData(
        success: KFunction1<User, Unit>,
        failure: KFunction1<Exception, Unit>,
        userHashMap: HashMap<String, Any>
    ) {
        mFireStore.collection(USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                success(User())
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }

    fun uploadImageToCloudStorage(
        success: KFunction1<String, Unit>,
        failure: KFunction1<Exception, Unit>,
        imageFileURI: Uri,
        activity:Activity
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
}



