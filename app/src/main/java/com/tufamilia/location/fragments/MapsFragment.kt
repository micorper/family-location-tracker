package com.tufamilia.location.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tufamilia.location.R

class MapsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        
        // Aquí iría la integración con Google Maps
        Toast.makeText(context, "Mapa en desarrollo", Toast.LENGTH_SHORT).show()
        
        return view
    }
}