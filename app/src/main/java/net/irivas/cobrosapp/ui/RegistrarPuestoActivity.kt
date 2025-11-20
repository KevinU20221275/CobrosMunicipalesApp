package net.irivas.cobrosapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.data.CobrosDBHelper

class RegistrarPuestoActivity : AppCompatActivity() {

    private lateinit var db: CobrosDBHelper
    private lateinit var inputNumero: EditText
    private lateinit var inputTarifa: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_puesto)

        db = CobrosDBHelper(this)
        inputNumero = findViewById(R.id.inputNumeroPuesto)
        inputTarifa = findViewById(R.id.inputTarifa)
        btnGuardar = findViewById(R.id.btnGuardarPuesto)

        btnGuardar.setOnClickListener { guardarPuesto() }
    }

    private fun guardarPuesto() {
        val numeroStr = inputNumero.text.toString().trim()
        val tarifaStr = inputTarifa.text.toString().trim()

        if (numeroStr.isEmpty() || numeroStr.toIntOrNull() == null) {
            inputNumero.error = "Número inválido"
            return
        }

        if (tarifaStr.isEmpty() || tarifaStr.toDoubleOrNull() == null || tarifaStr.toDouble() <= 0) {
            inputTarifa.error = "Tarifa inválida"
            return
        }

        val numero = numeroStr.toInt()
        val tarifa = tarifaStr.toDouble()

        val resultado = db.insertarPuesto(numero, tarifa)

        if (resultado) {
            Toast.makeText(this, "Puesto registrado", Toast.LENGTH_SHORT).show()
            inputNumero.text.clear()
            inputTarifa.text.clear()
        } else {
            Toast.makeText(this, "Error al guardar (¿duplicado?)", Toast.LENGTH_SHORT).show()
        }
    }
}
