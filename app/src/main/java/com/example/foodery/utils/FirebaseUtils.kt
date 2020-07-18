package com.example.foodery.utils

import com.example.foodery.data.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseUtils {
    fun getUserLocation(userId: String): String {
        val userRef = FirebaseDatabase.getInstance().getReference(Constants.USER_REF)
        var userLocation = "no"

        userRef.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        userLocation = userModel!!.location.toString()
                    }
                }
            })
        return userLocation
    }
}
