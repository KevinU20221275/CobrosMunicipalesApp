package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.data.CobrosDBHelper

class FormularioComercianteActivity : AppCompatActivity() {
    private lateinit var db: CobrosDBHelper
    private lateinit var inputNombre: EditText
    private lateinit var inputTelefono: EditText
    private lateinit var btnGuardar: Button
    private lateinit var titulo : TextView

    private var idComerciante: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_comerciante)

        db = CobrosDBHelper(this)
        idComerciante = intent.getIntExtra("ID_COMERCIANTE", 0)

        titulo = findViewById(R.id.tituloPantalla)
        inputNombre = findViewById(R.id.inputNombreComerciante)
        inputTelefono = findViewById(R.id.inputTelefonoComerciante)
        btnGuardar = findViewById(R.id.btnGuardarComerciante)

        if (idComerciante > 0){
            titulo.setText("Actualizar Comerciante")
            cargarData()
        }

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

            val ok = db.guardarComerciante(idComerciante, nombre, telefono.toString())

            if (ok) {
                var message = if (idComerciante > 0) "Comerciante Actualizado" else "Comerciante Guardado"

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)

                finish()
            } else {
                Toast.makeText(this,"Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarData(){
        var comerciante = db.obtenerComerciante(idComerciante)

        inputNombre.setText(comerciante.nombre)
        inputTelefono.setText(comerciante.telefono)
    }
}
