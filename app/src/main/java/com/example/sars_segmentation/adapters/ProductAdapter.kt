package com.example.sars_segmentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ProductAdapter(private val productList : ArrayList<Product>, private var onItemClickListener: OnItemClickListener?):RecyclerView.Adapter<ProductViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product,parent, false)
        return ProductViewHolder(view,onItemClickListener)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.txName.text = product.Product_Name
        holder.txCategory.text = product.Category
        holder.txPrice.text = product.Price


        val imageUrl = product.ImageURL
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.user) // Optional: Placeholder image while loading
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun getProduct(position: Int): Product {
        return productList[position]
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

}

