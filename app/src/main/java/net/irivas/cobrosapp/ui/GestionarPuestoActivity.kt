package net.irivas.cobrosapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.PuestoSeleccionAdapter
import net.irivas.cobrosapp.data.CobrosDBHelper

class AsignarPuestoActivity : AppCompatActivity() {

    private lateinit var db: CobrosDBHelper
    private lateinit var adapter: PuestoSeleccionAdapter
    private var idComerciante: Int = 0
    private lateinit var nombreComerciante: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asignar_puesto)

        db = CobrosDBHelper(this)

        idComerciante = intent.getIntExtra("idComerciante", 0)
        nombreComerciante = intent.getStringExtra("nombreComerciante") ?: ""

        val txtNombre = findViewById<TextView>(R.id.txtNombreComerciante)
        val recycler = findViewById<RecyclerView>(R.id.recyclerPuestos)
        val btnGuardar = findViewById<Button>(R.id.btnAsignarPuestos)

        txtNombre.text = nombreComerciante

        val puestosDisponibles = db.obtenerPuestosDisponibles()

        adapter = PuestoSeleccionAdapter(puestosDisponibles)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnGuardar.setOnClickListener {
            val seleccionados = adapter.obtenerSeleccionados()

            if (seleccionados.isEmpty()) {
                Toast.makeText(this, "Seleccione al menos un puesto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultado = db.asignarPuestosAComerciante(idComerciante, seleccionados)

            if (resultado) {
                Toast.makeText(this, "Puestos asignados correctamente", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Error al asignar puestos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}