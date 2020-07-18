package com.example.foodery.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.foodery.R
import com.example.foodery.data.model.Item
import com.example.foodery.data.model.Order
import com.example.foodery.databinding.FragmentOrderBinding
import com.example.foodery.ui.main.adapter.OrderAdapter
import com.example.foodery.ui.main.viewmodel.OrderViewModel
import com.example.foodery.utils.Constants
import com.example.foodery.utils.Resource
import com.example.foodery.utils.fadeInFadeOut
import com.example.foodery.utils.showSnackbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_item_dialog.*
import java.text.SimpleDateFormat
import java.util.*

class OrderFragment :Fragment() {
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var fragmentOrderBinding: FragmentOrderBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        fragmentOrderBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!

        return fragmentOrderBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        // onItem Clicked
        orderAdapter.setOnClickListener { order ->
//            showOrderDialog(order)
        }

        initItems()
    }

    private fun setupUI() {

        orderAdapter = OrderAdapter()

        fragmentOrderBinding.orderRecyclerView.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initItems() {
        orderViewModel.orderItems.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            when (response) {
                is Resource.Success -> {
                    showLoading(false)
                    response.data?.let { orderResponse ->
                        orderAdapter.differ.submitList(orderResponse.toMutableList())
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        showLoading(false)
                        Log.e(TAG, "Error: $message")
                        Snackbar.make(
                            fragmentOrderBinding.root,
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

        fragmentOrderBinding.swipeRefreshLayout.setOnRefreshListener {
            getFoodItems()
        }

        // reload if not success
        if (orderViewModel.orderItems.value !is Resource.Success) {
            getFoodItems()
        }

    }


    private fun getFoodItems() {
        orderViewModel.getOrderItems()
    }

    private fun showLoading(isLoading: Boolean) {
        fragmentOrderBinding.swipeRefreshLayout.isRefreshing = isLoading
    }

    companion object {
        var TAG = "OrderFragment"
    }

}