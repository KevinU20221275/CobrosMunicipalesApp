package net.irivas.cobrosapp.data

data class Pago(
    val id: Int = 0,
    val nombre: String,
    val numeroPuesto: String,
    val monto: Double,
    val recibido: Double,
    val vuelto: Double,
    val fecha: String,
    val latitud: Double,
    val longitud: Double
)
