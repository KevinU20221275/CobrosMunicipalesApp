package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.ComercianteAdapter
import net.irivas.cobrosapp.data.CobrosDBHelper
import net.irivas.cobrosapp.data.Comerciante
import androidx.core.widget.addTextChangedListener

class ListarComerciantesActivity : AppCompatActivity() {

    private lateinit var db: CobrosDBHelper
    private lateinit var recycler: RecyclerView
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var inputSearch: EditText
    private lateinit var adapter: ComercianteAdapter
    private val listaOriginal = mutableListOf<Comerciante>()
    private val listaMostrada = mutableListOf<Comerciante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_comerciantes)

        db = CobrosDBHelper(this)
        recycler = findViewById(R.id.recyclerComerciantes)
        btnAgregar = findViewById(R.id.btnAgregarComerciante)
        inputSearch = findViewById(R.id.inputSearch)

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ComercianteAdapter(
            listaMostrada,
            onGestionarPuestos = { comerciante ->
                val intent = Intent(this, GestionarPuestoActivity::class.java)
                intent.putExtra("ID_COMERCIANTE", comerciante.idComerciante)
                startActivity(intent)
            },
            onEdit = { comerciante ->
                val intent = Intent(this, FormularioComercianteActivity::class.java)
                intent.putExtra("ID_COMERCIANTE", comerciante.idComerciante)
                startActivity(intent)
            }
        )
        recycler.adapter = adapter

        cargarLista()
        configurarBuscador()

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, FormularioComercianteActivity::class.java))
        }
    }

    private fun cargarLista() {
        listaOriginal.clear()
        listaOriginal.addAll(db.obtenerComerciantes())

        listaMostrada.clear()
        listaMostrada.addAll(listaOriginal)

        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        listaOriginal.clear()
        listaOriginal.addAll(db.obtenerComerciantes())

        val texto = inputSearch.text.toString().trim().lowercase()
        val filtrado = listaOriginal.filter { it.nombre.lowercase().contains(texto) }

        adapter.actualizarLista(filtrado)
    }

    private fun configurarBuscador() {
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s?.toString()?.trim() ?: ""
                if (texto.isEmpty()) {
                    // Restaurar lista completa
                    adapter.actualizarLista(listaOriginal)
                } else {
                    val filtrado = listaOriginal.filter { com ->
                        com.nombre.contains(texto, ignoreCase = true)
                    }
                    adapter.actualizarLista(filtrado)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}

