package net.irivas.cobrosapp.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.ResumenAdapter
import net.irivas.cobrosapp.adapters.TopComerciantesAdapter
import net.irivas.cobrosapp.adapters.TopPuestoAdapter
import net.irivas.cobrosapp.data.CobroDTO
import net.irivas.cobrosapp.data.CobroPorPuesto
import net.irivas.cobrosapp.data.CobroResumen
import net.irivas.cobrosapp.data.CobrosDBHelper
import net.irivas.cobrosapp.data.Metricas
import net.irivas.cobrosapp.data.ReporteStats
import net.irivas.cobrosapp.data.TopComerciante
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.collections.take

class ReportesActivity : AppCompatActivity() {
    private lateinit var inputFechaInicio: TextView
    private lateinit var inputFechaFin: TextView
    private lateinit var iconFechaInicio: ImageView
    private lateinit var iconFechaFin: ImageView
    private lateinit var btnGenerarReporte: Button
    private lateinit var txtTotalPeriodo: TextView
    private lateinit var cardEmpty: MaterialCardView
    private lateinit var db: CobrosDBHelper
    private lateinit var cardTotal: MaterialCardView

    // text de la card de resumen
    private lateinit var txtStatsTitle: TextView
    private lateinit var txtStatsTotalCobrado: TextView
    private lateinit var txtStatsTotalRecibido: TextView
    private lateinit var txtStatsTotalVuelto: TextView
    private lateinit var txtStatsNumeroCobros: TextView
    private lateinit var txtStatsPromedioCobro: TextView

    // cards
    private lateinit var cardCobrosPorPuesto: MaterialCardView
    private lateinit var cardTopComerciantes: MaterialCardView
    private lateinit var cardTablaResumen : MaterialCardView
    private lateinit var cardResumenAnalisisMetricas : MaterialCardView

    // txt de metricas
    private lateinit var txtDiaMayor: TextView
    private lateinit var txtDiaMenor: TextView
    private lateinit var txtComercianteTop: TextView


    // reciclers
    private lateinit var recyclerResumen : RecyclerView
    private lateinit var recyclerTopComerciantes: RecyclerView
    private lateinit var recyclerPuestos: RecyclerView

    // adapters
    private lateinit var adapterResumen : ResumenAdapter
    private lateinit var adapterTopPuestos: TopPuestoAdapter
    private lateinit var adapterTopComerciantes : TopComerciantesAdapter


    private val listaPuestos = mutableListOf<CobroPorPuesto>()
    private val listaComerciantes = mutableListOf<TopComerciante>()
    private val listaResumen = mutableListOf<CobroResumen>()

    private lateinit var fabExportarPdf: FloatingActionButton
    // variables para el pdf
    private lateinit var stats: ReporteStats
    private lateinit var metricas: Metricas


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        db = CobrosDBHelper(this)
        inputFechaInicio = findViewById(R.id.inputFechaInicio)
        iconFechaInicio = findViewById(R.id.iconFechaInicio)
        inputFechaFin = findViewById(R.id.inputFechaFinal)
        iconFechaFin = findViewById(R.id.iconFechaFinal)
        btnGenerarReporte = findViewById(R.id.btnGenerarReporte)
        txtTotalPeriodo = findViewById(R.id.txtTotalRecaudado)

        // cards
        cardEmpty = findViewById(R.id.cardEmpty)
        cardTotal = findViewById(R.id.cardTotal)
        cardResumenAnalisisMetricas = findViewById(R.id.cardResumenAnalisisMetricas)
        cardTablaResumen = findViewById(R.id.cardTablaResumen)
        cardTopComerciantes =findViewById(R.id.cardTopComerciantes)
        cardCobrosPorPuesto = findViewById(R.id.cardCobrosPorPuesto)

        // boton para exportar pdf
        fabExportarPdf = findViewById(R.id.fabExportarPdf)

        // seccion de metricas/stats
        txtStatsTitle = findViewById(R.id.txtTituloResumen)
        txtStatsTotalCobrado = findViewById(R.id.txtStatsTotalCobrado)
        txtStatsTotalRecibido = findViewById(R.id.txtStatsTotalRecibido)
        txtStatsTotalVuelto = findViewById(R.id.txtStatsTotalVuelto)
        txtStatsNumeroCobros = findViewById(R.id.txtStatsNumeroCobros)
        txtStatsPromedioCobro = findViewById(R.id.txtStatsPromedioCobro)
        txtDiaMayor = findViewById(R.id.txtDiaMayor)
        txtDiaMenor = findViewById(R.id.txtDiaMenor)
        txtComercianteTop = findViewById(R.id.txtComercianteTop)

        // configuracion de recyclers
        recyclerResumen = findViewById(R.id.recyclerResumen)
        recyclerTopComerciantes = findViewById(R.id.recyclerTopComerciantes)
        recyclerPuestos = findViewById(R.id.recyclerPuestos)

        recyclerResumen.layoutManager = LinearLayoutManager(this)
        recyclerTopComerciantes.layoutManager = LinearLayoutManager(this)
        recyclerPuestos.layoutManager = LinearLayoutManager(this)

        adapterResumen = ResumenAdapter(listaResumen)
        adapterTopComerciantes = TopComerciantesAdapter(listaComerciantes)
        adapterTopPuestos = TopPuestoAdapter(listaPuestos)

        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        val drawable = ContextCompat.getDrawable(this, R.drawable.divider_line)
        if (drawable != null) {
            divider.setDrawable(drawable)
        }

        recyclerResumen.addItemDecoration(divider)
        recyclerTopComerciantes.addItemDecoration(divider)
        recyclerPuestos.addItemDecoration(divider)

        recyclerResumen.adapter = adapterResumen
        recyclerTopComerciantes.adapter = adapterTopComerciantes
        recyclerPuestos.adapter = adapterTopPuestos

        // listener de fechas
        inputFechaInicio.setOnClickListener { mostrarCalendario(inputFechaInicio) }
        iconFechaInicio.setOnClickListener { mostrarCalendario(inputFechaInicio) }
        inputFechaFin.setOnClickListener { mostrarCalendario(inputFechaFin) }
        iconFechaFin.setOnClickListener { mostrarCalendario(inputFechaFin) }


        btnGenerarReporte.setOnClickListener {
            val fechaInicio = inputFechaInicio.text.toString().trim()
            val fechaFin = inputFechaFin.text.toString().trim()

            if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                Toast.makeText(this, "Seleccione ambas fechas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inicioSQL = convertirFechaAFormatoSQL(fechaInicio)
            val finSQL = convertirFechaAFormatoSQL(fechaFin)

            if (inicioSQL == null || finSQL == null) {
                Toast.makeText(this, "Fechas inválidas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lista = db.obtenerCobrosConInfoPorFecha(inicioSQL, finSQL)

            if (lista.isEmpty()) {
                showOrHideCards(false)
                txtTotalPeriodo.text = "$0.00"

                listaResumen.clear()
                listaComerciantes.clear()
                listaPuestos.clear()

                adapterResumen.notifyDataSetChanged()
                adapterTopComerciantes.notifyDataSetChanged()
                adapterTopPuestos.notifyDataSetChanged()
                return@setOnClickListener
            }

            showOrHideCards(true)

            val periodoTexto = "$fechaInicio al $fechaFin"
            stats = generarReporteStats(lista, periodoTexto)

            // stats
            txtStatsTitle.text = "Reporte del ${periodoTexto}"
            txtStatsTotalCobrado.text = "Total cobrado: $${"%.2f".format(stats.totalCobrado)}"
            txtStatsTotalRecibido.text = "Total recibido: $${"%.2f".format(stats.totalRecibido)}"
            txtStatsTotalVuelto.text = "Total vuelto: $${"%.2f".format(stats.totalVuelto)}"
            txtStatsNumeroCobros.text = "Cobros realizados: ${stats.numeroCobros}"
            txtStatsPromedioCobro.text = "Promedio por cobro: $${"%.2f".format(stats.PromedioPorCobro)}"

            txtTotalPeriodo.text = "$${"%.2f".format(stats.totalCobrado)}"

            // metricas
            metricas = generarMetricas(lista)
            txtDiaMayor.text = "Día de mayor recaudación: ${metricas.diaMayor} ($${"%.2f".format(metricas.montoMayor)})"
            txtDiaMenor.text = "Día de menor recaudación: ${metricas.diaMenor} ($${"%.2f".format(metricas.montoMenor)})"
            txtComercianteTop.text = "Comerciante que más pagó: ${metricas.comercianteTop} ($${"%.2f".format(metricas.montoTop)})"

            //  CARGAR RESUMEN
            listaResumen.clear()
            listaResumen.addAll(generarResumen(lista, 5))
            adapterResumen.notifyDataSetChanged()

            //  TOP COMERCIANTES
            listaComerciantes.clear()
            listaComerciantes.addAll(generarTopComerciantes(lista, 5))
            adapterTopComerciantes.notifyDataSetChanged()

            //  PUESTOS
            listaPuestos.clear()
            listaPuestos.addAll(generarCobrosPorPuesto(lista, 5))
            adapterTopPuestos.notifyDataSetChanged()

        }

        fabExportarPdf.setOnClickListener {
            if (listaResumen.isEmpty() || listaComerciantes.isEmpty() || listaPuestos.isEmpty()) {
                Toast.makeText(this, "Primero genere el reporte", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fechaInicio = inputFechaInicio.text.toString().trim()
            val fechaFin = inputFechaFin.text.toString().trim()

            // genera el pdf
            exportarPdf(
                listaResumen,
                listaComerciantes,
                listaPuestos,
                stats,
                metricas,
                fechaInicio,
                fechaFin
            )
        }
    }


    private fun mostrarCalendario(inputFecha: TextView) {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            inputFecha.setText("$d/${m + 1}/$y")
        }, year, month, day)

        datePicker.show()
    }

    private fun convertirFechaAFormatoSQL(fechaUsuario: String): String? {
        return try {
            val entrada = DateTimeFormatter.ofPattern("d/M/yyyy")
            val salida = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fecha = LocalDate.parse(fechaUsuario, entrada)
            fecha.format(salida)
        } catch (e: Exception) {
            null
        }
    }

    private fun generarReporteStats(lista: List<CobroDTO>, periodo:String): ReporteStats {
        val totalCobros = lista.sumOf { it.monto }
        val totalRecibido = lista.sumOf { it.recibido }
        val totalVuelto = lista.sumOf { it.vuelto }
        val numeroCobros = lista.size
        val promedioPorCobro = totalCobros / numeroCobros

        return ReporteStats(periodo, totalCobros, totalRecibido, totalVuelto, numeroCobros,promedioPorCobro )
    }

    private fun generarResumen(lista: List<CobroDTO>, topN:Int? = null): List<CobroResumen> {

        return lista.map { cobro ->
            // formatear fecha si es posible, si no, deja la original
            val fechaFormateada = convertirFechaFormatoStandard(cobro.fecha)

            CobroResumen(
                fecha = fechaFormateada,
                comerciante = cobro.nombreComerciante ?: "Desconocido",
                puesto = cobro.numeroPuesto ?: "?",
                monto = cobro.monto
            )
        }.let { if (topN != null) it.take(topN) else it }
    }

    private fun generarTopComerciantes(lista: List<CobroDTO>, topN: Int? = null): List<TopComerciante> {
        return lista
            .groupBy { it.nombreComerciante ?: "Desconocido" }      // agrupa por nombre (seguro si es null)
            .map { (nombre, cobros) ->
                TopComerciante(
                    nombre = nombre,
                    totalCobros = cobros.size,                      // cantidad de cobros
                    totalPagado = cobros.sumOf { it.monto }        // suma de montos
                )
            }
            .sortedByDescending { it.totalPagado }                 // ordena por total pagado (desc)
            .let { if (topN != null) it.take(topN) else it }
    }

    private fun generarCobrosPorPuesto(lista: List<CobroDTO>, topN: Int? = null): List<CobroPorPuesto> {
        return lista
            .groupBy { it.numeroPuesto ?: "?" }
            .map { (puesto, cobros) ->
                CobroPorPuesto(
                    numeroPuesto = puesto,
                    totalCobros = cobros.size,
                    totalMonto = cobros.sumOf { it.monto }
                )
            }
            .sortedByDescending { it.totalMonto }
            .let { if (topN != null) it.take(topN) else it }
    }

    private fun generarMetricas(lista: List<CobroDTO>): Metricas {

        // === DIA DE MAYOR Y MENOR RECAUDACIÓN ===
        val agrupadoPorDia = lista.groupBy { it.fecha }

        val diaMayor = agrupadoPorDia.maxByOrNull { (_, cobros) ->
            cobros.sumOf { it.monto }
        }?.let { entry ->
            val fecha = entry.key
            val total = entry.value.sumOf { it.monto }
            fecha to total
        } ?: ("-" to 0.0)

        val diaMenor = agrupadoPorDia.minByOrNull { (_, cobros) ->
            cobros.sumOf { it.monto }
        }?.let { entry ->
            val fecha = entry.key
            val total = entry.value.sumOf { it.monto }
            fecha to total
        } ?: ("-" to 0.0)

        // === COMERCIANTE CON MÁS PAGO ===
        val comercianteTop = lista.groupBy { it.nombreComerciante ?: "Desconocido" }
            .map { (nombre, cobros) ->
                nombre to cobros.sumOf { it.monto }
            }
            .maxByOrNull { it.second }
            ?.let { it.first to it.second }
            ?: ("-" to 0.0)

        val fechaMayorRecaudacion = convertirFechaFormatoStandard(diaMayor.first)
        val fechaMenorRecaudacion = convertirFechaFormatoStandard(diaMenor.first)

        return Metricas(
            diaMayor = fechaMayorRecaudacion,
            montoMayor = diaMayor.second,
            diaMenor = fechaMenorRecaudacion,
            montoMenor = diaMenor.second,
            comercianteTop = comercianteTop.first,
            montoTop = comercianteTop.second
        )
    }

    private fun convertirFechaFormatoStandard(fecha:String): String {
        val formatoEntrada = DateTimeFormatter.ISO_LOCAL_DATE
        val formatoSalida = DateTimeFormatter.ofPattern("d/M/yyyy")

        val fechaFormateada = try {
            LocalDate.parse(fecha, formatoEntrada).format(formatoSalida)
        } catch (e: Exception) {
            fecha
        }

        return fechaFormateada
    }

    private fun showOrHideCards(show: Boolean) {
        if (show) animateCardOut(cardEmpty) else animateCardIn(cardEmpty)

        if (show) {

            // el boton de pdf aparece
            fabExportarPdf.show()

            var delay = 0L
            val delayStep = 120L  // delay

            val cards = listOf(
                cardTotal,
                cardTablaResumen,
                cardTopComerciantes,
                cardCobrosPorPuesto,
                cardResumenAnalisisMetricas
            )

            // agrega una animacion con delay una tras otra card
            cards.forEach { card ->
                animateCardInStaggered(card, delay)
                delay += delayStep
            }

        } else {
            // Se ocultan todas rápidamente
            animateCardOut(cardTotal)
            animateCardOut(cardTablaResumen)
            animateCardOut(cardTopComerciantes)
            animateCardOut(cardCobrosPorPuesto)
            animateCardOut(cardResumenAnalisisMetricas)
            fabExportarPdf.hide()
        }
    }

    // animaciones
    private fun animateCardIn(view: View) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.translationY = 50f

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(250)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun animateCardOut(view: View) {
        view.animate()
            .alpha(0f)
            .translationY(50f)
            .setDuration(200)
            .withEndAction { view.visibility = View.GONE }
            .start()
    }

    private fun animateCardInStaggered(view: View, delay: Long) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.translationY = 50f

        view.animate()
            .setStartDelay(delay)
            .alpha(1f)
            .translationY(0f)
            .setDuration(260)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    fun exportarPdf(
        listaResumen: List<CobroResumen>,
        listaComerciantes: List<TopComerciante>,
        listaPuestos: List<CobroPorPuesto>,
        stats: ReporteStats,
        metricas: Metricas,
        fechaInicio: String,
        fechaFin: String
    ) {
        val pdf = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1

        val paintTitle = Paint().apply {
            color = Color.BLACK
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }
        val paintSubTitle = Paint().apply {
            color = Color.DKGRAY
            textSize = 18f
        }
        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        val rowHeight = 25f

        fun drawTable(
            canvas: Canvas,
            startX: Float,
            startY: Float,
            headers: List<String>,
            rows: List<List<String>>,
            columnWidths: List<Float>
        ): Float {
            var y = startY
            var x = startX
            val headerPaint = Paint(paintText).apply { typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD) }

            // Dibujar encabezados
            headers.forEachIndexed { i, header ->
                canvas.drawText(header, x + 4f, y + rowHeight - 6f, headerPaint)
                x += columnWidths[i]
            }
            y += rowHeight
            canvas.drawLine(startX, y, startX + columnWidths.sum(), y, linePaint)

            // Dibujar filas
            rows.forEachIndexed { rowIndex, row ->
                x = startX
                // Fondo alternado
                if (rowIndex % 2 == 0) {
                    val bgPaint = Paint().apply { color = Color.parseColor("#F5F5F5") }
                    canvas.drawRect(startX, y, startX + columnWidths.sum(), y + rowHeight, bgPaint)
                }
                row.forEachIndexed { i, cell ->
                    val align = if (i == row.size - 1) Paint.Align.RIGHT else Paint.Align.LEFT
                    val cellPaint = Paint(paintText).apply { textAlign = align }
                    val drawX = if (align == Paint.Align.RIGHT) x + columnWidths[i] - 4f else x + 4f
                    canvas.drawText(cell, drawX, y + rowHeight - 6f, cellPaint)
                    x += columnWidths[i]
                }
                y += rowHeight
                canvas.drawLine(startX, y, startX + columnWidths.sum(), y, linePaint)
            }
            return y
        }

        // --- PRIMERA PÁGINA ---
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
        var page = pdf.startPage(pageInfo)
        var canvas = page.canvas
        var y = 50f

        canvas.drawText("Reporte de Cobros", 40f, y, paintTitle)
        y += 30f
        canvas.drawText("Periodo: $fechaInicio - $fechaFin", 40f, y, paintSubTitle)
        y += 40f

        // Estadísticas
        val statsList = listOf(
            "Total cobrado: $${"%.2f".format(stats.totalCobrado)}",
            "Total recibido: $${"%.2f".format(stats.totalRecibido)}",
            "Total vuelto: $${"%.2f".format(stats.totalVuelto)}",
            "Cobros realizados: ${stats.numeroCobros}",
            "Promedio por cobro: $${"%.2f".format(stats.PromedioPorCobro)}"
        )


        canvas.drawText("Resumen", 40f, y, paintTitle)
        y += 30f
        statsList.forEach {
            canvas.drawText(it, 40f, y, paintText)
            y += 20f
        }

        y += 20f
        // Métricas
        canvas.drawText("Métricas", 40f, y, paintTitle)
        y += 30f
        val metricasList = listOf(
            "Día de mayor recaudación: ${metricas.diaMayor} ($${"%.2f".format(metricas.montoMayor)})",
            "Día de menor recaudación: ${metricas.diaMenor} ($${"%.2f".format(metricas.montoMenor)})",
            "Comerciante que más pagó: ${metricas.comercianteTop} ($${"%.2f".format(metricas.montoTop)})"
        )
        metricasList.forEach {
            canvas.drawText(it, 40f, y, paintText)
            y += 20f
        }

        y+= 30f
        // Tabla Resumen
        canvas.drawText("Resumen de Cobros", 40f, y, paintTitle)
        y += 20f
        val resumenRows = listaResumen.map { listOf(it.fecha, it.comerciante, it.puesto, "$${"%.2f".format(it.monto)}") }
        val resumenColumns = listOf(120f, 150f, 100f, 100f)
        drawTable(canvas, 40f, y, listOf("Fecha", "Comerciante", "# Puesto", "Monto"), resumenRows, resumenColumns)

        pdf.finishPage(page)

        // --- SEGUNDA PÁGINA: TOP COMERCIANTES + COBROS POR PUESTO ---
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
        page = pdf.startPage(pageInfo)
        canvas = page.canvas
        y = 50f

        // Top Comerciantes
        canvas.drawText("Top Comerciantes", 40f, y, paintTitle)
        y += 20f
        val topRows = listaComerciantes.map { listOf(it.nombre, it.totalCobros.toString(), "$${"%.2f".format(it.totalPagado)}") }
        val topColumns = listOf(200f, 100f, 100f)
        y = drawTable(canvas, 40f, y, listOf("Nombre", "Total Cobros", "Total Pagado"), topRows, topColumns)

        y += 50f
        // Cobros por Puesto
        canvas.drawText("Cobros por Puesto", 40f, y, paintTitle)
        y += 20f
        val puestosRows = listaPuestos.map { listOf(it.numeroPuesto, it.totalCobros.toString(), "$${"%.2f".format(it.totalMonto)}") }
        val puestosColumns = listOf(150f, 100f, 100f)
        drawTable(canvas, 40f, y, listOf("# Puesto", "Total Cobros", "Total Monto"), puestosRows, puestosColumns)

        pdf.finishPage(page)

        // --- GUARDAR PDF ---
        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ReportesCobros")
        if (!folder.exists()) folder.mkdirs()

        val file = File(folder, "ReporteCobros_${System.currentTimeMillis()}.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_LONG).show()
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}