package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.PuestoListAdapter
import net.irivas.cobrosapp.data.CobrosDBHelper
import net.irivas.cobrosapp.data.Puesto

class ListarPuestosActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var chipTodos: TextView
    private lateinit var chipDisponibles: TextView
    private lateinit var chipOcupados: TextView

    private val listaPuestos = mutableListOf<Puesto>()
    private lateinit var adapter: PuestoListAdapter
    private lateinit var db: CobrosDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_puestos)

        recycler = findViewById(R.id.recyclerPuestos)
        btnAgregar = findViewById(R.id.btnAgregarPuesto)
        chipTodos = findViewById(R.id.chipTodos)
        chipDisponibles = findViewById(R.id.chipDisponobles)
        chipOcupados = findViewById(R.id.chipOcupados)
        db = CobrosDBHelper(this)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = PuestoListAdapter(listaPuestos,
            onEdit = { puesto ->
                val intent = Intent(this, FormularioPuestoActivity::class.java)
                intent.putExtra("ID_PUESTO", puesto.id)
                startActivity(intent)
            })

        recycler.adapter = adapter

        listaPuestos.clear()
        listaPuestos.addAll(db.obtenerPuestos())
        adapter.notifyDataSetChanged()

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, FormularioPuestoActivity::class.java))
        }

        // Chips
        chipTodos.setOnClickListener {
            activarChip(chipTodos, chipDisponibles, chipOcupados)
            filtrarPuestos(null)
        }
        chipDisponibles.setOnClickListener {
            activarChip(chipDisponibles, chipTodos, chipOcupados)
            filtrarPuestos(1)
        }
        chipOcupados.setOnClickListener {
            activarChip(chipOcupados, chipTodos, chipDisponibles)
            filtrarPuestos(0)
        }
    }

    private fun filtrarPuestos(disponible: Int?) {
        val filtrada = if (disponible == null) {
            listaPuestos
        } else {
            listaPuestos.filter { it.disponible == disponible }
        }
        adapter.actualizarLista(filtrada)
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

    override fun onResume() {
        super.onResume()
        listaPuestos.clear()
        listaPuestos.addAll(db.obtenerPuestos())
        adapter.actualizarLista(listaPuestos)
    }
}