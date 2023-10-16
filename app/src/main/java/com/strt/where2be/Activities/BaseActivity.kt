package com.strt.where2be.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.CaseMap.Title
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.strt.where2be.R
import java.io.IOException
import com.strt.where2be.Utils.Constants


open class BaseActivity : AppCompatActivity() {
    companion object{
        const val READ_MEDIA_IMAGES_PERMISSION_CODE=1
        const val PICK_IMAGE_REQUEST_CODE=2
        const val PROFILE_DATA_STATE_REQUEST_CODE=3
        const val CREATE_FOLDER_REQUEST_CODE=4
        const val PLACE_REQUEST_CODE=5
        const val CREATE_LOCATION_REQUEST_CODE=6
        const val PROFILE_DATA_CHANGED_RESULT_CODE=11

    }

    val auth=FirebaseAuth.getInstance()
    var savedValue:String=""
    var isUserDataChanged:Boolean=false
    var doubleBackPressToExit = false
    var mSelectedImageFileUri:Uri?=null
    private lateinit var mProgressDialog: Dialog
    private lateinit var dataStore:DataStore<Preferences>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
    fun showProgressDialog(text:String){
        mProgressDialog=Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_dialog).text=text
        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun setTypeFace(actTextView: TextView):TextView {
        val typeface: Typeface = Typeface.createFromAsset(assets,"SunnySpells.otf")
        actTextView.typeface=typeface
        return actTextView
    }

    fun doubleBacktoExit(){
        if (doubleBackPressToExit){
            super.onBackPressed()
            return
        }
        this.doubleBackPressToExit=true
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({doubleBackPressToExit=false},2000)
    }

    fun showErrorSnackBar(message: String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    fun setupActionBar(actToolbar: androidx.appcompat.widget.Toolbar,title: String=""): androidx.appcompat.widget.Toolbar {
        setSupportActionBar(actToolbar)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.title=title
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        actToolbar.setNavigationOnClickListener { onBackPressed() }
        return actToolbar
    }

    fun validateForm(name:String,email:String,password:String):Boolean{
        return when {
            TextUtils.isEmpty(name)->{showErrorSnackBar("Please enter a name")
                false}
            TextUtils.isEmpty(email)->{showErrorSnackBar("Please enter an email")
                false}
            TextUtils.isEmpty(password)->{showErrorSnackBar("Please enter a password")
                false}
            else ->true
        }
    }


    fun glideImage(activity: Activity, uri:String?, placeHolder: Int, imageView: ImageView){
        try {
            Glide.with(activity)
                .load(uri)
                .centerCrop()
                .placeholder(placeHolder)
                .into(imageView)

        }catch (e:IOException){
            e.printStackTrace()
        }

    }


    fun moveFocus(currentET:EditText,nextET:EditText){
        val focusedET=findViewById<EditText>(currentET.id)
        val requestFocusET=findViewById<EditText>(nextET.id)
        focusedET.setOnKeyListener { _, keyCode, event ->
            if (keyCode== KeyEvent.KEYCODE_ENTER && event.action== KeyEvent.ACTION_DOWN){
                if (focusedET.isFocused)
                    requestFocusET.requestFocus()
                return@setOnKeyListener true
            }
            false
        }
    }
    fun getFileExtension(uri:Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun showImageChooser(){
        var galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST_CODE)
    }


}