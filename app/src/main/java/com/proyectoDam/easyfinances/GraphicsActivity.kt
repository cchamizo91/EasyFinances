package com.proyectoDam.easyfinances

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.proyectoDam.easyfinances.models.Transaction

class GraphicsActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var textTotalIngresos: TextView
    private lateinit var textTotalGastos: TextView
    private lateinit var textTotalAhorro: TextView

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val transactionList = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphics)

        // Asociación de vistas (asegúrate de que los IDs coinciden con tu layout)
        barChart = findViewById(R.id.barChart)
        textTotalIngresos = findViewById(R.id.textTotalIngresos)
        textTotalGastos = findViewById(R.id.textTotalGastos)
        textTotalAhorro  = findViewById(R.id.textTotalAhorro)

        // Listener en tiempo real para actualizar tanto totales como el gráfico
        syncTransactionsWithFirebase()
    }

    private fun syncTransactionsWithFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("GraphicsActivity", "Error al escuchar cambios: ${error.message}")
                    return@addSnapshotListener
                }
                transactionList.clear()
                snapshots?.forEach { document ->
                    val transaction = Transaction(
                        type = document.getString("type") ?: "Desconocido",
                        amount = document.getDouble("amount") ?: 0.0,
                        category = document.getString("category") ?: "Sin categoría",
                        timestamp = document.getLong("timestamp") ?: 0L
                    )
                    transactionList.add(transaction)
                }
                updateTotalsAndChart(transactionList)
            }
    }

    private fun updateTotalsAndChart(transactions: List<Transaction>) {
        var totalIngresos = 0.0
        var totalGastos = 0.0

        transactions.forEach { transaction ->
            when (transaction.type) {
                "Ingreso" -> totalIngresos += transaction.amount
                "Gasto" -> totalGastos += transaction.amount
            }
        }

        val totalAhorro = totalIngresos - totalGastos

        // Actualizar TextViews
        textTotalIngresos.text = "Total ingresos: €${String.format("%.2f", totalIngresos)}"
        textTotalGastos.text = "Total gastos: €${String.format("%.2f", totalGastos)}"
        textTotalAhorro.text  = "Total ahorro: €${String.format("%.2f", totalAhorro)}"

        // Actualizar gráfico agregando una tercera barra para el ahorro
        val entries = mutableListOf<BarEntry>(
            BarEntry(0f, totalIngresos.toFloat()),
            BarEntry(1f, totalGastos.toFloat()),
            BarEntry(2f, totalAhorro.toFloat())
        )

        // Asigna colores:
        // - Total ingresos: verde (#4CAF50)
        // - Total gastos: rojo (#F44336)
        // - Total ahorro: naranja (#FF9800)
        val dataSet = BarDataSet(entries, "Resumen Financiero").apply {
            setColors(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#F44336"),
                Color.parseColor("#FF9800")
            )
            valueTextSize = 16f
        }

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.invalidate()  // Refresca el gráfico
    }
}
