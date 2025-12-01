package net.irivas.cobrosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import net.irivas.cobrosapp.ui.LoginActivity
import net.irivas.cobrosapp.ui.PanelAdministracionActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnIngresar).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        if (isLogin()) {
            val intent = Intent(this, PanelAdministracionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun isLogin(): Boolean {
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        return prefs.getBoolean("logueado", false)
    }
}