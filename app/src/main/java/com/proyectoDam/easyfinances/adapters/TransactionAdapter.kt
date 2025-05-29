package com.proyectoDam.easyfinances.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyectoDam.easyfinances.databinding.ItemTransactionBinding
import com.proyectoDam.easyfinances.models.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private var transactionList: MutableList<Transaction>,
    private val onTransactionClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        val amountFormatted = "€${String.format("%.2f", transaction.amount)}"
        val dateFormatted = formatDate(transaction.timestamp)

        holder.binding.textTransactionAmount.text = amountFormatted
        holder.binding.textTransactionCategory.text = transaction.category
        holder.binding.textTransactionDate.text = dateFormatted
        holder.binding.textTransactionType.text = transaction.type

        // Mejora de accesibilidad
        holder.binding.root.contentDescription =
            "Transacción ${transaction.type}, ${transaction.category}, $amountFormatted, $dateFormatted"

        holder.binding.root.setOnClickListener {
            onTransactionClick(transaction)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    // Actualiza la lista interna asignándole una copia de la nueva lista
    fun updateList(newList: List<Transaction>) {
        transactionList = newList.toMutableList()
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
