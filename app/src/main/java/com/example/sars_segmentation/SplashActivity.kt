package com.example.sars_segmentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private val handler = Handler()
    private lateinit var runnable:Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        runnable = Runnable {
            val intent = Intent(this, DashBoardActivity::class.java)
            startActivity(intent)
            finish()
        }

        handler.postDelayed(runnable,10000)

        btnBack.setOnClickListener{
            onSkipClicked()
        }
    }
    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()

        //showToast("onDestroy()")
    }

    private fun onSkipClicked() {
        Intent(this, DashBoardActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}