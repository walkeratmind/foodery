package com.example.foodery.data.model

data class Item(
    var id:String? = null,
    val name:String,
    val type: String,
    val category:String,
    val normalPrice:Int,
    val offerPrice:Int,
    val description:String
)