package com.proyectoDam.easyfinances

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.FileOutputStream
import java.io.IOException

class MyPrintDocumentAdapter(private val context: Context) : PrintDocumentAdapter() {

    private var pdfDocument: PdfDocument? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal,
        layoutResultCallback: LayoutResultCallback,
        extras: Bundle?
    ) {
        // Creamos el documento PDF en memoria
        pdfDocument = PdfDocument()

        // Definimos el tamaño de la página (por ejemplo, 600 x 800)
        val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()

        // Creamos una página y dibujamos en ella
        val page = pdfDocument!!.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Dibujar un título en el PDF
        paint.textSize = 18f
        canvas.drawText("Resumen de Transacciones", 50f, 50f, paint)

        // Aquí podrías iterar sobre una lista de transacciones y dibujarlas en el canvas
        // Por ejemplo:
        // paint.textSize = 14f
        // var yPosition = 100f
        // for (transaction in listTransactions) {
        //     val line = "${transaction.fecha} | ${transaction.tipo} | ${transaction.categoria} | ${transaction.monto}€"
        //     canvas.drawText(line, 50f, yPosition, paint)
        //     yPosition += 30f
        // }

        pdfDocument!!.finishPage(page)

        if (cancellationSignal.isCanceled) {
            layoutResultCallback.onLayoutCancelled()
            return
        }

        // Definimos la información del documento que se muestra en el diálogo
        val info = PrintDocumentInfo.Builder("informe.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)  // Modifica este valor si hay más páginas
            .build()

        layoutResultCallback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        writeResultCallback: WriteResultCallback
    ) {
        try {
            // Escribimos el contenido del PDF en el descriptor de archivo
            pdfDocument?.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: IOException) {
            writeResultCallback.onWriteFailed(e.toString())
            return
        } finally {
            pdfDocument?.close()
            pdfDocument = null
        }
        writeResultCallback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }
}
