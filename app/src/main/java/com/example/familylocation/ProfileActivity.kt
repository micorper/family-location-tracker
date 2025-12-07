package com.example.familylocation

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.familylocation.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.Calendar

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    
    private val calendar = Calendar.getInstance()
    private var selectedDate = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mi Perfil"
        
        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        
        // Cargar información del usuario
        loadUserProfile()
        
        // Configurar listeners
        setupListeners()
    }
    
    private fun loadUserProfile() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            // Cargar nombre
            binding.nameInput.setText(user.displayName ?: "")
            
            // Cargar email
            binding.emailInput.setText(user.email ?: "")
            
            // Cargar teléfono si está disponible
            binding.phoneInput.setText(user.phoneNumber ?: "")
            
            // Cargar foto de perfil si está disponible
            user.photoUrl?.let { photoUri ->
                // Aquí cargarías la imagen en un ImageView usando una librería como Glide
                // binding.profileImage.setImageURI(photoUri)
            }
            
            // Establecer fecha de nacimiento por defecto (opcional)
            binding.birthDateInput.setOnClickListener {
                showDatePicker()
            }
        }
    }
    
    private fun setupListeners() {
        // Botón para seleccionar foto de perfil
        binding.changePhotoButton.setOnClickListener {
            selectProfilePicture()
        }
        
        // Botón para guardar cambios
        binding.saveButton.setOnClickListener {
            saveProfileChanges()
        }
        
        // Botón para cambiar contraseña
        binding.changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
        
        // Botón para cerrar sesión
        binding.logoutButton.setOnClickListener {
            logout()
        }
        
        // Configuraciones adicionales
        binding.notificationSettings.setOnClickListener {
            Toast.makeText(this, "Configuración de notificaciones en desarrollo", Toast.LENGTH_SHORT).show()
        }
        
        binding.privacySettings.setOnClickListener {
            Toast.makeText(this, "Configuración de privacidad en desarrollo", Toast.LENGTH_SHORT).show()
        }
        
        binding.familyMembers.setOnClickListener {
            Toast.makeText(this, "Gestión de miembros familiares en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.birthDateInput.setText(selectedDate)
            },
            year,
            month,
            day
        )
        
        datePickerDialog.show()
    }
    
    private fun selectProfilePicture() {
        // Aquí implementarías la lógica para seleccionar una imagen
        Toast.makeText(this, "Selección de foto en desarrollo", Toast.LENGTH_SHORT).show()
    }
    
    private fun saveProfileChanges() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val name = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val phone = binding.phoneInput.text.toString().trim()
            
            if (name.isEmpty()) {
                binding.nameInput.error = "El nombre es obligatorio"
                return
            }
            
            if (email.isEmpty()) {
                binding.emailInput.error = "El email es obligatorio"
                return
            }
            
            // Crear request para actualizar perfil
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            
            // Actualizar perfil
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Actualizar email si cambió
                        if (email != user.email) {
                            user.updateEmail(email)
                                .addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Error al actualizar email: ${emailTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Error al actualizar perfil: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    
    private fun showChangePasswordDialog() {
        // Aquí implementarías un diálogo para cambiar contraseña
        Toast.makeText(this, "Cambio de contraseña en desarrollo", Toast.LENGTH_SHORT).show()
    }
    
    private fun logout() {
        firebaseAuth.signOut()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        
        // Redirigir a MainActivity o LoginActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_help -> {
                Toast.makeText(this, "Ayuda del perfil", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // Manejar resultado de selección de imagen
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            data.data?.let { uri ->
                // Aquí cargarías la imagen seleccionada
                // binding.profileImage.setImageURI(uri)
                Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
