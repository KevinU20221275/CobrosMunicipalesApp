package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.ComercianteAdapter
import net.irivas.cobrosapp.data.CobrosDBHelper

class ListarComerciantesActivity : AppCompatActivity() {

    private lateinit var db: CobrosDBHelper
    private lateinit var recycler: RecyclerView
    private lateinit var btnAgregar: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_comerciantes)

        db = CobrosDBHelper(this)

        recycler = findViewById(R.id.recyclerComerciantes)
        btnAgregar = findViewById(R.id.btnAgregarComerciante)

        cargarLista()

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, FormularioComercianteActivity::class.java))
        }
    }

    private fun cargarLista() {
        val lista = db.obtenerComerciantes()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ComercianteAdapter(lista,
            onGestionarPuestos = { comerciante ->
                val intent = Intent(this, GestionarPuestoActivity::class.java)
                intent.putExtra("ID_COMERCIANTE", comerciante.idComerciante)
                startActivity(intent)
            },
            onEdit = { comerciante ->
                val intent = Intent(this, FormularioComercianteActivity::class.java)
                intent.putExtra("ID_COMERCIANTE", comerciante.idComerciante)
                startActivity(intent)
            })
    }

    override fun onResume() {
        super.onResume()
        cargarLista()
    }
}

