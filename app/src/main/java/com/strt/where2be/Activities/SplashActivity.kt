package com.strt.where2be.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        setTypeFace(binding.tvAppName)

        Handler().postDelayed({
            val currentUserID = FirestoreClass().getCurrentUserId()
            Log.e("current user ID","is $currentUserID")
            if (currentUserID.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                startActivity(Intent(this,IntroActivty::class.java))
            }
            finish()
        }, 2000)
    }
}