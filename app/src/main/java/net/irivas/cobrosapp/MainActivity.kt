package net.irivas.cobrosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import net.irivas.cobrosapp.ui.HistorialCobrosActivity
import net.irivas.cobrosapp.ui.ListarComerciantesActivity
import net.irivas.cobrosapp.ui.ListarPuestosActivity
import net.irivas.cobrosapp.ui.FormularioCobroActivity
import net.irivas.cobrosapp.ui.PanelAdministracionActivity
import net.irivas.cobrosapp.ui.ReportesActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isLogin()){
            startActivity(Intent(this, PanelAdministracionActivity::class.java))
            finish()
            return
        }

        findViewById<Button>(R.id.btnIngresar).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun isLogin(): Boolean {
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        return prefs.getBoolean("logueado", false)
    }
}