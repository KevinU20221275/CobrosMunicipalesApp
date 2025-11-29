package net.irivas.cobrosapp.data

data class CobroResumen(
    val fecha: String,
    val comerciante: String,
    val puesto: String,
    val monto: Double
)

data class ReporteStats(
    val periodo: String,
    val totalCobrado: Double,
    val totalRecibido: Double,
    val totalVuelto: Double,
    val numeroCobros: Int,
    val PromedioPorCobro: Double
)

data class TopComerciante(
    val nombre: String,
    val totalCobros: Int,
    val totalPagado: Double
)

data class CobroPorPuesto(
    val numeroPuesto: String,
    val totalCobros: Int,
    val totalMonto: Double
)

data class Metricas(
    val diaMayor: String,
    val montoMayor: Double,
    val diaMenor: String,
    val montoMenor: Double,
    val comercianteTop: String,
    val montoTop: Double
)


