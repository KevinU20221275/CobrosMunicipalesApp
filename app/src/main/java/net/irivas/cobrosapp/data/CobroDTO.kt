package net.irivas.cobrosapp.data

data class CobroDTO(
    val idCobro: Int,
    val nombreComerciante: String?,
    val numeroPuesto: String?,
    val monto: Double,
    val recibido: Double,
    val vuelto: Double,
    val fecha: String
)
