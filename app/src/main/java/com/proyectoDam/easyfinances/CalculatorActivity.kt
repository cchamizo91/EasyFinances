package com.proyectoDam.easyfinances

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CalculatorActivity : AppCompatActivity() {

    private lateinit var editTextNum1: EditText
    private lateinit var editTextNum2: EditText
    private lateinit var resultText: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnSubtract: Button
    private lateinit var btnMultiply: Button
    private lateinit var btnDivide: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botón de retroceso

        // Referencias a los elementos de la interfaz
        editTextNum1 = findViewById(R.id.editTextNum1)
        editTextNum2 = findViewById(R.id.editTextNum2)
        resultText = findViewById(R.id.textResult)
        btnAdd = findViewById(R.id.btnAdd)
        btnSubtract = findViewById(R.id.btnSubtract)
        btnMultiply = findViewById(R.id.btnMultiply)
        btnDivide = findViewById(R.id.btnDivide)

        // Configurar los botones
        btnAdd.setOnClickListener { calculate("+") }
        btnSubtract.setOnClickListener { calculate("-") }
        btnMultiply.setOnClickListener { calculate("*") }
        btnDivide.setOnClickListener { calculate("/") }
    }

    private fun calculate(operation: String) {
        val num1 = editTextNum1.text.toString().toDoubleOrNull()
        val num2 = editTextNum2.text.toString().toDoubleOrNull()

        if (num1 == null || num2 == null) {
            Toast.makeText(this, "Por favor, introduce valores válidos", Toast.LENGTH_SHORT).show()
            return
        }

        val result = when (operation) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> {
                if (num2 == 0.0) {
                    Toast.makeText(this, "No se puede dividir por cero", Toast.LENGTH_SHORT).show()
                    return
                }
                num1 / num2
            }
            else -> 0.0
        }

        resultText.text = "Resultado: $result"
    }
}
