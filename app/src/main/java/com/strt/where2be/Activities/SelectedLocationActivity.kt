package com.strt.where2be.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.Models.Locations
import com.strt.where2be.R
import com.strt.where2be.Utils.Constants
import com.strt.where2be.databinding.ActivitySelectedLocationBinding

class SelectedLocationActivity : BaseActivity() {
    private lateinit var currentLocation:Locations
    private lateinit var binding:ActivitySelectedLocationBinding
    private var locationDocumentId=""
    private var folderDocumentID=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySelectedLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra(Constants.LOCATION_ID)){
            locationDocumentId= intent.getStringExtra(Constants.LOCATION_ID).toString()
            folderDocumentID=intent.getStringExtra(Constants.FOLDER_ID).toString()
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getLocationDetails(this,folderDocumentID,locationDocumentId)

        binding.btShowLocation.setOnClickListener {
            intent=Intent(this,MapActivity::class.java)
            intent.putExtra(Constants.LOCATION_LAT,currentLocation.lat)
            intent.putExtra(Constants.LOCATION_LON,currentLocation.lon)
            Log.i("currentLocation.lat",currentLocation.lat)
            Log.i("currentLocation.lon",currentLocation.lon)
            startActivity(intent)
        }
    }

    fun locationDetails(location: Locations){
        currentLocation=location
        hideProgressDialog()
        setupActionBar(binding.tbSelectedLocation,location.name)
        setupLocationDetails(currentLocation)
    }
    fun setupLocationDetails(location: Locations){
        glideImage(this,location.image,R.drawable.image_plaseholder_dark,binding.ivSelectedLocationImage)
        binding.tvSelectedLocationName.text=location.name
        binding.tvSelectedLocationDescription.text=location.descriptionText
        hideProgressDialog()
    }
}