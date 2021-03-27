package com.athompson.cafe.firestore

import com.athompson.cafelib.models.FoodMenuItem
import com.athompson.cafelib.shared.SharedConstants.MENU_ITEM
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1


class FireStoreMenuItem {


    private val mFireStore = FirebaseFirestore.getInstance()

    fun getMenuItems(
        mUid:String,
        success: KFunction1<ArrayList<FoodMenuItem?>, Unit>,
        failure: (Exception) -> Unit
    ) {
        mFireStore.collection(MENU_ITEM)
            .whereEqualTo("uid", mUid)
            .get()
            .addOnSuccessListener { document ->
                val menuList: ArrayList<FoodMenuItem?> = ArrayList()
                for (i in document.documents) {

                    val menu = i.toObject(FoodMenuItem::class.java)
                    menu?.uid = i.id
                    if (menu != null) {
                        menuList.add(menu)
                    }
                }
                success(menuList)
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }


    fun deleteMenuItem(   success: KFunction0<Unit>,
                          failure: (Exception) -> Unit, menuItemID: String) {
        mFireStore.collection(MENU_ITEM)
            .document(menuItemID)
            .delete()
            .addOnSuccessListener {
               success()
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }

    fun addMenuItem(success: KFunction0<Unit>, failure: (Exception) -> Unit, menu: FoodMenuItem) {
        mFireStore.collection(MENU_ITEM)
            .document()
            .set(menu, SetOptions.merge())
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }
}
