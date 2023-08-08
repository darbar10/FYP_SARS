package com.example.sars_segmentation

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize Firebase Auth
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference


        btnlogin.setOnClickListener {
            val emailRegex =
                Regex("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$")

            val email = user_email.text.toString()
            val password = user_password.text.toString()

            if (email.isNullOrEmpty()) {
                showToast("Please enter your email")
                return@setOnClickListener
            }

            if (!emailRegex.matches(email)) {
                showToast("Please enter valid email address")
                return@setOnClickListener
            }

            if (password.isNullOrEmpty()) {
                showToast("Please enter your password")
                return@setOnClickListener
            }

            signIn(email, password)
        }

        btnBacksi.setOnClickListener{
            onBackClicked()
        }

    }

    private fun signIn(email: String, password: String) {
//         [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI(user)
                    } else {
                        Log.d(TAG, "signInWithEmail:success, but user is null")
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val userName = user.displayName
            if (userName != null) {
                fetchUserData(userName)
            } else {
                val userId = user.uid
                Log.d(TAG, "User ID: $userId")
                showToast("Display Name not found. Unable to fetch user data.")
            }
        }
    }


    private fun fetchUserData(userName: String) {
        val userDataRef = database.child("Users").child(userName)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Fetch the user data
                val education = dataSnapshot.child("education").getValue(Int::class.java)
                val maritalStatus = dataSnapshot.child("marital_status").getValue(Int::class.java)
                val income = dataSnapshot.child("income").getValue(Int::class.java)
                val kidHome = dataSnapshot.child("kidHome").getValue(Int::class.java)
                val teenHome = dataSnapshot.child("teenHome").getValue(Int::class.java)
                val recent = dataSnapshot.child("recency").getValue(Int::class.java)
                val totalSpent = dataSnapshot.child("total_spent").getValue(Int::class.java)

                Log.d(TAG, "Education: $education")
                Log.d(TAG, "Marital Status: $maritalStatus")
                Log.d(TAG, "Income: $income")
                Log.d(TAG, "Kid Home: $kidHome")
                Log.d(TAG, "Teen Home: $teenHome")
                Log.d(TAG, "Recent: $recent")
                Log.d(TAG, "Total Spent: $totalSpent")

                // Process the fetched data as desired
                val signupData = JSONObject()
                signupData.put("username", userName)
                signupData.put("education", education)
                signupData.put("marital_status", maritalStatus)
                signupData.put("income", income)
                signupData.put("kidHome", kidHome)
                signupData.put("teenHome", teenHome)
                signupData.put("recent", recent)
                signupData.put("totalSpent", totalSpent)

                val requestBody = signupData.toString().toRequestBody("application/json".toMediaType())

                // Create an OkHttp client
                val client = OkHttpClient()

                // Create a POST request to the Flask API endpoint
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/signin") // Replace with your API URL
                    .post(requestBody)
                    .build()

                // Execute the request asynchronously using coroutines
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val response = client.newCall(request).execute()
                        val responseData = response.body?.string()
                        if (response.isSuccessful) {
                            // Handle successful response
                            runOnUiThread {
                                Log.d(TAG, "User Data: $responseData")
                                showToast("Sign-in successful")
                                onLoginClicked(userName, responseData)
                            }
                        } else {
                            // Handle error response
                            runOnUiThread {
                                showToast("Sign-in failed. Please try again.")
                            }
                        }
                    } catch (e: IOException) {
                        // Handle network error
                        runOnUiThread {
                            showToast("Sign-in failed. Please try again.")
                        }
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        // Handle JSON parsing error
                        runOnUiThread {
                            showToast("Sign-in failed. Please try again.")
                        }
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error case where the data retrieval is unsuccessful
                showToast("Failed to fetch user data. Please try again.")
            }
        })
    }

    private fun onLoginClicked(userName: String, responseData: String?) {
        Log.d(TAG, "onLoginClicked: $userName")
        Toast.makeText(this, "Logged in as $userName", Toast.LENGTH_SHORT).show()

        if (!responseData.isNullOrEmpty()) {
            // Create an intent to start the ProductsDisplayActivity
            val intent = Intent(this, ProductsDisplayActivity::class.java)

            // Retrieve the recommended products from the API response
            val recommendedProducts = if (responseData != null) {
                parseRecommendedProducts(responseData)
            } else {
                ArrayList()
            }

            // Pass the username and recommended products as extras in the intent
            intent.putExtra("userName", userName)
            intent.putStringArrayListExtra("recommendedProducts", recommendedProducts)

            // Start the ProductsDisplayActivity
            startActivity(intent)
            finish()
        } else {
            showToast("Failed to retrieve recommended products. Please try again.")
        }

    }

    private fun parseRecommendedProducts(responseData: String): ArrayList<String> {
        val recommendedProducts = ArrayList<String>()

        try {
            val jsonArray = JSONArray(responseData)

            for (i in 0 until jsonArray.length()) {
                val product = jsonArray.getString(i)
                recommendedProducts.add(product)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return recommendedProducts
    }

    private fun onBackClicked() {
        Intent(this, DashBoardActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}
//    private fun onLoginClicked(userName: String) {
//        Log.d(TAG, "onLoginClicked: $userName")
//        Toast.makeText(this, "Logged in as $userName", Toast.LENGTH_SHORT).show()
//        Intent(this, ProductsDisplayActivity::class.java).apply {
//            putExtra("userName", userName)
//            startActivity(this)
//            finish()
//        }
//    }

//    private fun parseRecommendedProducts(responseData: String?): ArrayList<String> {
//        val recommendedProducts = ArrayList<String>()
//
//        if (responseData != null) {
//            try {
//                val jsonObject = JSONObject(responseData)
//                val productsArray = jsonObject.getJSONArray("recommendedProducts")
//
//                for (i in 0 until productsArray.length()) {
//                    val product = productsArray.getString(i)
//                    recommendedProducts.add(product)
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        }
//
//        return recommendedProducts
//    }


//        // Create an intent to start the ProductsDisplayActivity
//        val intent = Intent(this, ProductsDisplayActivity::class.java)
//
//        // Retrieve the recommended products from the API response
//        val recommendedProducts = parseRecommendedProducts(responseData)
//
//        // Pass the username and recommended products as extras in the intent
//        intent.putExtra("userName", userName)
//        intent.putStringArrayListExtra("recommendedProducts", recommendedProducts)
//
//        // Start the ProductsDisplayActivity
//        startActivity(intent)
//        finish()