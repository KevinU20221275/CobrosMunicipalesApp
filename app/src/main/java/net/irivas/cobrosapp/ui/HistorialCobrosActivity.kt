package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.CobrosAdapter
import net.irivas.cobrosapp.data.CobroDTO
import net.irivas.cobrosapp.data.CobrosDBHelper
import java.time.LocalDate

// este comentario sera mi commit antes del refactor :)

class HistorialCobrosActivity : AppCompatActivity() {
    private lateinit var recyclerPagos: RecyclerView
    private lateinit var adapter: CobrosAdapter
    private lateinit var inputSearch: EditText
    private lateinit var txtTotal: TextView
    private lateinit var db: CobrosDBHelper
    private lateinit var btnAgregar: FloatingActionButton

    private lateinit var chipHoy : TextView
    private lateinit var chipAyer : TextView
    private lateinit var chipSemana : TextView

    private val listaOriginal = mutableListOf<CobroDTO>()
    private val listaFiltrada = mutableListOf<CobroDTO>()

    private enum class Filter { TODOS, HOY, AYER, SEMANA }
    private var filtroActual = Filter.HOY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_cobros)

        db = CobrosDBHelper(this)
        recyclerPagos = findViewById(R.id.recyclerPagos)
        inputSearch = findViewById(R.id.inputSearch)
        txtTotal = findViewById(R.id.txtTotalValue)
        btnAgregar = findViewById(R.id.btnAgregarCobro)
        chipHoy = findViewById(R.id.chipHoy)
        chipAyer = findViewById(R.id.chipAyer)
        chipSemana = findViewById(R.id.chipSemana)

        adapter = CobrosAdapter(
            mutableListOf(),
            onEdit = { cobro ->
                val intent = Intent(this, FormularioCobroActivity::class.java)
                intent.putExtra("ID_COBRO", cobro.idCobro)
                startActivity(intent)
            },
            onDelete = { cobro ->
                confirmarEliminacion(cobro.idCobro)
            },
            db,
        )
        recyclerPagos.layoutManager = LinearLayoutManager(this)
        recyclerPagos.adapter = adapter


        btnAgregar.setOnClickListener {
            startActivity(Intent(this, FormularioCobroActivity::class.java))
        }

        cargarCobros()

        // Por defecto
        activarChip(chipHoy, chipAyer, chipSemana)
        filtrarHoy()

        chipHoy.setOnClickListener {
            filtroActual = Filter.HOY
            activarChip(chipHoy, chipAyer, chipSemana)
            filtrarHoy()
        }

        chipAyer.setOnClickListener {
            filtroActual = Filter.AYER
            activarChip(chipAyer, chipHoy, chipSemana)
            filtrarAyer()
        }

        chipSemana.setOnClickListener {
            filtroActual = Filter.SEMANA
            activarChip(chipSemana, chipHoy, chipAyer)
            filtrarSemana()
        }

        calcularTotal()

    }

    override fun onResume() {
        super.onResume()
        when (filtroActual) {
            Filter.HOY -> filtrarHoy()
            Filter.AYER -> filtrarAyer()
            Filter.SEMANA -> filtrarSemana()
            else -> {}
        }
    }

    private fun cargarCobros() {
        // limpia las listas al iniciar
        listaOriginal.clear()
        listaFiltrada.clear()

        // carga los pagos desde la db
        listaOriginal.addAll(db.obtenerCobrosConInfo())

        // copia la lista original a la lista que usaremos para filtrar
        listaFiltrada.addAll(listaOriginal)
    }

    private fun filtrarHoy() {
        val hoy = LocalDate.now().toString()
        val datos = db.obtenerCobrosConInfoPorFecha(hoy)

        listaFiltrada.clear()
        listaFiltrada.addAll(datos)
        adapter.actualizarLista(listaFiltrada)
        calcularTotal()
    }

    private fun filtrarAyer() {
        val ayer = LocalDate.now().minusDays(1).toString()
        aplicarFiltro { db.obtenerCobrosConInfoPorFecha(ayer) }
    }

    private fun filtrarSemana() {
        val inicio = LocalDate.now().minusDays(6).toString()
        val fin = LocalDate.now().toString()
        aplicarFiltro { db.obtenerCobrosConInfoPorFecha(inicio, fin) }
    }

    private fun aplicarFiltro(obtenerDatos: () -> List<CobroDTO>) {
        val nuevaLista = obtenerDatos()

        listaFiltrada.clear()
        listaFiltrada.addAll(nuevaLista)

        adapter.actualizarLista(listaFiltrada)
        calcularTotal()
    }


    private fun calcularTotal() {
        val total = listaFiltrada.sumOf { it.monto }
        txtTotal.text = "$%.2f".format(total)
    }

    private fun confirmarEliminacion(idCobro: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Deseas eliminar este cobro? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                db.eliminarCobro(idCobro)

                // actualiza la lista despues de eliminar
                when (filtroActual) {
                    Filter.HOY -> filtrarHoy()
                    Filter.AYER -> filtrarAyer()
                    Filter.SEMANA -> filtrarSemana()
                    else -> cargarCobros()
                }


                Toast.makeText(this, "Cobro eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun activarChip(chipActivo: TextView, vararg chipsInactivos: TextView) {
        chipActivo.animate().scaleX(0.95f).scaleY(0.95f).setDuration(60).withEndAction {
            chipActivo.animate().scaleX(1f).scaleY(1f).duration = 60
        }
        chipActivo.setBackgroundResource(R.drawable.chip_active)
        chipActivo.setTextColor(ContextCompat.getColor(this, R.color.primary))

        chipsInactivos.forEach { chip ->
            chip.setBackgroundResource(R.drawable.chip_inactive)
            chip.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        }
    }
}