package com.proyectoDam.easyfinances.models

data class Transaction(
        val type: String = "",
        val amount: Double = 0.0,
        val category: String = "",
        val timestamp: Long = 0L
)

