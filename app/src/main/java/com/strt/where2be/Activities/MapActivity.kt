package com.strt.where2be.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.strt.where2be.R
import com.strt.where2be.Utils.Constants

class MapActivity : AppCompatActivity(),OnMapReadyCallback {

    private var locationLat:String=""
    private var locationLon:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if (intent.hasExtra(Constants.LOCATION_LON)&& intent.hasExtra(Constants.LOCATION_LAT)){
            locationLat= intent.getStringExtra(Constants.LOCATION_LAT).toString()
            locationLon= intent.getStringExtra(Constants.LOCATION_LON).toString()

        }
        val supportMapFragment:SupportMapFragment=supportFragmentManager.findFragmentById(R.id.location_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {
        if (locationLat.isNotEmpty() && locationLon.isNotEmpty()) {
            val position = LatLng(locationLat.toDouble(), locationLon.toDouble())
            map.addMarker(MarkerOptions().position(position))
            val zoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
            map.animateCamera(zoom)
        }else{
            Toast.makeText(this, "error getting position", Toast.LENGTH_SHORT).show()
        }
    }
}