package com.proyectoDam.easyfinances.managers

import android.content.Context
import android.os.Environment
import android.util.Log
import com.proyectoDam.easyfinances.models.Transaction
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportManager {

    /**
     * Exporta la lista de transacciones a un archivo Excel (.xlsx).
     * Devuelve true si la exportación fue exitosa o false en caso de error.
     */
    fun exportToExcel(context: Context, transactions: List<Transaction>): Boolean {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Transacciones")

            // Crea la fila de cabecera
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Fecha")
            headerRow.createCell(1).setCellValue("Tipo")
            headerRow.createCell(2).setCellValue("Categoría")
            headerRow.createCell(3).setCellValue("Cantidad")

            // Rellena las filas con las transacciones
            transactions.forEachIndexed { index, transaction ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(formatTimestamp(transaction.timestamp))
                row.createCell(1).setCellValue(transaction.type)
                row.createCell(2).setCellValue(transaction.category)
                row.createCell(3).setCellValue(transaction.amount)
            }

            // Directorio de salida: Asegúrate de que tu aplicación tiene permisos para escribir.
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (directory != null && !directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, "transacciones.xlsx")
            FileOutputStream(file).use { fos ->
                workbook.write(fos)
            }
            workbook.close()
            true
        } catch (e: Exception) {
            Log.e("ExportManager", "Error al exportar a Excel: ${e.message}")
            false
        }
    }

    /**
     * Formatea el timestamp (en milisegundos) a una cadena de fecha legible.
     */
    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

