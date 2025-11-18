package net.irivas.cobrosapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PagosDBHelper(context: Context) : SQLiteOpenHelper(context, "pagos.dp", null, 1){
    override fun onCreate(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE pagos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                numeroPuesto TEXT,
                monto REAL,
                recibido REAL,
                vuelto REAL,
                fecha TEXT,
                latitud REAL,
                longitud REAL
            );
        """.trimIndent()

        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pagos")
        onCreate(db)
    }

    // insertar registros
    fun insertarPago(pago: Pago): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        values.put("nombre", pago.nombre)
        values.put("numeroPuesto", pago.numeroPuesto)
        values.put("fecha", pago.fecha)
        values.put("monto", pago.monto)
        values.put("recibido", pago.recibido)
        values.put("vuelto", pago.vuelto)
        values.put("latitud", pago.latitud)
        values.put("longitud", pago.longitud)

        val resultado = db.insert("pagos", null, values)

        db.close()

        return resultado != -1L
    }

    // listar todos los pagos/cobros
    fun obtenerPagos(): List<Pago> {
        val lista = mutableListOf<Pago>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM pagos ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorAPago(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

    // buscar por nombre o puesto
    fun buscarPagos(query: String): List<Pago> {
        val lista = mutableListOf<Pago>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM pagos WHERE nombre LIKE ? OR puesto LIKE ? ORDER BY id DESC",
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorAPago(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

    // obtener pagos por fecha
    fun obtenerPagosPorFecha(filtro: String): List<Pago> {
        val lista = mutableListOf<Pago>()
        val db = readableDatabase

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoy = Calendar.getInstance()

        val fechaHoy = formato.format(hoy.time)
        val fechaAyer = formato.format(hoy.apply { add(Calendar.DAY_OF_YEAR, -1) }.time)

        val cursor: Cursor = when (filtro.uppercase()) {

            "HOY" -> {
                db.rawQuery(
                    "SELECT * FROM pagos WHERE fecha LIKE ?",
                    arrayOf("%$fechaHoy%")
                )
            }

            "AYER" -> {
                db.rawQuery(
                    "SELECT * FROM pagos WHERE fecha LIKE ?",
                    arrayOf("%$fechaAyer%")
                )
            }

            "SEMANA" -> {
                val weekAgo = Calendar.getInstance()
                weekAgo.add(Calendar.DAY_OF_YEAR, -7)
                val fechaSemana = formato.format(weekAgo.time)

                db.rawQuery(
                    "SELECT * FROM pagos WHERE date(substr(fecha, instr(fecha, ',') + 2)) >= date(?)",
                    arrayOf(fechaSemana)
                )
            }

            else -> db.rawQuery("SELECT * FROM pagos", null)
        }

        if (cursor.moveToFirst()) {
            do lista.add(cursorAPago(cursor))
            while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

    // funcion para convertir cursor a objeto Pago
    private fun cursorAPago(cursor: Cursor): Pago {
        return Pago(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
            numeroPuesto = cursor.getString(cursor.getColumnIndexOrThrow("numeroPuesto")),
            fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
            monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
            recibido = cursor.getDouble(cursor.getColumnIndexOrThrow("recibido")),
            vuelto = cursor.getDouble(cursor.getColumnIndexOrThrow("vuelto")),
            longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")),
            latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"))
        )
    }
}