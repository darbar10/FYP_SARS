@file:Suppress("DEPRECATION")

package com.example.sars_segmentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashBoardActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        btnSignUp.setOnClickListener {
            Intent(this, Sign_UpActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }

        btnUser.setOnClickListener {
            Intent(this, SignInActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }

//        onBackPressed()
    }

//    override fun onBackPressed() {
//
//        // Add your logic here, for example, to go back to the previous activity
//        super.onBackPressed()
//    }
}