package com.athompson.cafe.firestore

import android.content.Context
import android.content.SharedPreferences
import com.athompson.cafe.Constants
import com.athompson.cafelib.models.User
import com.athompson.cafelib.shared.CafeQRApplication
import com.athompson.cafelib.shared.SharedConstants
import com.athompson.cafelib.shared.SharedConstants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KFunction1


class FireStoreUser {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun add(success: KFunction1<User, Unit>, failure: KFunction1<Exception, Unit>, userInfo: User) {
        mFireStore.collection(USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                success(User())
            }
            .addOnFailureListener {
                failure(it)
            }
    }


    private fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun get(success: KFunction1<User, Unit>, failure: KFunction1<Exception, Unit>) {

        mFireStore.collection(USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)

                val sharedPreferences = CafeQRApplication.appInstance.getSharedPreferences(
                    SharedConstants.PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                if (user != null) {
                    editor.putString(
                        SharedConstants.LOGGED_IN_USERNAME,
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

    fun update(
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
}

