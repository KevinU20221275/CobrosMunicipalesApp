package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.CobrosAdapter
import net.irivas.cobrosapp.data.CobroDTO
import net.irivas.cobrosapp.data.CobrosDBHelper

class HistorialCobrosActivity : AppCompatActivity() {
    private lateinit var recyclerPagos: RecyclerView
    private lateinit var adapter: CobrosAdapter
    private lateinit var inputSearch: EditText
    private lateinit var txtTotal: TextView
    private lateinit var db: CobrosDBHelper
    private lateinit var btnAgregar: FloatingActionButton

    private val listaOriginal = mutableListOf<CobroDTO>()
    private val listaFiltrada = mutableListOf<CobroDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_cobros)

        db = CobrosDBHelper(this)
        recyclerPagos = findViewById(R.id.recyclerPagos)
        inputSearch = findViewById(R.id.inputSearch)
        txtTotal = findViewById(R.id.txtTotalValue)
        btnAgregar = findViewById(R.id.btnAgregarCobro)

        cargarCobros()

        adapter = CobrosAdapter(listaFiltrada,
            onEdit = { cobro ->
                val intent = Intent(this, FormularioCobroActivity::class.java)
                intent.putExtra("ID_COBRO", cobro.idCobro)
                startActivity(intent)
            },
            onDelete = { cobro ->
                confirmarEliminacion(cobro.idCobro)
            }
            ,db,)
        recyclerPagos.layoutManager = LinearLayoutManager(this)
        recyclerPagos.adapter = adapter

        calcularTotal()

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, FormularioCobroActivity::class.java))
        }

        //configurarBusqueda()
    }

    override fun onResume() {
        super.onResume()
        listaOriginal.clear()
        listaOriginal.addAll(db.obtenerCobrosConInfo())

        adapter.actualizarLista(listaOriginal)
        calcularTotal()
    }

    private fun cargarCobros(){
        // limpia las listas al iniciar
        listaOriginal.clear()
        listaFiltrada.clear()

        // carga los pagos desde la db
        listaOriginal.addAll(db.obtenerCobrosConInfo())

        // copia la lista original a la lista que usaremos para filtrar
        listaFiltrada.addAll(listaOriginal)
    }

    /* private fun configurarBusqueda() {
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(texto: CharSequence?, start: Int, before: Int, count: Int) {

                val query = texto.toString().lowercase()

                val nuevaLista = listaOriginal.filter {
                    it.nombre.lowercase().contains(query) ||
                            it.numeroPuesto.lowercase().contains(query)
                }
                listaFiltrada.clear()
                listaFiltrada.addAll(nuevaLista)
                adapter.notifyDataSetChanged()

                calcularTotal()
            }
        })
    }*/

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
                onResume()

                Toast.makeText(this, "Cobro eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

}