package com.strt.where2be.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.Models.User
import com.strt.where2be.R
import com.strt.where2be.Utils.Constants
import com.strt.where2be.databinding.ActivityProfileBinding
import java.util.HashMap

class ProfileActivity : BaseActivity() {


    private lateinit var mUserDetails:User
    private lateinit var userImageString:String
    private lateinit var binding:ActivityProfileBinding
    private  var mProfileImageURL:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.tbProfile)
        FirestoreClass().loadUserData(this)
        moveFocus(binding.etProfileName,binding.etProfileEmail)

        binding.ivUserProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES)==PackageManager.PERMISSION_GRANTED){
                showImageChooser()
                glideImage(this@ProfileActivity,mSelectedImageFileUri.toString(),R.drawable.person_dark,binding.ivUserProfile)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),READ_MEDIA_IMAGES_PERMISSION_CODE
                )
            }
        }
        binding.btProfileUpdate.setOnClickListener {
            if (mSelectedImageFileUri!=null){
                showProgressDialog(resources.getString(R.string.please_wait))
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserData()
            }
            hideProgressDialog()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==READ_MEDIA_IMAGES_PERMISSION_CODE){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showImageChooser()
                glideImage(this@ProfileActivity,mSelectedImageFileUri.toString(),R.drawable.person_dark,binding.ivUserProfile)
            }
        }else{
            Toast.makeText(this, "Storage Permission Denied, you can allow it from settings.", Toast.LENGTH_SHORT).show()
        }
    }
    fun setUpProfileDetails(user:User){
        showProgressDialog(resources.getString(R.string.please_wait))
        mUserDetails=user
        val name=user.name
        val email=user.email
        val profileImage =user.image
        binding.etProfileEmail.setText(email)
        binding.etProfileName.setText(name)
        glideImage(this@ProfileActivity,profileImage,R.drawable.person_dark,binding.ivUserProfile)
        hideProgressDialog()
    }

    fun updateUserData(){
        val userHashMap=HashMap<String,Any>()
        if (mProfileImageURL.isNotEmpty()&&mProfileImageURL!=mUserDetails.image){
            userHashMap[Constants.IMAGEURI]=userImageString
            userHashMap[Constants.IMAGE] = mProfileImageURL
            isUserDataChanged=true
            setResult(PROFILE_DATA_CHANGED_RESULT_CODE)
            if (mUserDetails.imageUri.isNotEmpty())
                FirebaseStorage.getInstance().reference.child(mUserDetails.imageUri).delete()
        }
        if (binding.etProfileName.text.toString()!=mUserDetails.name){
            userHashMap[Constants.NAME]=binding.etProfileName.text.toString()
            isUserDataChanged=true
            setResult(PROFILE_DATA_CHANGED_RESULT_CODE)
        }
        if (isUserDataChanged){
            FirestoreClass().updateUserData(this,userHashMap)
            isUserDataChanged=false
        }else{
            Toast.makeText(this, "No changes have been made", Toast.LENGTH_SHORT).show()
            hideProgressDialog()
        }
    }

    fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri!=null&&mUserDetails.image!=mSelectedImageFileUri.toString()){
            userImageString="USER_IMAGE"+System.currentTimeMillis()+"."+getFileExtension(mSelectedImageFileUri!!)
            val sRef:StorageReference=FirebaseStorage.getInstance().reference
                .child(userImageString)
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
                Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageURL=uri.toString()
                    updateUserData()
                    hideProgressDialog()
                }
            }.addOnFailureListener{
                exception->
                Toast.makeText(this@ProfileActivity, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK && requestCode== PICK_IMAGE_REQUEST_CODE && data!!.data!=null) {
            mSelectedImageFileUri = data.data
            glideImage(this,mSelectedImageFileUri.toString(),
                R.drawable.person_dark,binding.ivUserProfile)
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        finish()
    }

}