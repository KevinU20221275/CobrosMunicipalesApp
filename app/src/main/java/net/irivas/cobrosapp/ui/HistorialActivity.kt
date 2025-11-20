package net.irivas.cobrosapp.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.CobrosAdapter
import net.irivas.cobrosapp.data.CobroDTO
import net.irivas.cobrosapp.data.CobrosDBHelper

class HistorialActivity : AppCompatActivity() {
    private lateinit var recyclerPagos: RecyclerView
    private lateinit var adapter: CobrosAdapter
    private lateinit var inputSearch: EditText
    private lateinit var txtTotal: TextView
    private lateinit var db: CobrosDBHelper


    private val listaOriginal = mutableListOf<CobroDTO>()
    private val listaFiltrada = mutableListOf<CobroDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        db = CobrosDBHelper(this)
        recyclerPagos = findViewById(R.id.recyclerPagos)
        inputSearch = findViewById(R.id.inputSearch)
        txtTotal = findViewById(R.id.txtTotalValue)

        // limpia las listas al iniciar
        listaOriginal.clear()
        listaFiltrada.clear()

        // carga los pagos desde la db
        listaOriginal.addAll(db.obtenerCobrosConInfo())

        // copia la lista original a la lista que usaremos para filtrar
        listaFiltrada.addAll(listaOriginal)

        adapter = CobrosAdapter(listaFiltrada, db)
        recyclerPagos.layoutManager = LinearLayoutManager(this)
        recyclerPagos.adapter = adapter

        calcularTotal()

        //configurarBusqueda()
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
}