package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.adapters.PanelAdministracionAdapter
import net.irivas.cobrosapp.data.PanelItem

class PanelAdministracionActivity : AppCompatActivity() {
    private lateinit var recyclerDashboard: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_administracion)

        val toolbar: androidx.appcompat.widget.Toolbar? = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerDashboard = findViewById(R.id.recyclerDashboard)

        val items = listOf(
            PanelItem(R.drawable.ic_store, "Puestos", "Administrar puestos"),
            PanelItem(R.drawable.ic_group, "Comerciantes", "Ver y gestionar comerciantes"),
            PanelItem(R.drawable.ic_receipt, "Cobros", "Controlar cobros registrados"),
            PanelItem(R.drawable.ic_report, "Generar Reporte", "Crear reportes detallados")
        )

        recyclerDashboard.layoutManager = GridLayoutManager(this, 2)
        recyclerDashboard.adapter = PanelAdministracionAdapter(items) { item ->
            when (item.titulo) {
                "Puestos" -> startActivity(Intent(this, ListarPuestosActivity::class.java))
                "Comerciantes" -> startActivity(Intent(this, ListarComerciantesActivity::class.java))
                "Cobros" -> startActivity(Intent(this, HistorialCobrosActivity::class.java))
                "Generar Reporte" -> startActivity(Intent(this, ReportesActivity::class.java))
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_panel_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                //cerrarSesion()
                Log.d("LOGOUT", "Cerrando secion....")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

   /* private fun cerrarSesion() {
        // Limpiar datos guardados si usan SharedPreferences
        getSharedPreferences("appPrefs", MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Mandar al login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    } */
}