package com.athompson.cafe.firestore

import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.shared.SharedConstants.MENUS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1


class FireStoreCafeQrMenu {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun getAll(
        success: KFunction1<ArrayList<CafeQrMenu?>, Unit>,
        failure: (Exception) -> Unit
    ) {
        mFireStore.collection(MENUS)
            .get()
            .addOnSuccessListener { document ->
                val menuList: ArrayList<CafeQrMenu?> = ArrayList()
                for (i in document.documents) {

                    val menu = i.toObject(CafeQrMenu::class.java)
                    menu?.id = i.id
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


    fun delete(success: KFunction0<Unit>,
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

    fun add(
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

    fun update(
        success: KFunction0<Unit>,
        failure: KFunction1<Exception, Unit>,
        id: String,
        menu: CafeQrMenu
    ) {
        mFireStore.collection(MENUS)
            .document(id)
            .set(menu, SetOptions.merge())
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }
}
