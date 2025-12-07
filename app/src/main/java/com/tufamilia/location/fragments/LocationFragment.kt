package com.tufamilia.location.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tufamilia.location.R

class LocationFragment : Fragment() {
    
    private lateinit var tvLocation: TextView
    private lateinit var btnStartTracking: Button
    private lateinit var btnStopTracking: Button
    private lateinit var tvStatus: TextView
    
    private var isTracking = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)
        
        initializeViews(view)
        setupClickListeners()
        updateUI()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        tvLocation = view.findViewById(R.id.tvLocation)
        btnStartTracking = view.findViewById(R.id.btnStartTracking)
        btnStopTracking = view.findViewById(R.id.btnStopTracking)
        tvStatus = view.findViewById(R.id.tvStatus)
    }
    
    private fun setupClickListeners() {
        btnStartTracking.setOnClickListener {
            startTracking()
        }
        
        btnStopTracking.setOnClickListener {
            stopTracking()
        }
    }
    
    private fun startTracking() {
        isTracking = true
        updateUI()
        Toast.makeText(context, getString(R.string.msg_tracking_started), Toast.LENGTH_SHORT).show()
        
        // Aquí iría la lógica para iniciar el servicio de ubicación
        tvLocation.text = "Lat: -34.6037, Lng: -58.3816\nTimestamp: ${System.currentTimeMillis()}"
    }
    
    private fun stopTracking() {
        isTracking = false
        updateUI()
        Toast.makeText(context, getString(R.string.msg_tracking_stopped), Toast.LENGTH_SHORT).show()
    }
    
    private fun updateUI() {
        if (isTracking) {
            tvStatus.text = getString(R.string.status_tracking)
            tvStatus.setTextColor(requireContext().getColor(R.color.success))
            btnStartTracking.visibility = View.GONE
            btnStopTracking.visibility = View.VISIBLE
        } else {
            tvStatus.text = getString(R.string.status_idle)
            tvStatus.setTextColor(requireContext().getColor(R.color.text_secondary))
            btnStartTracking.visibility = View.VISIBLE
            btnStopTracking.visibility = View.GONE
        }
    }
}