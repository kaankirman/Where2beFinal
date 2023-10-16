package com.strt.where2be.Activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.strt.where2be.R
import com.strt.where2be.databinding.ActivityIntroActivtyBinding

class IntroActivty : BaseActivity() {
    private lateinit var binding:ActivityIntroActivtyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIntroActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTypeFace(binding.tvIntroAppName)

        binding.btSignIn.setOnClickListener{
            startActivity(Intent(this,SignInActivity::class.java))
        }
        binding.btSignUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }
}