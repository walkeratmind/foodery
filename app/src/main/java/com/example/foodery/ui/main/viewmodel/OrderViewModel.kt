package com.example.foodery.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodery.data.model.Order
import com.example.foodery.utils.Constants
import com.example.foodery.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class OrderViewModel : ViewModel() {

    private var user:FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    private var db = FirebaseFirestore.getInstance()
    val orderItems: MutableLiveData<Resource<MutableList<Order>>> = MutableLiveData()

    init {
        getOrderItems()
    }

    fun getOrderItems() = viewModelScope.launch {
        orderItems.postValue(Resource.Loading())

        // Read data from Cloud Firestore
        db.collection(Constants.USER_REF)
            .document(Constants.ORDER_REF).collection(Constants.ORDER_REF).get()
            .addOnSuccessListener { result ->
                val orderList: MutableList<Order>? = mutableListOf()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    /*
                    * null,
                    user.uid,
                    item.id,
                    item.name,
                    item.normalPrice,
                    null,
                    null,
                    totalItem,
                    totalPrice,
                    formattedDate,
                    "pending"*/


                    if (document["userId"].toString().equals(user.uid)) {
                        val order: Order = Order(
                            document.id,
                            document["userId"].toString(),
                            document["userName"].toString(),
                            document["userEmail"].toString(),
                            document["userPhone"].toString(),
                            document["itemId"].toString(),
                            document["itemName"].toString(),
                            document["unitPrice"].toString().toInt(),
                            null,
                            null,
                            document["totalItem"].toString().toInt(),
                            document["totalPrice"].toString().toInt(),
                            document["orderDate"].toString(),
                            document["status"].toString()
                        )
                        Log.d(TAG, "Item: ${order}")
                        orderList?.add(order)
                    }
                }
                orderItems.postValue(Resource.Success(orderList!!))
            }
            .addOnFailureListener { exception ->
                orderItems.postValue(Resource.Error(exception.toString()))
                Log.w(TAG, "Error getting documents.", exception)
            }
    }


    companion object {
        const val TAG = "OrderViewModel"
    }

}