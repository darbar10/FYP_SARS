package com.example.sars_segmentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.utils.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_products_display.*

class ProductsDisplayActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var productRecyclerview : RecyclerView
    private lateinit var recommendedProducts: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_display)

        // Retrieve the user name from the Intent
        val userName = intent.getStringExtra("userName")

        // Retrieve the recommended products from the Intent
        recommendedProducts = intent.getStringArrayListExtra("recommendedProducts") ?: ArrayList()


        // Get a reference to the TextView
        val txUserName: TextView = findViewById(R.id.txUserName)

        // Set the user name to the TextView
        txUserName.text = userName

        productRecyclerview = findViewById(R.id.ProductList)
        productRecyclerview.layoutManager = GridLayoutManager(this,2)
        productRecyclerview.setHasFixedSize(true)

        getProductData()

        btnSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun getProductData() {
        database = FirebaseDatabase.getInstance().getReference("Products")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val productArrayList = ArrayList<Product>()

                for (snapshot in dataSnapshot.children) {
                    val productName = snapshot.child("Product_Name").getValue(String::class.java)
                    val category = snapshot.child("Category").getValue(String::class.java)
                    val price = snapshot.child("Price").getValue(String::class.java)
                    val imageUrl = snapshot.child("ImageURL").getValue(String::class.java)

                    if (productName != null && category != null && price != null && imageUrl != null) {
                        val product = Product(productName, category, price, imageUrl)
                        productArrayList.add(product)
                    }
                }

                // Filter the products based on the recommended products list
                val filteredProducts = ArrayList<Product>()
                for (product in productArrayList) {
                    if (recommendedProducts.contains(product.Product_Name)) {
                        filteredProducts.add(product)
                    }
                }

                // Set the adapter for the RecyclerView
                val productAdapter = ProductAdapter(filteredProducts, object : ProductAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        // Handle item click event
                        // You can redirect to a new page with the selected product
                        val product = filteredProducts[position]
                        val intent = Intent(this@ProductsDisplayActivity, ProductDetailActivity::class.java)
                        intent.putExtra("product", product)
                        startActivity(intent)
                    }
                })
                productRecyclerview.adapter = productAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }


    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]
        Intent(this, SignInActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}

private fun Intent.putExtra(s: String, product: Product) {
    TODO("Not yet implemented")
}

//    private fun getProductData() {
//        database = FirebaseDatabase.getInstance().getReference("Products")
//
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val productArrayList = ArrayList<Product>()
//
//                for (snapshot in dataSnapshot.children) {
//                    val productName = snapshot.child("Product_Name").getValue(String::class.java)
//                    val category = snapshot.child("Category").getValue(String::class.java)
//                    val price = snapshot.child("Price").getValue(String::class.java)
//                    val imageUrl = snapshot.child("ImageURL").getValue(String::class.java)
//
//                    if (productName != null && category != null && price != null && imageUrl != null) {
//                        val product = Product(productName, category, price, imageUrl)
//                        productArrayList.add(product)
//                    }
//                }
//
//                // Set the adapter for the RecyclerView
//                //productRecyclerview.adapter = ProductAdapter(productArrayList)
//                val productAdapter = ProductAdapter(productArrayList, object : ProductAdapter.OnItemClickListener {
//                    override fun onItemClick(position: Int) {
//                        // Handle item click event
//                        // You can redirect to a new page with the selected product
//                        val product = productArrayList[position]
//                        val intent = Intent(this@ProductsDisplayActivity, ProductDetailActivity::class.java)
//                        intent.putExtra("product", product)
//                        startActivity(intent)
//                    }
//                })
//                productRecyclerview.adapter = productAdapter
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle database error
//            }
//        })
//    }

//private fun getProductData() {
//    database = FirebaseDatabase.getInstance().getReference("Products")
//
//    database.addValueEventListener(object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            val productArrayList = ArrayList<Product>()
//
//            for (snapshot in dataSnapshot.children) {
//                val productName = snapshot.child("Product_Name").getValue(String::class.java)
//                val category = snapshot.child("Category").getValue(String::class.java)
//                val price = snapshot.child("Price").getValue(String::class.java)
//                val imageUrl = snapshot.child("ImageURL").getValue(String::class.java)
//
//                if (productName != null && category != null && price != null && imageUrl != null) {
//                    val product = Product(productName, category, price, imageUrl)
//                    productArrayList.add(product)
//                }
//            }
//
//            // Filter the products based on the recommended products list
//            val filteredProducts = ArrayList<Product>()
//            for (product in productArrayList) {
//                if (recommendedProducts.contains(product.Product_Name)) {
//                    filteredProducts.add(product)
//                }
//            }
//
//            // Set the adapter for the RecyclerView
//            val productAdapter = ProductAdapter(filteredProducts, object : ProductAdapter.OnItemClickListener {
//                override fun onItemClick(position: Int) {
//                    // Handle item click event
//                    // You can redirect to a new page with the selected product
//                    val product = filteredProducts[position]
//                    val intent = Intent(this@ProductsDisplayActivity, ProductDetailActivity::class.java)
//                    intent.putExtra("product", product)
//                    startActivity(intent)
//                }
//            })
//            productRecyclerview.adapter = productAdapter
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            // Handle database error
//        }
//    })
//}

