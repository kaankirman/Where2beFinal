package com.strt.where2be.Activities

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.Models.User
import com.strt.where2be.R
import com.strt.where2be.databinding.ActivitySignUpBinding

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.tbSignUp)

        binding.btSignUpActSignUp.setOnClickListener { registerUser() }
        moveFocus(binding.etSignUpName,binding.etSignUpEmail)
        moveFocus(binding.etSignUpEmail,binding.etSignUpPassword)
    }

    //signup with given name,email,password save to firebase
    private fun registerUser(){
        val name:String=binding.etSignUpName.text.toString().trim{it<=' '}
        val email:String=binding.etSignUpEmail.text.toString().trim{it<=' '}
        val password:String=binding.etSignUpPassword.text.toString().trim{it<=' '}

        if (validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                task->
                if (task.isSuccessful){
                    val firebaseUser:FirebaseUser=task.result!!.user!!
                    val registeredEmail=firebaseUser.email!!
                    val user= User(firebaseUser.uid,name,registeredEmail)
                    FirestoreClass().registerUser(this,user)
                }else{
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this, "you have succesfully registered", Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        auth.signOut()
        finish()
    }


}