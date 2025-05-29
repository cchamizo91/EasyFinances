package com.proyectoDam.easyfinances

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.proyectoDam.easyfinances.databinding.ActivityOptionBinding
import com.proyectoDam.easyfinances.models.Transaction
import java.io.File

class OptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOptionBinding
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth = FirebaseAuth.getInstance()
    private var transactionList: MutableList<Transaction> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verifica que el usuario esté autenticado
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupListeners()
        setupCerrarSesionListener()  // Configura el botón "Cerrar sesión"
    }

    private fun setupListeners() {
        binding.cardTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        binding.cardTransactionList.setOnClickListener {
            startActivity(Intent(this, TransactionsListActivity::class.java))
        }

        binding.cardHealth.setOnClickListener {
            startActivity(Intent(this, GraphicsActivity::class.java))
        }

        binding.cardCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        binding.cardExportPDF.setOnClickListener {
            fetchTransactionsAndExportPDF()
        }
    }

    // Configura el listener para el botón de "Cerrar sesión"
    private fun setupCerrarSesionListener() {
        binding.btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    private fun fetchTransactionsAndExportPDF() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                transactionList.clear()
                for (doc in documents) {
                    val amountField = doc.get("amount")
                    val amount: Double = when (amountField) {
                        is Number -> amountField.toDouble()
                        is String -> amountField.toDoubleOrNull() ?: 0.0
                        else -> 0.0
                    }
                    val transaction = Transaction(
                        type = doc.getString("type") ?: "Desconocido",
                        amount = amount,
                        category = doc.getString("category") ?: "Sin categoría",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                    transactionList.add(transaction)
                }
                if (transactionList.isNotEmpty()) {
                    exportTransactionsToPDF()
                } else {
                    Toast.makeText(this, "No hay transacciones para exportar.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener transacciones: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("OptionActivity", "Error al obtener transacciones", e)
            }
    }

    private fun exportTransactionsToPDF() {
        val success = PDFExportManager.exportToPDF(this, transactionList)
        if (success) {
            Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_SHORT).show()
            openGeneratedPDF()
        } else {
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGeneratedPDF() {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "transacciones.pdf")
        if (!file.exists()) {
            Toast.makeText(this, "El PDF no existe", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No se encontró una aplicación para abrir PDFs.", Toast.LENGTH_SHORT).show()
        }
    }

    // Método que muestra el diálogo de confirmación para cerrar sesión
    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas cerrar sesión?")
            .setPositiveButton("Sí") { dialog, _ ->
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
