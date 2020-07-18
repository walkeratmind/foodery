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
import com.example.foodery.R
import com.example.foodery.databinding.FragmentHomeBinding
import com.example.foodery.ui.main.adapter.ItemAdapter
import com.example.foodery.ui.main.viewmodel.HomeViewModel
import com.example.foodery.utils.Resource
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        initItems()

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
                        Snackbar.make(fragmentHomeBinding.root, "Error:  $message", Snackbar.LENGTH_SHORT)
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