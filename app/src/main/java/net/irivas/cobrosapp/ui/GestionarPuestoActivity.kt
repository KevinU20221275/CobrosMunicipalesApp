package net.irivas.cobrosapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.PuestoAdapter
import net.irivas.cobrosapp.data.CobrosDBHelper
import net.irivas.cobrosapp.data.Puesto

class GestionarPuestoActivity : AppCompatActivity() {

    private lateinit var db: CobrosDBHelper
    private lateinit var adapterDisponibles: PuestoAdapter
    private lateinit var adapterAsignados: PuestoAdapter

    private lateinit var listaDisponibles: MutableList<Puesto>
    private lateinit var listaAsignados: MutableList<Puesto>

    private var idComerciante: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_puestos)

        db = CobrosDBHelper(this)
        idComerciante = intent.getIntExtra("ID_COMERCIANTE", 0)

        cargarListas()
        configurarRecyclerViews()
        configurarBotones()
    }

    private fun cargarListas() {
        listaDisponibles = db.obtenerPuestosDisponibles().toMutableList()
        listaAsignados = db.obtenerPuestosPorComerciante(idComerciante).toMutableList()
    }

    private fun configurarRecyclerViews() {
        val recyclerDisp = findViewById<RecyclerView>(R.id.recyclerDisponibles)
        val recyclerAsig = findViewById<RecyclerView>(R.id.recyclerAsignados)

        adapterDisponibles = PuestoAdapter(listaDisponibles) {}

        adapterAsignados = PuestoAdapter(listaAsignados) {}

        recyclerDisp.layoutManager = LinearLayoutManager(this)
        recyclerDisp.adapter = adapterDisponibles

        recyclerAsig.layoutManager = LinearLayoutManager(this)
        recyclerAsig.adapter = adapterAsignados
    }

    private fun configurarBotones() {
        val btnAsignar = findViewById<Button>(R.id.btnAsignar)
        val btnQuitar = findViewById<Button>(R.id.btnQuitar)

        btnAsignar.setOnClickListener {
            val seleccionados = adapterDisponibles.obtenerSeleccionados()
            if (seleccionados.isEmpty()) {
                Toast.makeText(
                    this,
                    "Seleccione al menos un puesto para asignar",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            for (idP in seleccionados) {
                db.asignarPuestosAComerciante(idComerciante, listOf(idP))
                val puesto = listaDisponibles.find { it.id == idP }!!
                listaDisponibles.remove(puesto)
                listaAsignados.add(puesto)
            }

            adapterDisponibles.limpiarSeleccion()
            adapterDisponibles.notifyDataSetChanged()
            adapterAsignados.notifyDataSetChanged()
            Toast.makeText(this, "Puestos asignados correctamente", Toast.LENGTH_SHORT).show()
        }

        btnQuitar.setOnClickListener {
            val seleccionados = adapterAsignados.obtenerSeleccionados()
            if (seleccionados.isEmpty()) {
                Toast.makeText(this, "Seleccione puestos a liberar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            for (idP in seleccionados) {
                db.liberarPuesto(idP)
                val puesto = listaAsignados.find { it.id == idP }!!
                listaAsignados.remove(puesto)
                listaDisponibles.add(puesto)
            }

            adapterAsignados.limpiarSeleccion()
            adapterAsignados.notifyDataSetChanged()
            adapterDisponibles.notifyDataSetChanged()
            Toast.makeText(this, "Puestos liberados correctamente", Toast.LENGTH_SHORT).show()
        }
    }
}
