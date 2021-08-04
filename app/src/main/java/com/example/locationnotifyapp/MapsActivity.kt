package com.example.locationnotifyapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Network
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationnotifyapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions

class MapsActivity : AppCompatActivity(),TextToSpeech.OnInitListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private lateinit var locationRequest: LocationRequest
private lateinit var locationCallback: LocationCallback
private lateinit var textToSpeech: TextToSpeech
private  var radius : Double = 0.0
    private lateinit var latLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val distance= floatArrayOf(2.0F)

        radius = 1000.0
        latLng = LatLng(10.0199410,77.025625)


textToSpeech=TextToSpeech(this,this)
        locationRequest=LocationRequest()
        locationCallback=object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
               for(location:Location in locationResult.locations){
                   Location.distanceBetween( location.latitude, location.longitude,
                       latLng.latitude,latLng.longitude,distance)
                   if( distance[0] > radius ){
                       val text="You are outside the radius"
                       speakOut(text)
                       Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show();
                   } else {
                       val textIn= "You are inside the radius"
                       speakOut(textIn)
                       Toast.makeText(baseContext, textIn , Toast.LENGTH_LONG).show();
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
                val latlong = LatLng(latLng.latitude,latLng.longitude)
                staticMarkerOnMap(latlong)

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,12F))

                checkIsInOut()
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    private fun staticMarkerOnMap(latlong: LatLng) {
        val markerOptions = MarkerOptions().position(latlong)
        markerOptions.title("Destination")
        mMap.addCircle(CircleOptions().center(latlong).radius(radius).strokeColor(Color.GREEN).fillColor(Color.CYAN))
        mMap.addMarker(markerOptions)
    }

    override fun onInit(status: Int) {

    }


}


