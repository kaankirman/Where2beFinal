package com.strt.where2be.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.Models.Locations
import com.strt.where2be.R
import com.strt.where2be.Utils.Constants
import com.strt.where2be.databinding.ActivityAddLocationBinding
import java.lang.Exception

class AddLocationActivity : BaseActivity() {
    private lateinit var binding: ActivityAddLocationBinding
    private var mLocationImageURL:String=""
    private var locationImageString:String=""

    private var mLatitude:Double=0.0
    private var mLongitude:Double=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.tbAddLocation)


        if (!Places.isInitialized()){
            Places.initialize(this@AddLocationActivity,resources.getString(R.string.google_places_api_key))
        }
        binding.ivLocationImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES)== PackageManager.PERMISSION_GRANTED){
                showImageChooser()
                glideImage(this@AddLocationActivity,mSelectedImageFileUri.toString(),R.drawable.image_plaseholder_dark,binding.ivLocationImage)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),READ_MEDIA_IMAGES_PERMISSION_CODE
                )
            }
        }
        binding.etSelectLocation.setOnClickListener{
            try {
                val fields= listOf(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS)
                val intent=Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                    .build(this@AddLocationActivity)
                startActivityForResult(intent, PLACE_REQUEST_CODE)

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        binding.btAddLocation.setOnClickListener {
            if (mSelectedImageFileUri!=null){
                uploadLocationImage()
            }else{
                createLocation()
            }
        }
    }

    fun uploadLocationImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri!=null){
            locationImageString="LOCATION_IMAGE"+System.currentTimeMillis()+"."+getFileExtension(mSelectedImageFileUri!!)
            val sRef: StorageReference = FirebaseStorage.getInstance().reference
                .child(locationImageString)
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot->
                Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mLocationImageURL=uri.toString()
                    createLocation()
                    hideProgressDialog()
                }
            }.addOnFailureListener{
                    exception->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }
    fun createLocation(){
        var location= Locations(location_id="0",
            binding.etLocationName.text.toString(),
            mLatitude.toString(),
            mLongitude.toString(),
            mLocationImageURL,
            binding.etLocationDescription.text.toString()
        )
        val currentFolderId=intent.getStringExtra(Constants.FOLDER_ID)
        if (binding.etLocationName.text.toString().isNotEmpty()&&binding.etLocationDescription.text.toString().isNotEmpty()&&binding.etSelectLocation.text.toString().isNotEmpty()){
            FirestoreClass().createLocation(this,location,currentFolderId!!)
        }else{
            hideProgressDialog()
            Toast.makeText(this, "Please Fill all information", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK && requestCode== PICK_IMAGE_REQUEST_CODE && data!!.data!=null) {
            mSelectedImageFileUri = data.data
            glideImage(this,mSelectedImageFileUri.toString(),
                R.drawable.person_dark,binding.ivLocationImage)
        }
        if (resultCode== RESULT_OK && requestCode== PLACE_REQUEST_CODE ){
            val place:Place=Autocomplete.getPlaceFromIntent(data!!)
            binding.etSelectLocation.setText(place.address)
            mLatitude=place.latLng!!.latitude
            mLongitude=place.latLng!!.longitude
        }
    }
    fun locationCreatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this, "Location successfully created!", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
}