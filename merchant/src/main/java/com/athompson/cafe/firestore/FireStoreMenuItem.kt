package com.athompson.cafe.firestore

import com.athompson.cafe.ui.fragments.menu.MenuFragment
import com.athompson.cafelib.models.FoodMenuItem
import com.athompson.cafelib.shared.CafeQRApplication
import com.athompson.cafelib.shared.SharedConstants.MENUS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1


class FireStoreMenuItem {

    // Access a Cloud Firestore instance.
    private val mFireStore = FirebaseFirestore.getInstance()

    fun getMenuItems(
        success: KFunction1<ArrayList<FoodMenuItem>, Unit>,
        failure: (Exception) -> Unit
    ) {
        mFireStore.collection(MENUS)
            .whereEqualTo("uid", CafeQRApplication.selectedVenue?.uid)
            .get()
            .addOnSuccessListener { document ->
                val menuList: ArrayList<FoodMenuItem> = ArrayList()
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

    fun addMenuItem(success: KFunction0<Unit>, failure: (Exception) -> Unit, menu: FoodMenuItem) {
        mFireStore.collection(MENUS)
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
