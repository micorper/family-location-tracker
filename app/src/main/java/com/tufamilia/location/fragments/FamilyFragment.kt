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

class FamilyFragment : Fragment() {
    
    private lateinit var tvFamilyInfo: TextView
    private lateinit var btnAddMember: Button
    private lateinit var btnScanQR: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_family, container, false)
        
        initializeViews(view)
        setupClickListeners()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        tvFamilyInfo = view.findViewById(R.id.tvFamilyInfo)
        btnAddMember = view.findViewById(R.id.btnAddMember)
        btnScanQR = view.findViewById(R.id.btnScanQR)
    }
    
    private fun setupClickListeners() {
        btnAddMember.setOnClickListener {
            addFamilyMember()
        }
        
        btnScanQR.setOnClickListener {
            scanQRCode()
        }
    }
    
    private fun addFamilyMember() {
        Toast.makeText(context, getString(R.string.msg_family_member_added), Toast.LENGTH_SHORT).show()
        tvFamilyInfo.text = "Miembro familiar agregado exitosamente"
    }
    
    private fun scanQRCode() {
        Toast.makeText(context, getString(R.string.msg_qr_code_scanned), Toast.LENGTH_SHORT).show()
        // Aquí iría la lógica para escanear códigos QR
    }
}