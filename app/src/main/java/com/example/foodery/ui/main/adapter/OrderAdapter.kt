package com.example.foodery.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foodery.R
import com.example.foodery.data.model.Order
import kotlinx.android.synthetic.main.layout_item.view.itemNameView
import kotlinx.android.synthetic.main.layout_item.view.normalPriceView
import kotlinx.android.synthetic.main.layout_order_item.view.*
import kotlinx.android.synthetic.main.layout_order_item.view.totalPriceValue

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_order_item, parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.itemView.apply {
            itemNameView.text = order.itemName

            orderStatusTextView.text = context.getString(R.string.order_status_string, order.status)

            if (order.unitPrice!! > 500) {
                normalPriceView.setBackgroundResource(R.drawable.price_tag_high_bg)
            }
            normalPriceView.text = context.getString(R.string.price_string, order.unitPrice)

            quantityValue.text = order.totalItem.toString()
            totalPriceValue.text = context.getString(R.string.price_string, order.totalItem)
            userLocationValue.text = order.location
            orderDate.text = context.getString(R.string.date_string, order.orderDate)


            setOnClickListener {
                onItemClickListener?.let { it(order) }
            }
        }
    }

    private var onItemClickListener: ((Order) -> Unit)? = null

    fun setOnClickListener(listener: (Order) -> Unit) {
        onItemClickListener = listener
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }

        }
    }
}