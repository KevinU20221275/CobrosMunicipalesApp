package net.irivas.cobrosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import net.irivas.cobrosapp.ui.HistorialActivity
import net.irivas.cobrosapp.ui.ListarComerciantesActivity
import net.irivas.cobrosapp.ui.RegistrarCobroActivity
import net.irivas.cobrosapp.ui.RegistrarPuestoActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnListarComerciantes).setOnClickListener {
            startActivity(Intent(this, ListarComerciantesActivity::class.java))
        }

        findViewById<Button>(R.id.btnRegistrarPuesto).setOnClickListener {
            startActivity(Intent(this, RegistrarPuestoActivity::class.java))
        }

        findViewById<Button>(R.id.btnRegistrarCobro).setOnClickListener {
            startActivity(Intent(this, RegistrarCobroActivity::class.java))
        }

        findViewById<Button>(R.id.btnHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }
    }
}