package net.irivas.cobrosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import net.irivas.cobrosapp.ui.HistorialActivity
import net.irivas.cobrosapp.ui.RegistrarCobroActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnRegistrarCobro).setOnClickListener {
            startActivity(Intent(this, RegistrarCobroActivity::class.java))
        }

        findViewById<Button>(R.id.btnHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }
    }
}