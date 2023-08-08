package com.example.sars_segmentation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_detail.btnBackpd

@Suppress("DEPRECATION")
class ProductDetailActivity : AppCompatActivity() {
    private lateinit var product: Product
    private lateinit var txName: TextView
    private lateinit var txCategory: TextView
    private lateinit var txPrice: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Initialize your UI views
        txName = findViewById(R.id.txName)
        txCategory = findViewById(R.id.txCategory)
        txPrice = findViewById(R.id.txPrice)
        imageView = findViewById(R.id.imgUser)

        // Retrieve the product from the intent
        product = intent.getParcelableExtra("product")!!

        if (product != null) {
            // Update your UI with the product details
            txName.text = product.Product_Name
            txCategory.text = product.Category
            txPrice.text = product.Price

            // Load the product image using Picasso or Glide
            Picasso.get()
                .load(product.ImageURL)
                .placeholder(R.drawable.user) // Optional: Placeholder image while loading
                .into(imageView)
        } else {
            // Handle the case when the product is null
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish() // Close the activity
        }

        btnBackpd.setOnClickListener {
            onBackClicked()
        }
    }

    private fun onBackClicked() {
            finish()
    }
}
