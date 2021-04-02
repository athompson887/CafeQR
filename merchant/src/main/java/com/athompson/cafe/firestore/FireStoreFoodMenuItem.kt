package com.athompson.cafe.firestore

import com.athompson.cafelib.models.FoodMenuItem
import com.athompson.cafelib.shared.SharedConstants.FOOD_MENU_ITEM
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1


class FireStoreFoodMenuItem {


    private val mFireStore = FirebaseFirestore.getInstance()

    fun getAll(
        selectedMenuId: String,
        success: KFunction1<ArrayList<FoodMenuItem?>, Unit>,
        failure: (Exception) -> Unit
    ) {
        mFireStore.collection(FOOD_MENU_ITEM)
            .whereEqualTo("menuId", selectedMenuId)
            .get()
            .addOnSuccessListener { document ->
                val menuList: ArrayList<FoodMenuItem?> = ArrayList()
                for (i in document.documents) {
                    val menu = i.toObject(FoodMenuItem::class.java)
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


    fun delete(
        success: KFunction0<Unit>,
        failure: (Exception) -> Unit, menuItemID: String
    ) {
        mFireStore.collection(FOOD_MENU_ITEM)
            .document(menuItemID)
            .delete()
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }

    fun add(success: KFunction0<Unit>, failure: (Exception) -> Unit, menu: FoodMenuItem) {
        mFireStore.collection(FOOD_MENU_ITEM)
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
        item: FoodMenuItem
    ) {
        mFireStore.collection(FOOD_MENU_ITEM)
            .document(id)
            .set(item, SetOptions.merge())
            .addOnSuccessListener {
                success()
            }
            .addOnFailureListener { e ->
                failure(e)
            }
    }
}
