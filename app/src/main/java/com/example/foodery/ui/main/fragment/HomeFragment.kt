package com.example.foodery.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.foodery.R
import com.example.foodery.data.model.Item
import com.example.foodery.data.model.Order
import com.example.foodery.databinding.FragmentHomeBinding
import com.example.foodery.ui.main.adapter.ItemAdapter
import com.example.foodery.ui.main.viewmodel.HomeViewModel
import com.example.foodery.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_item_dialog.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        fragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!

        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        // onItem Clicked
        itemAdapter.setOnClickListener { item ->
            showItemDialog(item)
        }

        initItems()

    }

    private fun showItemDialog(item: Item) {
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.layout_item_dialog, scrollable = true)

        dialog.itemNameView.text = item.name
        dialog.itemDescriptionView.text = item.description
        if (item.normalPrice > 500) {
            dialog.normalPriceView.setBackgroundResource(R.drawable.price_tag_high_bg)
        }
        dialog.normalPriceView.append(" ${item.normalPrice}")

        var totalItem: Int = 1
        var totalPrice: Int = item.normalPrice * totalItem

        dialog.totalPriceValue.text = requireContext().getString(
            R.string.price_string,
            (item.normalPrice * totalItem)
        )

        dialog.decrementQuantityBtn.setOnClickListener { view ->

            if (totalItem <= Constants.MIN_ITEM) {
                dialog.cartMessageTextView.apply {
                    text = getString(R.string.error_min_item)
                    fadeInFadeOut(animDuration = 3000L)
                }
            } else {
                totalItem--
                totalPrice = item.normalPrice * totalItem
                dialog.quantityValue.text = totalItem.toString()

                dialog.totalPriceValue.text = requireContext().getString(
                    R.string.price_string, totalPrice
                )
            }
        }

        dialog.incrementQuantityBtn.setOnClickListener { view ->
            if (totalItem >= Constants.MAX_ITEM) {
                dialog.cartMessageTextView.apply {
                    text = getString(R.string.error_max_item)
                    fadeInFadeOut(animDuration = 3000L)
                }
            } else {
                totalItem++
                totalPrice = item.normalPrice * totalItem
                dialog.quantityValue.text = totalItem.toString()

                dialog.totalPriceValue.text = requireContext().getString(
                    R.string.price_string,
                    totalPrice
                )

            }
        }

        val db = FirebaseFirestore.getInstance()

        dialog.orderBtn.setOnClickListener { view ->
            dialog.dismiss()

            /*
                val id:String,
                val itemId: String,
                val userId: String,
                val totalPrice: String,
                val orderDate: String,
                val status: String  //
            */
            val currentDate = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val formattedDate = df.format(currentDate)

            val order =
                Order(
                    null,
                    user.uid,
//                    mutableListOf(user.uid, user.displayName, user.email, user.phoneNumber),
                    user.displayName,
                    user.email,
                    user.phoneNumber,
                    item.id,
                    item.name,
                    item.normalPrice,
                    null,
                    null,
                    totalItem,
                    totalPrice,
                    formattedDate,
                    "pending"
                )

            showLoading(true)
            db.collection(Constants.USER_REF)
                .document(Constants.ORDER_REF).collection(Constants.ORDER_REF).document()
                .set(order)
                .addOnSuccessListener { documentReference ->
                    showLoading(false)
                    Log.d(TAG, "DocumentSnapshot added: $documentReference")
                    requireActivity().showSnackbar(requireView(), getString(R.string.successful))
                }
                .addOnFailureListener { exception ->
                    showLoading(false)
                    Log.w(TAG, "Error adding document", exception)
                    requireActivity().showSnackbar(requireView(), getString(R.string.failed))
                }
        }

        dialog.addToCartBtn.setOnClickListener { view ->
            dialog.dismiss()
            val currentDate = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val formattedDate = df.format(currentDate)

            val userLocation = FirebaseUtils.getUserLocation(user.uid)

            val order =
                Order(
                    null,
                    user.uid,
//                    mutableListOf(user.uid, user.displayName, user.email, user.phoneNumber),
                    user.displayName,
                    user.email,
                    user.phoneNumber,
                    item.id,
                    item.name,
                    item.normalPrice,
                    null,
                    userLocation,
                    totalItem,
                    totalPrice,
                    formattedDate,
                    "pending"
                )

            showLoading(true)
            db.collection(Constants.USER_REF)
                .document(Constants.ORDER_REF).collection(Constants.ORDER_REF).document()
                .set(order)
                .addOnSuccessListener { documentReference ->
                    showLoading(false)
                    Log.d(TAG, "DocumentSnapshot added: $documentReference")
                    requireActivity().showSnackbar(requireView(), getString(R.string.successful))
                }
                .addOnFailureListener { exception ->
                    showLoading(false)
                    Log.w(TAG, "Error adding document", exception)
                    requireActivity().showSnackbar(requireView(), getString(R.string.failed))
                }
        }

        dialog.show()
    }

    private fun setupUI() {

        itemAdapter = ItemAdapter()

        fragmentHomeBinding.itemRecyclerView.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initItems() {
        homeViewModel.foodItems.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    showLoading(false)
                    response.data?.let { itemResponse ->
                        itemAdapter.differ.submitList(itemResponse.toMutableList())
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        showLoading(false)
                        Log.e(TAG, "Error: $message")
                        Snackbar.make(
                            fragmentHomeBinding.root,
                            "Error:  $message",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                is Resource.Loading -> {
                    showLoading(true)
                    Log.d(TAG, "Loading...")
                }
            }
        })

        fragmentHomeBinding.swipeRefreshLayout.setOnRefreshListener {
            getFoodItems()
        }

        // reload if not success
        if (homeViewModel.foodItems.value !is Resource.Success) {
            getFoodItems()
        }

    }


    private fun getFoodItems() {
        homeViewModel.getFoodItems()
    }

    private fun showLoading(isLoading: Boolean) {
        fragmentHomeBinding.swipeRefreshLayout.isRefreshing = isLoading
    }

    companion object {
        var TAG = "HomeFragment"
    }


}