package com.strt.where2be.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.Models.Folders
import com.strt.where2be.R
import com.strt.where2be.databinding.ActivityCreateFolderBinding


class CreateFolderActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateFolderBinding
    private var mFolderImageURL:String=""
    private var folderImageString:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityCreateFolderBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar(binding.tbAddFolder)
        binding.btAddFolder.setOnClickListener {
            if (mSelectedImageFileUri!=null){
                uploadFolderImage()
            }else{
                createFolder()
            }
        }
        binding.ivFolderImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES)== PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),READ_MEDIA_IMAGES_PERMISSION_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK && requestCode== PICK_IMAGE_REQUEST_CODE && data!!.data!=null) {
            mSelectedImageFileUri = data.data
            mFolderImageURL= mSelectedImageFileUri.toString()
            glideImage(this,mSelectedImageFileUri.toString(),
                R.drawable.image_plaseholder_dark,binding.ivFolderImage)
        }
    }
    private fun createFolder(){
        var folder=Folders(
        binding.etFolderName.text.toString(),
        mFolderImageURL
        )
        if (binding.etFolderName.text.toString().isNotEmpty()){
            FirestoreClass().createFolder(this,folder)
        }else{
            hideProgressDialog()
            Toast.makeText(this, "Please give folder a name", Toast.LENGTH_SHORT).show()
        }
        
    }

    fun uploadFolderImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri!=null){
            folderImageString="FOLDER_IMAGE"+System.currentTimeMillis()+"."+getFileExtension(mSelectedImageFileUri!!)
            val sRef: StorageReference = FirebaseStorage.getInstance().reference
                .child(folderImageString)
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot->
                Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mFolderImageURL=uri.toString()
                    createFolder()
                    hideProgressDialog()
                }
            }.addOnFailureListener{
                    exception->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    fun folderCreatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this, "Folder successfully created!", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

}