package net.irivas.cobrosapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.data.CobrosDBHelper

class RegistrarComercianteActivity : AppCompatActivity() {
    private lateinit var db: CobrosDBHelper
    private lateinit var inputNombre: EditText
    private lateinit var inputTelefono: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_comerciante)

        db = CobrosDBHelper(this)
        inputNombre = findViewById(R.id.inputNombreComerciante)
        inputTelefono = findViewById(R.id.inputTelefonoComerciante)
        btnGuardar = findViewById(R.id.btnGuardarComerciante)

        btnGuardar.setOnClickListener {
            val nombre = inputNombre.text.toString().trim()
            val telefono = inputTelefono.text

            if (nombre.isEmpty()) {
                inputNombre.error = "Ingrese un nombre"
                return@setOnClickListener
            }

            if (telefono.isEmpty() || telefono.length != 8 || !telefono.isDigitsOnly()){
                inputTelefono.error = "Ingrese un numero de telefono valido"
                return@setOnClickListener
            }

            val ok = db.insertarComerciante(nombre, telefono.toString())

            Toast.makeText(this,
                if (ok) "Comerciante guardado" else "Error al guardar",
                Toast.LENGTH_SHORT
            ).show()

            if (ok) inputNombre.text.clear()
        }
    }
}
