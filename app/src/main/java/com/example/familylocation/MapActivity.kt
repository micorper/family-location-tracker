package com.example.familylocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.familylocation.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var binding: ActivityMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: FirebaseDatabase
    private lateinit var locationReference: DatabaseReference
    
    private val locationPermissionCode = 1001
    private val familyLocationMarkers = mutableMapOf<String, Marker>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mapa Familiar"
        
        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()
        locationReference = database.getReference("locations")
        
        // Inicializar Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // Configurar mapa
        setupMap()
        
        // Configurar ubicación en tiempo real
        setupRealTimeLocation()
    }
    
    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Configurar tipos de mapa
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        
        // Verificar permisos de ubicación
        checkLocationPermission()
        
        // Configurar listeners del mapa
        setupMapListeners()
    }
    
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permiso concedido - habilitar ubicación
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            getCurrentLocation()
        } else {
            // Solicitar permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
    }
    
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
    }
    
    private fun setupMapListeners() {
        googleMap.setOnMyLocationButtonClickListener {
            getCurrentLocation()
            true
        }
        
        googleMap.setOnMarkerClickListener { marker ->
            val familyMember = marker.tag as? String
            familyMember?.let {
                Toast.makeText(this, "Ubicación de: $it", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }
    
    private fun setupRealTimeLocation() {
        // Escuchar cambios en tiempo real de ubicaciones familiares
        locationReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clearFamilyMarkers()
                
                for (child in snapshot.children) {
                    val userId = child.key
                    val locationData = child.getValue(LocationData::class.java)
                    
                    userId?.let { user ->
                        locationData?.let { location ->
                            addFamilyMemberMarker(user, location)
                        }
                    }
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapActivity, "Error al cargar ubicaciones", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun clearFamilyMarkers() {
        familyLocationMarkers.values.forEach { marker ->
            marker.remove()
        }
        familyLocationMarkers.clear()
    }
    
    private fun addFamilyMemberMarker(userId: String, location: LocationData) {
        val position = LatLng(location.latitude, location.longitude)
        
        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(location.name)
                .snippet("Última actualización: ${location.timestamp}")
        )
        
        marker?.tag = userId
        familyLocationMarkers[userId] = marker
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            locationPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission()
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_refresh -> {
                getCurrentLocation()
                Toast.makeText(this, "Ubicación actualizada", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> {
                Toast.makeText(this, "Configuración del mapa", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    // Clase de datos para ubicación
    data class LocationData(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val name: String = "",
        val timestamp: String = ""
    )
}
