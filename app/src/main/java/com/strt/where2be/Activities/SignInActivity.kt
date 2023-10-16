package com.strt.where2be.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.Models.User
import com.strt.where2be.R
import com.strt.where2be.databinding.ActivityIntroActivtyBinding
import com.strt.where2be.databinding.ActivitySignInBinding

class SignInActivity : BaseActivity() {
    private lateinit var binding:ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.tbSignIn)

        binding.btSignInActSignIn.setOnClickListener {
            signInRegisteredUser()
        }
        moveFocus(binding.etSignInEmail,binding.etSignInPassword)
    }

    private fun signInRegisteredUser(){
        val email:String=binding.etSignInEmail.text.toString().trim{it<=' '}
        val password:String=binding.etSignInPassword.text.toString().trim{it<=' '}


        if (validateForm(name="name",email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){task->
                hideProgressDialog()
                if (task.isSuccessful){
                    Log.d("Sign in","signInWithEmail:success")
                    val user=auth.currentUser
                    FirestoreClass().loadUserData(this)
                }else{
                    Log.w("Sign in","signInWithEmail:failure",task.exception)
                    Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun signInSuccess(user:User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}