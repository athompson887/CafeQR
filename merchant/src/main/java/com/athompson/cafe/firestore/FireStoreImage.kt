package com.athompson.cafe.firestore

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.athompson.cafe.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1


class FireStoreImage {

    fun deleteImage(success: KFunction0<Unit>,
                    failure: (Exception) -> Unit, imageUrl: String) {
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.getReferenceFromUrl(imageUrl)
        storageReference.delete().addOnSuccessListener{
            success()
        }
        storageReference.delete().addOnFailureListener(){
            failure(it)
        }
    }


    fun uploadImageToCloudStorage(
        activity: Activity,
        imageFileURI: Uri?,
        pathConstant:String?,
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

                    }
            }
            .addOnFailureListener { exception ->
                failure(exception)
            }
    }
}
