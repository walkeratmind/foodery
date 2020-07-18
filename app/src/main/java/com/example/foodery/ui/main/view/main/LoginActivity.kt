package com.example.foodery.ui.main.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.foodery.R
import com.example.foodery.data.model.UserModel
import com.example.foodery.ui.main.view.base.BaseActivity
import com.example.foodery.utils.Constants
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_register.view.*

class LoginActivity : BaseActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private var userRef: DatabaseReference? = null


    //    private var dialog: AlertDialog? = null
    private var providers: List<AuthUI.IdpConfig>? = null

    companion object {
        private const val RC_SIGN_IN = 4000
        private const val TAG = "LoginActivity"
    }

    override fun onStart() {
        super.onStart()
//        firebaseAuth!!.addAuthStateListener { authStateListener!! }
    }

    override fun onStop() {
//        firebaseAuth!!.removeAuthStateListener { authStateListener!! }
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setProgressBar(progressBar)
        init()

    }

    private fun init() {

        providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("np").build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        userRef = FirebaseDatabase.getInstance().getReference(Constants.USER_REF)


        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth!!.currentUser
        if (user != null) {
            checkUserLocation(user)
            Log.d(TAG, "user login")

        } else {
            Log.d(TAG, "User not login")
            //                startActivity(Intent(this, LoginActivity::class.java))
            //                finish()
            signIn()
        }
    }

    private fun gotoMainActivity(userModel: UserModel) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun checkUserLocation(user: FirebaseUser) {
        showProgressBar()
        userRef!!.child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    hideProgressBar()
                    Toast.makeText(this@LoginActivity, " ${error.message}", Toast.LENGTH_SHORT)
                        .show()

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        if (userModel?.location != null) {
                            hideProgressBar()
                            gotoMainActivity(userModel)
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                this@LoginActivity,
                                "Enter your locaton",
                                Toast.LENGTH_SHORT
                            ).show()
                            showRegisterDialog(user)

                        }

                    } else {
                        hideProgressBar()
                        Toast.makeText(
                            this@LoginActivity,
                            "Enter your locaton",
                            Toast.LENGTH_SHORT
                        ).show()
                        showRegisterDialog(user)

                    }
                }
            })
    }

    private fun showRegisterDialog(user: FirebaseUser) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Register")
        builder.setCancelable(false)
        builder.setMessage("Please submit the correct location")

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null)


        if (user.displayName != null) {
            itemView.name.setText(user.displayName!!)
        }
        if (user.email != null) {
            itemView.email.setText(user.email!!)
        }
        if (user.phoneNumber != null) {
            itemView.phone.setText(user.phoneNumber!!)
        }



        builder.setPositiveButton("Register") { _, _ ->
                if (TextUtils.isEmpty(itemView.name.text)) {
                    itemView.name.error = "Required"
                }
                if (TextUtils.isEmpty(itemView.email.text)) {
                    itemView.email.error = "Required"
                }

                val userModel = UserModel()
                userModel.uid = user.uid
                userModel.name = itemView.name.text.toString().trim()
                userModel.email = itemView.email.text.toString().trim()

                userModel.phone = itemView.phone.text.toString().trim()

                userModel.location = itemView.location.text.toString().trim()

                showProgressBar()
                userRef!!.child(userModel.uid!!)
                    .setValue(userModel)
                    .addOnFailureListener { e ->
                        hideProgressBar()
                        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener { task ->
                        hideProgressBar()
                        Toast.makeText(
                            this,
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        gotoMainActivity(userModel)
                    }

            }


        builder.setView(itemView)
        val registerDialog = builder.create()
        registerDialog.show()

    }

    private fun signIn() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers!!)
                .setTheme(R.style.AppTheme)
                .setLogo(R.drawable.logo)
                .build(), RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                checkUserLocation(user!!)

            } else {
                Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show()
//                Snackbar.make(, R.string.login_failed,Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }
}