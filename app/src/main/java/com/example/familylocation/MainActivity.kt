package com.example.familylocation

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.familylocation.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        
        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        
        // Verificar autenticación
        checkAuthentication()
        
        // Configurar botones
        setupButtons()
    }
    
    private fun checkAuthentication() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            // Usuario no autenticado - mostrar opción de login
            showLoginPrompt()
        } else {
            // Usuario autenticado
            showWelcomeMessage(currentUser.email ?: "Usuario")
        }
    }
    
    private fun showLoginPrompt() {
        binding.welcomeText.text = "Bienvenido a Family Location Tracker"
        binding.subtitleText.text = "Inicia sesión para comenzar"
        binding.loginButton.visibility = android.view.View.VISIBLE
        
        binding.loginButton.setOnClickListener {
            // Aquí iría la lógica de autenticación
            Toast.makeText(this, "Función de login en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showWelcomeMessage(email: String) {
        binding.welcomeText.text = "Hola, $email"
        binding.subtitleText.text = "Gestiona tu ubicación familiar"
        binding.loginButton.visibility = android.view.View.GONE
    }
    
    private fun setupButtons() {
        binding.mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        
        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        
        binding.qrButton.setOnClickListener {
            val intent = Intent(this, QRActivity::class.java)
            startActivity(intent)
        }
        
        binding.settingsButton.setOnClickListener {
            Toast.makeText(this, "Configuración en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                firebaseAuth.signOut()
                recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onResume() {
        super.onResume()
        checkAuthentication()
    }
}
