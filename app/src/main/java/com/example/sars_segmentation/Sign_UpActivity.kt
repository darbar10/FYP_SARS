package com.example.sars_segmentation

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up2.*

class Sign_UpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var rgEducation: RadioGroup
    private lateinit var rgMaritalStatus: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        bindEducationRadioGroup()
        bindMaritalStatusRadioGroup()

        var edu1: Int
        var mar_stat1: Int

        // Initialize Firebase Auth
        auth = Firebase.auth

        btnSignup.setOnClickListener {

            val edu =  getSelectedEducation()
            val mar_stat = getSelectedMaritalStatus()

            edu1 = when (edu) {
                "Postgraduate" -> 1
                "Undergraduate" -> 0
                else -> -1 // Handle the default or "Please Select" case accordingly
            }
            mar_stat1 = when (mar_stat) {
                "Married" -> 1
                "Single" -> 0
                else -> -1 // Handle the default or "Please Select" case accordingly
            }

            if (edu1 == -1) {
                showToast("Please select education")
                return@setOnClickListener
            }

            if (mar_stat1 == -1) {
                showToast("Please select marital status")
                return@setOnClickListener
            }

            val emailRegex =
                Regex("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$")


            val fullName = name.text.toString()
            val email = txemail.text.toString()
            val education = edu1
            val marital_status = mar_stat1
            val income = Income.text.toString().toIntOrNull()
            val kidHome = txkidHome.text.toString().toIntOrNull()
            val teenHome = txteenHome.text.toString().toIntOrNull()
            val recent = recency.text.toString().toIntOrNull()
            val totalSpent = total_spent.text.toString().toIntOrNull()
            val password = txpassword.text.toString()

            if (fullName.isNullOrEmpty() &&
                email.isNullOrEmpty() &&
                education == null &&
                marital_status == null &&
                income == null &&
                kidHome == null &&
                teenHome == null &&
                recent == null &&
                totalSpent == null &&
                password.isNullOrEmpty()) {
                showToast("Please Fill All The Details Above.")
                return@setOnClickListener
            }


            if (fullName.isNullOrEmpty()) {
                showToast("Please enter your Full Name.")
                return@setOnClickListener
            }


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

            if (!chTermsConditions.isChecked) {
                showToast("Please select the Terms & Conditions")
                return@setOnClickListener
            }

            database = FirebaseDatabase.getInstance().reference
            val sanitizedFullName = fullName.replace("\\s+".toRegex(), "").replace("[^a-zA-Z0-9]".toRegex(), "_")
            val user = User(fullName, email, education, marital_status, income, kidHome, teenHome, recent, totalSpent, password)
            database.child("Users").child(sanitizedFullName).setValue(user).addOnSuccessListener {
                clearInputFields()
                createAccount(email,password,sanitizedFullName)
                showToast("Successfully Saved")

                signUp()
            }.addOnFailureListener{
                showToast("SignUp Failed. Please try again.")
            }
        }

        btnBack.setOnClickListener{
            onBackClicked()
        }


    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun createAccount(email: String, password: String, displayName: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    Log.d(TAG, "display name set:success")
                                } else {
                                    // Failed to set display name
                                    Log.d(TAG, "Display Name Set:unsuccessful")
                                }
                            }
                    }
                } else {
                    // Account creation failed
                    Log.d(TAG, "Account Creation:unsuccessful")
                    showToast("Account Creation Failed. Please Try Again")
                    // Handle the error appropriately
                }
            }
        // [END create_user_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {}

    private fun reload() {}


    private fun bindEducationRadioGroup() {
        rgEducation = findViewById(R.id.rgEducation)

        val radioPostgraduate = findViewById<RadioButton>(R.id.rbPostgraduate)
        val radioUndergraduate = findViewById<RadioButton>(R.id.rbUndergraduate)

        rgEducation.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val selectedEducation = radioButton.text.toString()
            Toast.makeText(this, "Selected Education: $selectedEducation", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindMaritalStatusRadioGroup() {
        rgMaritalStatus = findViewById(R.id.rgMaritalStatus)

        val radioMarried = findViewById<RadioButton>(R.id.rbMarried)
        val radioSingle = findViewById<RadioButton>(R.id.rbSingle)

        rgMaritalStatus.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val selectedMaritalStatus = radioButton.text.toString()
            Toast.makeText(this, "Selected Marital Status: $selectedMaritalStatus", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedEducation(): String {
        val checkedRadioButtonId = rgEducation.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(checkedRadioButtonId)
        return radioButton.text.toString()
    }

    private fun getSelectedMaritalStatus(): String {
        val checkedRadioButtonId = rgMaritalStatus.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(checkedRadioButtonId)
        return radioButton.text.toString()
    }

    private fun clearInputFields() {
        name.text.clear()
        txemail.text.clear()
        txkidHome.text.clear()
        txteenHome.text.clear()
        recency.text.clear()
        total_spent.text.clear()
        txpassword.text.clear()
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun onBackClicked() {
        Intent(this, DashBoardActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun signUp() {
        Intent(this, SignInActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}

private fun Any.clear() {
}