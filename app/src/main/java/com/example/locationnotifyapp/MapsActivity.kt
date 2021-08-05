package com.example.locationnotifyapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.locationnotifyapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(),TextToSpeech.OnInitListener, OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private lateinit var locationRequest: LocationRequest
private lateinit var locationCallback: LocationCallback
private lateinit var textToSpeech: TextToSpeech
private  var radius : Double = 0.0
    private lateinit var latLng: LatLng
private lateinit var bottomSheetDialog: BottomSheetDialog
private lateinit var view:View
    private lateinit var info :Any
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    info =   Snackbar.make(findViewById(android.R.id.content), "Long press on the location to add marker", Snackbar.LENGTH_INDEFINITE).show()
bottomSheetDialog= BottomSheetDialog(this)

         view = layoutInflater.inflate(R.layout.bottom_sheet,null)
        bottomSheetDialog.setContentView(view)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val distance= floatArrayOf(2.0F)

        //radius = 1000.0
        //latLng = LatLng(10.0199410,77.025625)


textToSpeech=TextToSpeech(this,this)
        locationRequest=LocationRequest()
        locationCallback=object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
              for(location: Location in locationResult.locations){
                   Location.distanceBetween( location.latitude, location.longitude,
                       latLng.latitude,latLng.longitude,distance)
                   if( distance[0] > radius ){
                       val text="You are outside the radius"
                       speakOut(text)
                       Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show()

                   } else {
                       val textIn= "You are inside the radius"
                       speakOut(textIn)
                       Toast.makeText(baseContext, textIn , Toast.LENGTH_LONG).show()
                   }

               }
            }
        }
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun speakOut(text: String) {
        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
mMap.setOnMapLongClickListener(this)

        mMap.uiSettings.isZoomControlsEnabled=true

        setUpMap()
      // checkIsInOut()

    }




    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)

            return
        }
        mMap.isMyLocationEnabled=true

        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) {
            location ->
            if(location!=null){
                var lastlocation = location
                val currentLoc = LatLng(location.latitude,location.longitude)
              //  placeMarkerOnMap(currentLoc)
               // val latlong = LatLng(latLng.latitude,latLng.longitude)
               // staticMarkerOnMap(latlong)

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,12F))


            }
        }
    }

    private fun checkIsInOut() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())

    }

//    private fun staticMarkerOnMap(latlong: LatLng) {
//        val markerOptions = MarkerOptions().position(latlong)
//        markerOptions.title("Destination")
//        mMap.addCircle(CircleOptions().center(latlong).radius(radius).strokeColor(Color.GREEN).fillColor(Color.CYAN))
//        mMap.addMarker(markerOptions)
//    }

    override fun onInit(status: Int) {

    }

    override fun onMapLongClick(p0: LatLng) {

         mMap.addMarker(MarkerOptions().position(p0))

        showBottomSheet(p0)




    }

    private fun showBottomSheet(p0: LatLng) {



        val btnCancel = view.findViewById<Button>(R.id.cancel)
        val btnDone=view.findViewById<Button>(R.id.done)
        val bar = view.findViewById<SeekBar>(R.id.radBar)



        val radVal = view.findViewById<TextView>(R.id.rad)
        radVal.text = bar.progress.toString()
        bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radVal.text=bar.progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                radVal.text=bar.progress.toString()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                radVal.text=bar.progress.toString()
            }

        })

        btnDone.setOnClickListener {

            radius = bar.progress.toDouble()

            mMap.addCircle(CircleOptions().center(p0).radius(radius).strokeColor(Color.GREEN).fillColor(Color.CYAN))
            bottomSheetDialog.dismiss()
            latLng=p0
            checkIsInOut()

        }
        btnCancel.setOnClickListener {

            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }




}



