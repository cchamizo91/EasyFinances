package com.proyectoDam.easyfinances

import android.app.LocaleManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.firebase.auth.FirebaseAuth
import com.proyectoDam.easyfinances.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // Cargamos el idioma guardado antes de crear la UI
        loadLocale()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setupNotificationChannel()
        setupListeners()
        setupLanguageButton()
    }

    // Configuración del canal de notificaciones (sin cambios)
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "finance_channel",
                "Notificaciones Financieras",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // Configuración de los listeners de las tarjetas (Iniciar sesión / Registrarse)
    private fun setupListeners() {
        binding.cardLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.cardRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Configuración del botón para cambio de idioma (el botón debe estar en el layout con id buttonLanguage)
    private fun setupLanguageButton() {
        binding.buttonLanguage.setOnClickListener {
            val languages = arrayOf("Español", "English")
            val languageCodes = arrayOf("es", "en")

            AlertDialog.Builder(this)
                .setTitle("Selecciona un idioma")
                .setItems(languages) { _, which ->
                    val selectedLanguage = languageCodes[which]
                    saveLocale(selectedLanguage)
                    applyLocale(selectedLanguage)
                    restartActivity()
                }
                .create()
                .show()
        }
    }

    // Guarda en SharedPreferences el idioma seleccionado
    private fun saveLocale(languageCode: String) {
        val prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit().putString("My_Lang", languageCode).apply()
    }

    // Lee la preferencia guardada y aplica el idioma
    private fun loadLocale() {
        val prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = prefs.getString("My_Lang", "es") ?: "es"
        applyLocale(language)
    }

    // Aplica el idioma usando las API modernas:
    // • En Android 13+ usamos LocaleManager
    // • En versiones anteriores usamos AppCompatDelegate.setApplicationLocales()
    private fun applyLocale(languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = getSystemService(LocaleManager::class.java)
            localeManager?.applicationLocales = android.os.LocaleList.forLanguageTags(languageCode)
        } else {
            val localeList = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    // Reinicia la actividad para que se apliquen los cambios en la interfaz
    private fun restartActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

