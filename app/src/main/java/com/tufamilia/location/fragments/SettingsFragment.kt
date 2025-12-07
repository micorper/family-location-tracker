package com.tufamilia.location.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tufamilia.location.R

class SettingsFragment : Fragment() {

    private lateinit var tvAppInfo: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        
        initializeViews(view)
        displayAppInfo()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        tvAppInfo = view.findViewById(R.id.tvAppInfo)
    }
    
    private fun displayAppInfo() {
        tvAppInfo.text = """
            Family Location Tracker v1.0
            
            Una aplicación para compartir ubicaciones en tiempo real con tu familia.
            
            Desarrollado con:
            - Android SDK 34
            - Kotlin
            - Firebase
            - Google Play Services
        """.trimIndent()
    }
}