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
import net.irivas.cobrosapp.data.Cobro
import net.irivas.cobrosapp.data.CobrosDBHelper
import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import net.irivas.cobrosapp.data.Comerciante
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class FormularioCobroActivity : AppCompatActivity() {
    private lateinit var db : CobrosDBHelper

    private lateinit var inputNombre: AutoCompleteTextView
    private lateinit var inputMonto: EditText
    private lateinit var inputRecibido: EditText
    private lateinit var inputVuelto: EditText
    private lateinit var inputFecha: EditText
    private lateinit var imgCalendar: ImageView
    private lateinit var btnGuardar: Button
    private lateinit var txtLatitud: TextView
    private lateinit var txtLongitud: TextView
    private lateinit var spinnerPuesto: Spinner
    private lateinit var comerciantes: List<Comerciante>
    private lateinit var titulo : TextView
    private var idComercianteSeleccionado: Int = -1
    private var idPuestoSeleccionado: Int = -1
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var intentosGPS = 0

    private var idCobro:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_cobro)

        db = CobrosDBHelper(this)

        inputNombre = findViewById(R.id.inputNombre)
        spinnerPuesto = findViewById(R.id.spinnerPuesto)
        inputMonto = findViewById(R.id.inputMonto)
        inputRecibido = findViewById(R.id.inputRecibido)
        inputVuelto = findViewById(R.id.inputVuelto)
        inputFecha = findViewById(R.id.inputFecha)
        imgCalendar = findViewById(R.id.imgCalendar)
        btnGuardar = findViewById(R.id.btnGuardar)
        txtLatitud = findViewById(R.id.txtLatitud)
        txtLongitud = findViewById(R.id.txtLongitud)
        titulo = findViewById(R.id.tituloPantalla)

        // carga la lista de comerciantes para usarla en AutoCompleteTextView
        cargarComerciantes()

        inputRecibido.addTextChangedListener {
            val monto = inputMonto.text.toString().toDoubleOrNull() ?: 0.0
            val recibido = inputRecibido.text.toString().toDoubleOrNull() ?: 0.0
            val vuelto = recibido - monto

            inputVuelto.setText(String.format("%.2f", vuelto))
        }

        inputMonto.addTextChangedListener {
            val recibido = inputRecibido.text.toString().toDoubleOrNull() ?: 0.0

            if (recibido > 0){
                val monto = inputMonto.text.toString().toDoubleOrNull() ?: 0.0
                val vuelto = recibido - monto

                inputVuelto.setText(String.format("%.2f", vuelto))
            }
        }

        inputFecha.setOnClickListener { mostrarCalendario() }
        imgCalendar.setOnClickListener { mostrarCalendario() }

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        verificarPermisos()

        obtenerUbicacion()

        idCobro = intent.getIntExtra("ID_COBRO", 0)

        if (idCobro > 0){
            val data = db.obtenerCobroParaEditar(idCobro)
            titulo.setText("Actualizar Cobro")

            if (data != null){
                val comerciante = comerciantes.firstOrNull{it.idComerciante == data.idComerciante}
                inputNombre.setText(comerciante?.nombre ?: "",false)
                inputNombre.isEnabled = false // No Editable

                inputRecibido.setText(data.recibido.toString())
                inputVuelto.setText(data.vuelto.toString())
                inputFecha.setText(data.fecha)
                idComercianteSeleccionado = data.idComerciante

                txtLatitud.text = "Latitud: ${data.latitud}"
                txtLongitud.text = "Longitud: ${data.longitud}"

                cargarPuestos(data.idComerciante, data.idPuesto)
            }
        }

        // BOTÓN GUARDAR
        btnGuardar.setOnClickListener {
            if (validarDatos() && gpsListo()) {
                guardarCobro()
            } else {
                return@setOnClickListener
            }
        }
    }

    private fun cargarComerciantes() {
        comerciantes = db.obtenerComerciantes() // retorna lista de objeto Comerciante

        val nombres = comerciantes.map { it.nombre }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nombres)
        inputNombre.setAdapter(adapter)

        // Evento: cuando seleccionan un comerciante
        inputNombre.setOnItemClickListener { parent, _, pos, _ ->
            val nombreSeleccionado = parent.getItemAtPosition(pos).toString()
            val comerciante = comerciantes.first{it.nombre == nombreSeleccionado}

            idComercianteSeleccionado = comerciante.idComerciante
            cargarPuestos(comerciante.idComerciante)
        }
    }

    private fun cargarPuestos(idComerciante: Int, idPuestoEditar:Int? = null) {
        var puestos = db.obtenerPuestosPorComerciante(idComerciante)

        // el comerciante ya no tiene el puesto asignado pero se esta editando el registro
        if (puestos.isEmpty() && idPuestoEditar != null){
            val puestoAnterior = db.obtenerPuesto(idPuestoEditar)
            if (puestoAnterior != null){
                puestos = listOf(puestoAnterior)
            }
        }

        if (puestos.isEmpty()) {
            // No tiene puestos: limpia y bloquea
            spinnerPuesto.adapter = null
            inputMonto.setText("")
            Toast.makeText(this, "Este comerciante no tiene puestos asignados", Toast.LENGTH_SHORT).show()
            return
        }

        val nombresPuestos = puestos.map { "Puesto #${it.numero}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresPuestos)
        spinnerPuesto.adapter = adapter

        // selecciona el puesto correcto si se esta editando
        if (idPuestoEditar != null){
            // si se esta editando deja seleccionado el puesto correspondiente
            val index = puestos.indexOfFirst { it.id == idPuestoEditar }
            if (index != -1) spinnerPuesto.setSelection(index)
        } else {
            // si no selecciona el primero por default
            spinnerPuesto.setSelection(0)
        }

        spinnerPuesto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val puesto = puestos[pos]
                idPuestoSeleccionado = puesto.id
                inputMonto.setText(puesto.tarifa.toString()) // monto automático
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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
                            Toast.makeText(this@FormularioCobroActivity, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
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
        val montoStr = inputMonto.text.toString().trim()
        val recibidoStr = inputRecibido.text.toString().trim()
        val fecha = inputFecha.text.toString().trim()

        if (nombre.isEmpty()) {
            inputNombre.error = "Ingrese el nombre"
            return false
        }

        if (idPuestoSeleccionado <= 0){
            Toast.makeText(this, "Seleccione un puesto válido", Toast.LENGTH_SHORT).show()
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
        val lat = txtLatitud.text.toString().replace("Latitud:", "").trim()
        val lon = txtLongitud.text.toString().replace("Longitud:", "").trim()

        if (lat.isBlank() || lon.isBlank() || lat == "-" || lon == "-") {
            Toast.makeText(this, "Esperando GPS... Por favor active el GPS", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    private fun guardarCobro() {
        val monto = inputMonto.text.toString().toDouble()
        val recibido = inputRecibido.text.toString().toDouble()
        val vuelto = recibido - monto
        val fechaUsuario = inputFecha.text.toString().trim()
        val fecha = try {
            val formatterEntrada = DateTimeFormatter.ofPattern("d/M/yyyy")
            val fechaLocal = LocalDate.parse(fechaUsuario, formatterEntrada)
            fechaLocal.toString() // convierte a yyyy-MM-dd
        } catch (e: Exception) {
            LocalDate.now().toString()
        }
        val lat = txtLatitud.text.toString().replace("Latitud: ", "").toDouble()
        val lon = txtLongitud.text.toString().replace("Longitud: ", "").toDouble()
        Log.d("comerciante: ", idComercianteSeleccionado.toString())

        val cobro = Cobro(
            idCobro = idCobro,
            idComerciante = idComercianteSeleccionado,
            idCobrador = 1,
            idPuesto = idPuestoSeleccionado,
            monto = monto,
            recibido = recibido,
            vuelto = vuelto,
            fecha = fecha,
            latitud = lat,
            longitud = lon
        )

        val ok = db.guardarCobro(cobro)

        if (ok) {
            var message = if (idCobro > 0) "Cobro Actualizado" else "Cobro Guardado"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            val returnIntent = Intent()
            setResult(RESULT_OK, returnIntent)

            finish()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
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