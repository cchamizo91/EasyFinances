package com.proyectoDam.easyfinances

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.proyectoDam.easyfinances.adapters.ReminderAdapter
import com.proyectoDam.easyfinances.databinding.ActivityCalendarBinding
import com.proyectoDam.easyfinances.models.Reminder
import com.proyectoDam.easyfinances.utils.EventDecorator
import java.util.Calendar

class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val reminders = mutableListOf<Reminder>()
    private lateinit var adapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ReminderAdapter(reminders)
        binding.recyclerReminders.layoutManager = LinearLayoutManager(this)
        binding.recyclerReminders.adapter = adapter

        binding.calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                val selectedDate = String.format("%02d/%02d/%d", date.day, date.month + 1, date.year)
                binding.txtSelectedDate.text = selectedDate

                if (binding.calendarView.selectedDates.contains(date)) {
                    loadRemindersForDate(selectedDate)
                    loadTransactionsForDate(selectedDate)
                } else {
                    binding.txtTransactionDetails.text = "" // Ahora se mantiene en blanco hasta seleccionar un evento
                }
            }
        }

        observeTransactionsAndReminders()
        loadAllReminders()

        binding.btnSaveReminder.setOnClickListener {
            val selectedDate = binding.txtSelectedDate.text.toString()
            val description = binding.etReminderInput.text.toString()

            if (description.isNotEmpty()) {
                saveReminder(selectedDate, description)
            } else {
                Toast.makeText(this, "Ingrese una descripción para el recordatorio.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDeleteReminder.setOnClickListener {
            val selectedDate = binding.txtSelectedDate.text.toString()
            deleteReminderForDate(selectedDate)
        }
    }

    private fun observeTransactionsAndReminders() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { transactions, _ ->
                val transactionDates = mutableListOf<CalendarDay>()

                for (doc in transactions!!) {
                    val timestamp = doc.getLong("timestamp") ?: continue
                    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
                    val day = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                    transactionDates.add(day)
                }

                db.collection("reminders")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { reminders, _ ->
                        val reminderDates = mutableListOf<CalendarDay>()
                        val mixedDates = mutableListOf<CalendarDay>()

                        for (doc in reminders!!) {
                            val dateStr = doc.getString("date") ?: continue
                            val dateParts = dateStr.split("/").map { it.toIntOrNull() }
                            if (dateParts.size == 3 && dateParts.all { it != null }) {
                                val day = CalendarDay.from(dateParts[2]!!, dateParts[1]!! - 1, dateParts[0]!!)

                                if (transactionDates.contains(day)) {
                                    mixedDates.add(day)
                                    transactionDates.remove(day) // Evita duplicar colores
                                } else {
                                    reminderDates.add(day)
                                }
                            }
                        }

                        // Aplicamos colores diferenciados
                        binding.calendarView.removeDecorators()
                        binding.calendarView.addDecorator(EventDecorator(Color.parseColor("#FFD700"), transactionDates)) // Amarillo claro
                        binding.calendarView.addDecorator(EventDecorator(Color.parseColor("#ADD8E6"), reminderDates)) // Azul claro
                        binding.calendarView.addDecorator(EventDecorator(Color.parseColor("#90EE90"), mixedDates)) // Verde agua para días con ambos eventos
                    }
            }
    }

    private fun loadAllReminders() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("reminders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                reminders.clear()
                documents.mapNotNullTo(reminders) {
                    try {
                        it.toObject(Reminder::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar los recordatorios.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRemindersForDate(date: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("reminders")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                reminders.clear()
                documents.mapNotNullTo(reminders) {
                    try {
                        it.toObject(Reminder::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                adapter.notifyDataSetChanged()
                binding.txtTransactionDetails.text =
                    if (reminders.isEmpty()) "" else reminders.joinToString("\n") { it.description }
            }
    }
    private var fixedTransactionText: String? = null  // Variable para mantener el último texto visible

    private fun loadTransactionsForDate(date: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val transactions = mutableListOf<String>()

                for (doc in documents) {
                    val timestamp = doc.getLong("timestamp") ?: continue
                    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
                    val formatted = String.format(
                        "%02d/%02d/%d",
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.YEAR)
                    )

                    if (formatted == date) {
                        val amount = doc.getDouble("amount") ?: 0.0
                        val type = doc.getString("type") ?: "Desconocido"
                        val category = doc.getString("category") ?: "Sin categoría"

                        // Seleccionamos el ícono adecuado según el tipo
                        val icon = when (type) {
                            "Ingreso" -> "💰"
                            "Gasto" -> "💸"
                            else -> "💵"
                        }

                        transactions.add("$icon $amount€ - $category")
                    }
                }

                // Solo actualizar el texto si hay transacciones nuevas
                fixedTransactionText = if (transactions.isNotEmpty()) {
                    transactions.joinToString("\n")
                } else {
                    null // Si no hay datos en la nueva fecha, no arrastrar los anteriores
                }

                binding.txtTransactionDetails.text = fixedTransactionText ?: ""

                // Mostrar un Dialog en lugar de modificar txtTransactionDetails
                if (fixedTransactionText != null) {
                    AlertDialog.Builder(this)
                        .setTitle("Detalles de Transacción")
                        .setMessage(fixedTransactionText)
                        .setPositiveButton("Cerrar", null)
                        .show()
                }
            }
    }




    private fun saveReminder(date: String, description: String) {
        val userId = auth.currentUser?.uid ?: return

        if (date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Fecha o descripción vacía. No se puede guardar.", Toast.LENGTH_SHORT).show()
            return
        }

        val newReminder = hashMapOf(
            "userId" to userId,
            "date" to date,
            "description" to description
        )

        db.collection("reminders")
            .add(newReminder)
            .addOnSuccessListener {
                Toast.makeText(this, "Recordatorio guardado.", Toast.LENGTH_SHORT).show()
                loadRemindersForDate(date)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el recordatorio.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteReminderForDate(date: String) {
        val userId = auth.currentUser?.uid ?: return

        if (date.isEmpty()) {
            Toast.makeText(this, "No se puede eliminar: Fecha inválida.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("reminders")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    doc.reference.delete()
                }
                Toast.makeText(this, "Recordatorios eliminados para $date.", Toast.LENGTH_SHORT).show()
                loadRemindersForDate(date) // Refrescar la lista después de eliminar
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar recordatorio.", Toast.LENGTH_SHORT).show()
            }
    }
}

