package net.irivas.cobrosapp.data

data class Cobro(
    val idCobro: Int = 0,
    val idCobrador: Int,
    val idComerciante: Int,
    val idPuesto: Int,
    val monto: Double,
    val recibido: Double,
    val vuelto: Double,
    val fecha: String,
    val latitud: Double,
    val longitud: Double
)
