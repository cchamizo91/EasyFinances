package com.proyectoDam.easyfinances.managers

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyectoDam.easyfinances.models.Transaction

object SmartNotificationsManager {

    /**
     * Analiza las transacciones y, si detecta que el gasto en la categoría "Restaurante"
     * supera un umbral definido, envía una notificación inteligente al usuario.
     */
    fun analyzeSpendingAndNotify(transactionList: List<Transaction>) {
        val restaurantSpending = transactionList
            .filter { it.category.equals("Restaurante", ignoreCase = true) }
            .sumOf { it.amount }
        val threshold = 100.0  // Este umbral lo puedes ajustar según tus necesidades
        if (restaurantSpending > threshold) {
            sendSmartNotification(
                "Gasto Excesivo en Restaurantes",
                "Has gastado €${String.format("%.2f", restaurantSpending)} en restaurantes. Considera ajustar tu presupuesto para ahorrar."
            )
        }
    }

    /**
     * Envía una notificación inteligente usando Firebase Firestore. Se asume
     * que el usuario ya está autenticado y se utiliza su UID para guardar la notificación.
     */
    private fun sendSmartNotification(title: String, message: String) {
        // Obtén el ID del usuario actual; si no está autenticado, se salta la notificación.
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("SmartNotificationsManager", "No hay usuario autenticado, no se puede enviar la notificación.")
            return
        }

        val notificationData = hashMapOf(
            "title" to title,
            "body" to message
        )
        FirebaseFirestore.getInstance().collection("notifications").document(userId)
            .set(notificationData)
            .addOnSuccessListener {
                Log.d("SmartNotificationsManager", "Notificación inteligente enviada: $title")
            }
            .addOnFailureListener { e ->
                Log.e("SmartNotificationsManager", "Error al enviar la notificación inteligente: ${e.message}")
            }
    }
}
