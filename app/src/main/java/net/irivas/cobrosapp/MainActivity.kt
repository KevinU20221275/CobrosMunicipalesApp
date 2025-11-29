package net.irivas.cobrosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import net.irivas.cobrosapp.ui.HistorialCobrosActivity
import net.irivas.cobrosapp.ui.ListarComerciantesActivity
import net.irivas.cobrosapp.ui.ListarPuestosActivity
import net.irivas.cobrosapp.ui.FormularioCobroActivity
import net.irivas.cobrosapp.ui.ReportesActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnListarComerciantes).setOnClickListener {
            startActivity(Intent(this, ListarComerciantesActivity::class.java))
        }

        findViewById<Button>(R.id.btnRegistrarPuesto).setOnClickListener {
            startActivity(Intent(this, ListarPuestosActivity::class.java))
        }

        findViewById<Button>(R.id.btnGenerarReporte).setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        findViewById<Button>(R.id.btnHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialCobrosActivity::class.java))
        }
    }
}