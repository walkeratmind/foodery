package com.example.foodery.data.model

data class Order(
    val id:String?,
    val userId:String?,
//    val userDetail: MutableList<String?>,
    val userName: String?,
    val userEmail: String?,
    val userPhone:String?,
    val itemId: String?,
    val itemName: String?,
    val unitPrice:Int?,
    val imageUri: String?,
    val location:String?,
    val totalItem: Int?,
    val totalPrice: Int?,
    val orderDate: String?,
    val status: String?  // orderStatus = PROCESS, DElIVERED, CANCELED
)