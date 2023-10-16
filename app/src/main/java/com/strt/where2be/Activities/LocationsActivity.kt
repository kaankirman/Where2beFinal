package com.strt.where2be.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.strt.where2be.Adapters.LocationItemAdapter
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.Models.Folders
import com.strt.where2be.Models.Locations
import com.strt.where2be.R
import com.strt.where2be.Utils.Constants
import com.strt.where2be.databinding.ActivityLocationsBinding

class LocationsActivity : BaseActivity() {
    private lateinit var currentFolder:Folders
    private lateinit var binding:ActivityLocationsBinding
    private var folderDocumentId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityLocationsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (intent.hasExtra(Constants.FOLDER_ID)){
            folderDocumentId= intent.getStringExtra(Constants.FOLDER_ID).toString()
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getFolderDetails(this,folderDocumentId)

        binding.addLocationButton.setOnClickListener{
            val intent=Intent(this@LocationsActivity,AddLocationActivity::class.java)
            intent.putExtra(Constants.FOLDER_ID,currentFolder.folder_id)
            startActivityForResult(intent,CREATE_LOCATION_REQUEST_CODE)
        }
    }

    fun folderDetails(folder:Folders){
        currentFolder=folder
        hideProgressDialog()
        setupActionBar(binding.tbFolderName,folder.name)
        FirestoreClass().getLocationList(this,currentFolder.folder_id)
    }

    fun prepareLocationsList(locationList:ArrayList<Locations>){
        hideProgressDialog()
        val rvLocationList=findViewById<RecyclerView>(R.id.rv_locations_list)
        if (locationList.size>0){
            rvLocationList.visibility= View.VISIBLE
            findViewById<TextView>(R.id.tv_no_location).visibility= View.GONE

            rvLocationList.layoutManager= LinearLayoutManager(this)
            rvLocationList.setHasFixedSize(true)

            val adapter= LocationItemAdapter(this,locationList)
            rvLocationList.adapter=adapter

            adapter.setOnClickListener(object: LocationItemAdapter.OnClickListener{
                override fun onClick(position: Int, model: Locations) {
                    val intent=Intent(this@LocationsActivity,SelectedLocationActivity::class.java)
                    intent.putExtra(Constants.LOCATION_ID,model.location_id)
                    intent.putExtra(Constants.FOLDER_ID,folderDocumentId)
                    startActivity(intent)
                }
            })

        }else{
            rvLocationList.visibility= View.GONE
            findViewById<TextView>(R.id.tv_no_location).visibility= View.VISIBLE

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK && requestCode== CREATE_LOCATION_REQUEST_CODE ){
            FirestoreClass().getLocationList(this,currentFolder.folder_id)
        }
    }
}