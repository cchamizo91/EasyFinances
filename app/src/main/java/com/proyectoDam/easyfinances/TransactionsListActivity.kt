package com.proyectoDam.easyfinances

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.proyectoDam.easyfinances.adapters.TransactionAdapter
import com.proyectoDam.easyfinances.databinding.ActivityTransactionsListBinding
import com.proyectoDam.easyfinances.models.Transaction

class TransactionsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionsListBinding
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val transactionList = mutableListOf<Transaction>()
    private lateinit var transactionAdapter: TransactionAdapter
    private var selectedTransaction: Transaction? = null

    // Variables para el resumen financiero
    private var saldoActual: Double = 0.0
    private var totalGastos: Double = 0.0
    private var totalAhorro: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        listenForTransactions()  // Escucha en tiempo real las transacciones desde Firestore
        setupDeleteButton()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(transactionList) { transaction ->
            selectedTransaction = transaction
            binding.btnDeleteTransaction.visibility =
                if (selectedTransaction == null) View.GONE else View.VISIBLE
        }
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(this@TransactionsListActivity)
            adapter = transactionAdapter
            setHasFixedSize(true)
            visibility = View.VISIBLE
        }
    }

    private fun listenForTransactions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("TransactionsList", "Error en snapshot: ${error.message}")
                    Toast.makeText(this, "Error al cargar datos: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                // Reinicia la lista y los totales
                transactionList.clear()
                saldoActual = 0.0
                totalGastos = 0.0
                totalAhorro = 0.0

                snapshots?.forEach { document ->
                    val amount = document.getDouble("amount") ?: 0.0
                    val transactionType = document.getString("type") ?: "Desconocido"
                    val category = document.getString("category") ?: "Sin categoría"
                    val timestamp = document.getLong("timestamp") ?: 0L

                    val transaction = Transaction(
                        type = transactionType,
                        amount = amount,
                        category = category,
                        timestamp = timestamp
                    )
                    transactionList.add(transaction)

                    // Acumula totales según el tipo
                    if (transactionType == "Ingreso") {
                        saldoActual += amount
                    } else if (transactionType == "Gasto") {
                        saldoActual -= amount
                        totalGastos += amount
                    }
                }
                totalAhorro = saldoActual  // O ajústalo según tu lógica de ahorro

                // Se pasa una copia de la lista para evitar referencias compartidas
                transactionAdapter.updateList(transactionList.toList())
                updateSaldoUI()
            }
    }

    private fun setupDeleteButton() {
        binding.btnDeleteTransaction.setOnClickListener {
            selectedTransaction?.let { deleteTransaction(it) }
        }
    }

    // Elimina la transacción seleccionada (se asume que el timestamp es único)
    private fun deleteTransaction(transaction: Transaction) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .whereEqualTo("timestamp", transaction.timestamp)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Transacción eliminada.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al eliminar transacción.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al buscar la transacción.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateSaldoUI() {
        binding.textSaldoActual.text = "Saldo actual: €${String.format("%.2f", saldoActual)}"
        binding.textTotalGastos.text = "Total gastos: €${String.format("%.2f", totalGastos)}"
        binding.textTotalAhorro.text = "Total ahorro: €${String.format("%.2f", totalAhorro)}"
    }
}
