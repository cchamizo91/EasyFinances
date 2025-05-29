package com.proyectoDam.easyfinances.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyectoDam.easyfinances.databinding.ItemReminderBinding
import com.proyectoDam.easyfinances.models.Reminder

class ReminderAdapter(private val reminders: List<Reminder>) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    // ViewHolder usando Binding
    class ReminderViewHolder(val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        // Inflar el diseño usando Binding
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        // Asignar datos directamente usando Binding
        holder.binding.txtReminderTitle.text = reminder.title
        holder.binding.txtReminderDescription.text = reminder.description
        holder.binding.txtReminderDate.text = reminder.date
    }

    override fun getItemCount(): Int = reminders.size
}


