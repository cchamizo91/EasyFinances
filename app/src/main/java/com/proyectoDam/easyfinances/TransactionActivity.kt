package com.proyectoDam.easyfinances

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyectoDam.easyfinances.databinding.ActivityTransactionBinding
import java.util.Calendar

class TransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionBinding
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var selectedDate: Long = 0L  // Se usará como timestamp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupListeners()
    }

    // Configuramos el Spinner con las categorías
    private fun setupSpinner() {
        val categories = listOf("Sin categoría", "Comida", "Transporte", "Ocio", "Salud", "Educación","Nómina","Garaje")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnSaveTransaction.setOnClickListener { saveTransaction() }
        binding.btnExit.setOnClickListener { exitActivity() }
    }

    // Muestra un DatePicker para elegir la fecha y la convierne en timestamp
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val cal = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
                }
                selectedDate = cal.timeInMillis
                binding.textSelectedDate.text =
                    "Fecha seleccionada: $selectedDay/${selectedMonth + 1}/$selectedYear"
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Guarda la transacción en Firestore
    private fun saveTransaction() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val amount = binding.editTextAmount.text.toString().toDoubleOrNull()
        val type = if (binding.checkboxIncome.isChecked) "Ingreso" else "Gasto"
        val selectedCategory = binding.spinnerCategory.selectedItem as? String ?: "Sin categoría"

        if (amount == null || amount <= 0 || selectedDate == 0L) {
            Toast.makeText(this, "Introduce un monto válido y selecciona una fecha.", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = hashMapOf(
            "amount" to amount,
            "type" to type,
            "timestamp" to selectedDate,
            "category" to selectedCategory,
            "userId" to userId // ✅ Asociar transacción con el usuario actual
        )

        db.collection("transactions")
            .add(transaction)
            .addOnSuccessListener {
                Toast.makeText(this, "Transacción guardada.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar la transacción.", Toast.LENGTH_SHORT).show()
            }
    }


    private fun exitActivity() {
        startActivity(Intent(this, OptionActivity::class.java))
        finish()
    }
}
