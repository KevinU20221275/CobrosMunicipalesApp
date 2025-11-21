package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.data.CobrosDBHelper

class FormularioPuestoActivity : AppCompatActivity() {

    private lateinit var db: CobrosDBHelper
    private lateinit var inputNumero: EditText
    private lateinit var inputTarifa: EditText
    private lateinit var btnGuardar: Button
    private lateinit var titulo : TextView

    private var idPuesto : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_puesto)

        db = CobrosDBHelper(this)
        idPuesto = intent.getIntExtra("ID_PUESTO", 0)

        titulo = findViewById(R.id.tituloPantalla)
        inputNumero = findViewById(R.id.inputNumeroPuesto)
        inputTarifa = findViewById(R.id.inputTarifa)
        btnGuardar = findViewById(R.id.btnGuardarPuesto)

        if (idPuesto > 0){
            titulo.setText("Actualizar Puesto")
            cargarData()
        }

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

        val resultado = db.guardarPuesto(idPuesto, numero, tarifa)

        if (resultado) {
            var message = if (idPuesto > 0) "Puesto Actualizado" else "Puesto Guardado"

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            val returnIntent = Intent()
            setResult(RESULT_OK, returnIntent)

            finish()
        } else {
            Toast.makeText(this, "Error al guardar (¿duplicado?)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarData(){
        var puesto = db.obtenerPuesto(idPuesto)

        inputNumero.setText(puesto?.numero)
        inputTarifa.setText(puesto?.tarifa.toString())
    }
}
