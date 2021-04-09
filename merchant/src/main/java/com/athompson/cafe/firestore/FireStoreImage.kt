package com.athompson.cafe.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.athompson.cafe.Constants
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.User
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.CafeQRApplication
import com.athompson.cafelib.shared.SharedConstants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1


class FireStoreImage {

    val referencedImages = ArrayList<String>()
    var allImages = ArrayList<StorageReference>()

    fun deleteImage(
        success: KFunction0<Unit>,
        failure: (Exception) -> Unit, imageUrl: String
    ) {
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.getReferenceFromUrl(imageUrl)
        storageReference.delete().
        addOnSuccessListener {
            success()
        }
        .addOnFailureListener {
            failure(it)
        }
    }

 /*   private fun deleteImageQuietly(imageUrl: String) {
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.getReferenceFromUrl(imageUrl)
        storageReference.
        delete().
        addOnSuccessListener {
            Log.d("deleteImageQuietly", imageUrl.safe() + "deleted")
        }
        .addOnFailureListener {
            Log.d("deleteImageQuietly", imageUrl.safe() + "failed to delete")
        }
    }*/

    fun listAllImages() {
        val storage = Firebase.storage
        val listRef = storage.reference.child("/")
        allImages.clear()
        referencedImages.clear()

        listRef.listAll()
            .addOnSuccessListener {
                allImages.addAll(it.items)
                getVenuesImages()
            }
    }

    private fun getVenuesImages() {
        FirebaseFirestore.getInstance().collection(SharedConstants.VENUES)
            .get()
            .addOnSuccessListener { document ->
                for (i in document.documents) {
                    val item = i.toObject(Venue::class.java)!!
                    if(item.imageUrl.isNotEmpty())
                        if(!referencedImages.contains(item.imageUrl))
                            referencedImages.add(item.imageUrl)
                }
                getUserImages()
            }
            .addOnFailureListener {
                getUserImages()
            }
    }

    private fun getUserImages() {
        FirebaseFirestore.getInstance().collection(SharedConstants.USERS)
            .get()
            .addOnSuccessListener { document ->
                for (i in document.documents) {
                    val item = i.toObject(User::class.java)!!
                    if(item.image.isNotEmpty())
                        if(!referencedImages.contains(item.image))
                            referencedImages.add(item.image)
                }
                getMenuImages()
            }
            .addOnFailureListener { e ->
                getMenuImages()
            }
    }

    private fun getMenuImages() {
        FirebaseFirestore.getInstance().collection(SharedConstants.MENUS)
            .get()
            .addOnSuccessListener { document ->
                for (i in document.documents) {
                    val item = i.toObject(CafeQrMenu::class.java)!!
                    if(item.imageUrl.isNotEmpty())
                        if(!referencedImages.contains(item.imageUrl))
                             referencedImages.add(item.imageUrl)
                }
                getFoodImages()
            }
            .addOnFailureListener { e ->
                getFoodImages()
            }
    }

    private fun getFoodImages() {
        FirebaseFirestore.getInstance().collection(SharedConstants.FOOD_MENU_ITEM)
            .get()
            .addOnSuccessListener { document ->
                for (i in document.documents) {
                    val item = i.toObject(CafeQrMenu::class.java)!!
                    if(item.imageUrl.isNotEmpty())
                        if(!referencedImages.contains(item.imageUrl))
                             referencedImages.add(item.imageUrl)
                }
              //deleteUnusedImages()
            }
            .addOnFailureListener { e ->
          //      deleteUnusedImages()
            }
    }

    private fun deleteUnusedImages()
    {
         allImages.forEach {
            val name = it.name
            var found = false
            referencedImages.forEach { ref ->
                if (ref.contains(name)) {
                    found = true
                }
            }
            if (!found)
                it.delete()
        }
    }

    fun uploadImageToCloudStorage(
        activity: Activity,
        imageFileURI: Uri?,
        prevUrl: String?,
        pathConstant: String?,
        success: KFunction1<String, Unit>,
        failure: KFunction1<Exception, Unit>
    ) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            pathConstant + System.currentTimeMillis() + "." + Constants.getFileExtension(
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
                        success(uri.toString())
                        if (!prevUrl.isNullOrEmpty()) {
                            //deleteImageQuietly(prevUrl)
                        }
                    }
            }
            .addOnFailureListener { exception ->
                failure(exception)
            }
    }
}
