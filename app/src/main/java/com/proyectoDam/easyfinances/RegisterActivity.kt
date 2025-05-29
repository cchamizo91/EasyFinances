package com.proyectoDam.easyfinances

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyectoDam.easyfinances.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.btnRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
                Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email, password)
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    createUserDocument(userId)
                } else {
                    Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createUserDocument(userId: String) {
        val userDocument = hashMapOf(
            "initialBalance" to 0.0,
            "currentBalance" to 0.0
        )

        db.collection("users").document(userId)
            .set(userDocument)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                sendWelcomeNotification(userId)
                startActivity(Intent(this, TransactionActivity::class.java))
                finish()
            }
    }

    private fun sendWelcomeNotification(userId: String) {
        val message = mapOf(
            "title" to "¡Bienvenido a EASYFINANCES!",
            "body" to "Tu cuenta ha sido creada exitosamente."
        )

        db.collection("notifications").document(userId)
            .set(message)
    }
}
