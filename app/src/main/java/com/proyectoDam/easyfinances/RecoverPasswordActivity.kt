package com.proyectoDam.easyfinances

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.proyectoDam.easyfinances.databinding.ActivityRecoverPasswordBinding

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecoverPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRecoverPassword.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()

            if (email.isEmpty()) {
                showToast("Por favor, ingresa tu correo electrónico.")
            } else if (!isValidEmail(email)) {
                showToast("Por favor, ingresa un correo válido.")
            } else {
                sendRecoveryEmail(email)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendRecoveryEmail(email: String) {
        binding.progressBar.visibility = View.VISIBLE
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    showToast("Correo de recuperación enviado. Revisa tu bandeja de entrada.")
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Ocurrió un error inesperado."
                    showToast("Error: $errorMessage")
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                showToast("Error al enviar el correo. Inténtalo más tarde.")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
