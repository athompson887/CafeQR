package com.athompson.cafe.firestore

import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.shared.SharedConstants.MENUS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1


class FireStoreMenu {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun getMenus(
        success: KFunction1<ArrayList<CafeQrMenu>, Unit>,
        failure: (Exception) -> Unit
    ) {
        mFireStore.collection(MENUS)
            .get()
            .addOnSuccessListener { document ->
                val menuList: ArrayList<CafeQrMenu> = ArrayList()
                for (i in document.documents) {

                    val menu = i.toObject(CafeQrMenu::class.java)
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


    fun deleteCafeQrMenu(   success: KFunction0<Unit>,
                            failure: (Exception) -> Unit, menuItemID: String) {
        mFireStore.collection(MENUS)
            .document(menuItemID)
            .delete()
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }

    fun addCafeQrMenu(
        success: KFunction0<Unit>,
        failure: (Exception) -> Unit,
        menu: CafeQrMenu
    ) {
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
