package com.proyectoDam.easyfinances

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import com.proyectoDam.easyfinances.models.Transaction
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PDFExportManager {

    /**
     * Exporta las transacciones a un archivo PDF físico en la carpeta pública de Descargas.
     * Devuelve true si se generó correctamente.
     */
    fun exportToPDF(context: Context, transactions: List<Transaction>): Boolean {
        if (transactions.isEmpty()) {
            Log.e("PDFExportManager", "No hay transacciones para exportar.")
            return false
        }
        return try {
            val document = PdfDocument()

            // Define el tamaño de la página (ajusta estos valores según tus necesidades)
            val pageWidth = 600
            val pageHeight = 800
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint()

            // Dibujar el título
            paint.textSize = 18f
            canvas.drawText("Resumen de Transacciones", 50f, 50f, paint)

            // Dibujar la lista de transacciones
            paint.textSize = 14f
            var yPosition = 100f
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            transactions.forEach { transaction ->
                val dateStr = sdf.format(Date(transaction.timestamp))
                val line = "$dateStr | ${transaction.type} | ${transaction.category} | ${transaction.amount}€"
                canvas.drawText(line, 50f, yPosition, paint)
                yPosition += 30f
            }

            document.finishPage(page)

            // Utilizar la carpeta pública de Descargas para guardar el PDF
            val downloadsDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val file = File(downloadsDir, "transacciones.pdf")
            FileOutputStream(file).use { fos ->
                document.writeTo(fos)
            }
            document.close()
            Log.d("PDFExportManager", "PDF escrito correctamente en: ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("PDFExportManager", "Error al exportar a PDF: ${e.message}", e)
            false
        }
    }
}

