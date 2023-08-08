package com.example.sars_segmentation

//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.item_product.view.*
//
//class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    private val txName: TextView = itemView.findViewById(R.id.txName)
//    private val txCategory: TextView = itemView.findViewById(R.id.txCategory)
//    private val txPrice: TextView = itemView.findViewById(R.id.txPrice)
//    private val imageView: ImageView = itemView.findViewById(R.id.imgUser)
//
//    fun bind(product: Product) {
//        txName.text = product.Product_Name
//        txCategory.text = product.Category
//        txPrice.text = product.Price
//
//        Picasso.get()
//            .load(product.ImageURL)
//            .placeholder(R.drawable.user) // Optional: Placeholder image while loading
//            .into(imageView)
//    }
//}
//
//

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductViewHolder(itemView: View,  private val onItemClickListener: ProductAdapter.OnItemClickListener?):RecyclerView.ViewHolder(itemView) {
    val txName = itemView.findViewById<TextView>(R.id.txName)
    val txCategory = itemView.findViewById<TextView>(R.id.txCategory)
    val txPrice = itemView.findViewById<TextView>(R.id.txPrice)
    val imageView: ImageView = itemView.findViewById(R.id.imgUser)

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.onItemClick(position)
            }
        }
    }
}