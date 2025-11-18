package net.irivas.cobrosapp.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.data.Pago
import net.irivas.cobrosapp.data.PagosDBHelper
import android.Manifest
import android.app.DatePickerDialog
import android.media.Image
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import java.util.Calendar

class RegistrarCobroActivity : AppCompatActivity() {
    private lateinit var db : PagosDBHelper

    private lateinit var inputNombre: EditText
    private lateinit var inputPuesto: EditText
    private lateinit var inputMonto: EditText
    private lateinit var inputRecibido: EditText
    private lateinit var inputVuelto: EditText
    private lateinit var inputFecha: EditText
    private lateinit var imgCalendar: ImageView
    private lateinit var btnGuardar: Button
    private lateinit var txtLatitud: TextView
    private lateinit var txtLongitud: TextView

    private lateinit var fusedLocation: FusedLocationProviderClient
    private var intentosGPS = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cobro)

        db = PagosDBHelper(this)

        inputNombre = findViewById(R.id.inputNombre)
        inputPuesto = findViewById(R.id.inputPuesto)
        inputMonto = findViewById(R.id.inputMonto)
        inputRecibido = findViewById(R.id.inputRecibido)
        inputVuelto = findViewById(R.id.inputVuelto)
        inputFecha = findViewById(R.id.inputFecha)
        imgCalendar = findViewById(R.id.imgCalendar)
        btnGuardar = findViewById(R.id.btnGuardar)
        txtLatitud = findViewById(R.id.txtLatitud)
        txtLongitud = findViewById(R.id.txtLongitud)


        inputRecibido.addTextChangedListener {
            val monto = inputMonto.text.toString().toDoubleOrNull() ?: 0.0
            val recibido = inputRecibido.text.toString().toDoubleOrNull() ?: 0.0
            val vuelto = recibido - monto

            inputVuelto.setText(String.format("%.2f", vuelto))
        }

        inputFecha.setOnClickListener { mostrarCalendario() }
        imgCalendar.setOnClickListener { mostrarCalendario() }

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        verificarPermisos()

        obtenerUbicacion()

        // BOTÓN GUARDAR
        btnGuardar.setOnClickListener {
            if (validarDatos() && gpsListo()) {
                insertarPago()
            }
        }
    }

    private fun obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1500
        ).setMaxUpdates(1).build()

        fusedLocation.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (location != null) {
                        txtLatitud.text = "Latitud: ${location.latitude}"
                        txtLongitud.text = "Longitud: ${location.longitude}"
                    } else {
                        // si no encuentra la ubicacion reintenta
                        intentosGPS++

                        if (intentosGPS <= 3){
                            obtenerUbicacion()
                        } else {
                            Toast.makeText(this@RegistrarCobroActivity, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacion()
        }
    }

    private fun validarDatos(): Boolean {

        val nombre = inputNombre.text.toString().trim()
        val puestoStr = inputPuesto.text.toString().trim()
        val montoStr = inputMonto.text.toString().trim()
        val recibidoStr = inputRecibido.text.toString().trim()
        val fecha = inputFecha.text.toString().trim()

        if (nombre.isEmpty()) {
            inputNombre.error = "Ingrese el nombre"
            return false
        }

        if (puestoStr.isEmpty() || puestoStr.toIntOrNull() == null || puestoStr.toInt() <= 0) {
            inputPuesto.error = "Puesto inválido"
            return false
        }

        if (montoStr.isEmpty() || montoStr.toDoubleOrNull() == null || montoStr.toDouble() <= 0) {
            inputMonto.error = "Monto inválido"
            return false
        }

        if (recibidoStr.isEmpty() || recibidoStr.toDoubleOrNull() == null) {
            inputRecibido.error = "Ingrese recibido"
            return false
        }

        val monto = montoStr.toDouble()
        val recibido = recibidoStr.toDouble()

        if (recibido < monto) {
            inputRecibido.error = "El recibido no puede ser menor al monto"
            return false
        }

        if (fecha.isEmpty()) {
            inputFecha.error = "Ingrese fecha"
            return false
        }

        return true
    }

    private fun gpsListo(): Boolean {
        val lat = txtLatitud.text.toString().trim()
        val lon = txtLongitud.text.toString().trim()

        if (lat.endsWith(": -") || lon.endsWith(": -")) {
            Toast.makeText(this, "Esperando GPS... Por favor active el GPS", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    private fun insertarPago() {

        val nombre = inputNombre.text.toString().trim()
        val puesto = inputPuesto.text.toString().toInt()
        val monto = inputMonto.text.toString().toDouble()
        val recibido = inputRecibido.text.toString().toDouble()
        val vuelto = recibido - monto
        val fecha = inputFecha.text.toString().trim()
        val lat = txtLatitud.text.toString().replace("Latitud: ", "").toDouble()
        val lon = txtLongitud.text.toString().replace("Longitud: ", "").toDouble()

        val pago = Pago(
            id = 0,
            nombre = nombre,
            numeroPuesto = puesto.toString(),
            monto = monto,
            recibido = recibido,
            vuelto = vuelto,
            fecha = fecha,
            latitud = lat,
            longitud = lon
        )

        val resultado = db.insertarPago(pago)

        if (resultado) {
            Toast.makeText(this, "Cobro registrado", Toast.LENGTH_SHORT).show()
            limpiarCampos()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        inputNombre.text.clear()
        inputPuesto.text.clear()
        inputMonto.text.clear()
        inputRecibido.text.clear()
        inputFecha.text.clear()
    }

    private fun verificarPermisos() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        } else {
            obtenerUbicacion()
        }
    }

    private fun mostrarCalendario() {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            inputFecha.setText("$d/${m + 1}/$y")
        }, year, month, day)

        datePicker.show()
    }
}