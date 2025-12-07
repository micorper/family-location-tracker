package com.example.familylocation

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.familylocation.databinding.ActivityQrBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.HashMap

class QRActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityQrBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Códigos QR"
        
        // Inicializar Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        
        // Configurar interfaz
        setupUI()
        
        // Generar QR por defecto
        generateMyQRCode()
    }
    
    private fun setupUI() {
        // Botón para generar mi QR
        binding.generateMyQrButton.setOnClickListener {
            generateMyQRCode()
        }
        
        // Botón para escanear QR
        binding.scanQrButton.setOnClickListener {
            scanQRCode()
        }
        
        // Botón para compartir QR
        binding.shareQrButton.setOnClickListener {
            shareQRCode()
        }
        
        // Botón para copiar código
        binding.copyCodeButton.setOnClickListener {
            copyQRCode()
        }
        
        // Configurar tab layout
        setupTabLayout()
    }
    
    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : androidx.viewpager.widget.ViewPager.OnTabSelectedListener {
            override fun onTabSelected(tab: androidx.viewpager.widget.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> generateMyQRCode()
                    1 -> binding.qrImageView.setImageBitmap(null)
                }
            }
            
            override fun onTabUnselected(tab: androidx.viewpager.widget.TabLayout.Tab?) {}
            override fun onTabReselected(tab: androidx.viewpager.widget.TabLayout.Tab?) {}
        })
    }
    
    private fun generateMyQRCode() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userName = user.displayName ?: "Usuario"
            val familyId = "FAMILY_${userId.take(8)}" // ID de familia simplificado
            
            // Crear datos del QR
            val qrData = "FAMILY_TRACKER|$familyId|$userId|$userName"
            
            // Generar QR
            try {
                val hints = HashMap<EncodeHintType, Any>()
                hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
                hints[EncodeHintType.MARGIN] = 1
                
                val writer = QRCodeWriter()
                val bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 400, 400, hints)
                
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                
                binding.qrImageView.setImageBitmap(bitmap)
                binding.qrCodeText.text = "Código: $familyId"
                binding.userNameText.text = userName
                
                // Guardar en Firebase para que otros puedan unirse
                saveFamilyInfo(familyId, userName, userId)
                
            } catch (e: Exception) {
                Toast.makeText(this, "Error al generar QR: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveFamilyInfo(familyId: String, userName: String, userId: String) {
        val familyRef = database.getReference("families").child(familyId)
        val familyData = mapOf(
            "createdBy" to userId,
            "createdByName" to userName,
            "createdAt" to System.currentTimeMillis(),
            "members" to mapOf(userId to userName)
        )
        
        familyRef.setValue(familyData)
            .addOnSuccessListener {
                Toast.makeText(this, "Código QR generado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar información: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun scanQRCode() {
        try {
            // Usar ZXing Android Embedded para escanear
            val intentIntegrator = com.journeyapps.barcodescanner.IntentIntegrator(this)
            intentIntegrator.setDesiredBarcodeFormats(com.journeyapps.barcodescanner.BarcodeFormat.QR_CODE.name)
            intentIntegrator.setPrompt("Escanea un código QR para unirte a una familia")
            intentIntegrator.setBeepEnabled(true)
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.initiateScan()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al iniciar escáner: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun shareQRCode() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userName = user.displayName ?: "Usuario"
            val familyId = "FAMILY_${user.uid.take(8)}"
            val shareText = "¡Únete a mi familia en Family Location Tracker!\n\n" +
                    "Código: $familyId\n" +
                    "Usuario: $userName\n\n" +
                    "Abre la app y escanea este código para unirte."
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            
            startActivity(Intent.createChooser(shareIntent, "Compartir código QR"))
        }
    }
    
    private fun copyQRCode() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val familyId = "FAMILY_${user.uid.take(8)}"
            
            val clipboard = android.content.ClipboardManager::class.java
            val clip = android.content.ClipData.newPlainText("Código QR", familyId)
            clipboard.setPrimaryClip(clip)
            
            Toast.makeText(this, "Código copiado: $familyId", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        val intentResult = com.journeyapps.barcodescanner.IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        
        if (intentResult != null) {
            val contents = intentResult.contents
            if (contents != null) {
                processQRContent(contents)
            }
        }
    }
    
    private fun processQRContent(content: String) {
        try {
            // Parsear el contenido del QR
            val parts = content.split("|")
            if (parts.size >= 3 && parts[0] == "FAMILY_TRACKER") {
                val familyId = parts[1]
                val creatorId = parts[2]
                val creatorName = parts.getOrNull(3) ?: "Usuario"
                
                joinFamily(familyId, creatorName, creatorId)
            } else {
                Toast.makeText(this, "Código QR no válido", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar código QR", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun joinFamily(familyId: String, creatorName: String, creatorId: String) {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val familyRef = database.getReference("families").child(familyId)
            val memberUpdate = mapOf(user.uid to user.displayName ?: "Usuario")
            
            familyRef.child("members").updateChildren(memberUpdate)
                .addOnSuccessListener {
                    Toast.makeText(this, "¡Te has unido a la familia de $creatorName!", Toast.LENGTH_SHORT).show()
                    
                    // Añadir la familia al perfil del usuario
                    val userFamilyRef = database.getReference("users").child(user.uid).child("families")
                    userFamilyRef.child(familyId).setValue(creatorName)
                    
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al unirse a la familia: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "Debes iniciar sesión para unirte a una familia", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.qr_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_help -> {
                Toast.makeText(this, "Ayuda sobre códigos QR", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
