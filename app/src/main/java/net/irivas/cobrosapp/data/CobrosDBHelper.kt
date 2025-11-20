package net.irivas.cobrosapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PagosDBHelper(context: Context) : SQLiteOpenHelper(context, "cobros.dp", null, 2){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE cobrador (
                id_cobrador INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                usuario TEXT UNIQUE NOT NULL,
                contrasena TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE comerciante (
                id_comerciante INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE puesto (
                id_puesto INTEGER PRIMARY KEY AUTOINCREMENT,
                id_comerciante INTEGER NOT NULL,
                numero_puesto INTEGER NOT NULL,
                tarifa REAL NOT NULL,
                FOREIGN KEY (id_comerciante) REFERENCES comerciante(id_comerciante)
            )
        """)

        db.execSQL("""
            CREATE TABLE cobro (
                id_cobro INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cobrador INTEGER NOT NULL,
                id_puesto INTEGER NOT NULL,
                monto REAL NOT NULL,
                recibido REAL NOT NULL,
                vuelto REAL NOT NULL,
                fecha TEXT NOT NULL,
                latitud REAL,
                longitud REAL,
                FOREIGN KEY (id_cobrador) REFERENCES cobrador(id_cobrador),
                FOREIGN KEY (id_puesto) REFERENCES puesto(id_puesto)
            )
        """)

        db.execSQL("INSERT INTO comerciante (nombre) VALUES ('Juan Pérez')")
        db.execSQL("INSERT INTO comerciante (nombre) VALUES ('María López')")

// Puestos de Juan (id_comerciante = 1)
        db.execSQL("INSERT INTO puesto (id_comerciante, numero, tarifa) VALUES (1, 12, 0.50)")
        db.execSQL("INSERT INTO puesto (id_comerciante, numero, tarifa) VALUES (1, 13, 0.75)")

// Puestos de María (id_comerciante = 2)
        db.execSQL("INSERT INTO puesto (id_comerciante, numero, tarifa) VALUES (2, 21, 0.50)")
        db.execSQL("INSERT INTO puesto (id_comerciante, numero, tarifa) VALUES (2, 22, 1.00)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS cobro")
        db.execSQL("DROP TABLE IF EXISTS puesto")
        db.execSQL("DROP TABLE IF EXISTS comerciante")
        db.execSQL("DROP TABLE IF EXISTS cobrador")
        onCreate(db)
    }

    // insertar registros
    fun insertarCobro(cobro: Cobro): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        values.put("id_cobrador", cobro.idCobrador)
        values.put("id_puesto", cobro.idPuesto)
        values.put("monto", cobro.monto)
        values.put("recibido", cobro.recibido)
        values.put("vuelto", cobro.vuelto)
        values.put("fecha", cobro.fecha)
        values.put("latitud", cobro.latitud)
        values.put("longitud", cobro.longitud)

        val resultado = db.insert("cobro", null, values)

        db.close()

        return resultado != -1L
    }

    fun insertarComerciante(nombre: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put("nombre", nombre)
        return db.insert("comerciante", null, values)
    }

    fun insertarPuesto(idComerciante: Int, numero: Int, tarifa: Double): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("id_comerciante", idComerciante)
        values.put("numero_puesto", numero)
        values.put("tarifa", tarifa)

        return db.insert("puesto", null, values)
    }

    fun insertarCobrador(nombre: String, usuario: String, contrasena: String): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("nombre", nombre)
        values.put("usuario", usuario)
        values.put("contrasena", contrasena)

        return db.insert("cobrador", null, values)
    }

    fun obtenerPuesto(idPuesto: Int): Puesto {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM puesto WHERE id = ?", arrayOf(idPuesto.toString()))
        cursor.moveToFirst()
        val puesto = Puesto(
            id = cursor.getInt(0),
            idComerciante = cursor.getInt(1),
            numero = cursor.getInt(2),
            tarifa = cursor.getDouble(3)
        )
        cursor.close()
        return puesto
    }

    fun obtenerComerciante(idComerciante: Int): Comerciante {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM comerciante WHERE id_comerciante = ?", arrayOf(idComerciante.toString()))
        cursor.moveToFirst()
        val comerciante = Comerciante(
            idComerciante = cursor.getInt(0),
            nombre = cursor.getString(1)
        )
        cursor.close()
        return comerciante
    }


    // obtener los comerciantes
    fun obtenerComerciantes(): List<Comerciante> {
        val lista = mutableListOf<Comerciante>()

        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id_comerciante, nombre FROM comerciante", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_comerciante"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))

                lista.add(Comerciante(id, nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return lista
    }


    // obtener puestos por comerciante 1 comerciante puede tener n puestos
    fun obtenerPuestosPorComerciante(idComerciante: Int): List<Puesto> {
        val lista = mutableListOf<Puesto>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT * FROM puesto WHERE id_comerciante = ? 
        """, arrayOf(idComerciante.toString()))

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Puesto(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_puesto")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_comerciante")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("numero_puesto")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("tarifa"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    // listar todos los cobros
    fun obtenerCobros(): List<Cobro> {
        val lista = mutableListOf<Cobro>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM cobro ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorACobro(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

    // buscar por nombre o puesto
    fun buscarCobros(query: String): List<Cobro> {
        val lista = mutableListOf<Cobro>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM cobro WHERE nombre LIKE ? OR puesto LIKE ? ORDER BY id DESC",
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorACobro(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

    // obtener pagos por fecha
    fun obtenerCobrosPorFecha(filtro: String): List<Cobro> {
        val lista = mutableListOf<Cobro>()
        val db = readableDatabase

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoy = Calendar.getInstance()

        val fechaHoy = formato.format(hoy.time)
        val fechaAyer = formato.format(hoy.apply { add(Calendar.DAY_OF_YEAR, -1) }.time)

        val cursor: Cursor = when (filtro.uppercase()) {

            "HOY" -> {
                db.rawQuery(
                    "SELECT * FROM cobro WHERE fecha LIKE ?",
                    arrayOf("%$fechaHoy%")
                )
            }

            "AYER" -> {
                db.rawQuery(
                    "SELECT * FROM cobro WHERE fecha LIKE ?",
                    arrayOf("%$fechaAyer%")
                )
            }

            "SEMANA" -> {
                val weekAgo = Calendar.getInstance()
                weekAgo.add(Calendar.DAY_OF_YEAR, -7)
                val fechaSemana = formato.format(weekAgo.time)

                db.rawQuery(
                    "SELECT * FROM cobro WHERE date(substr(fecha, instr(fecha, ',') + 2)) >= date(?)",
                    arrayOf(fechaSemana)
                )
            }

            else -> db.rawQuery("SELECT * FROM cobro", null)
        }

        if (cursor.moveToFirst()) {
            do lista.add(cursorACobro(cursor))
            while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

    // funcion para convertir cursor a objeto Cobro
    private fun cursorACobro(cursor: Cursor): Cobro {
        return Cobro(
            idCobro = cursor.getInt(cursor.getColumnIndexOrThrow("id_cobro")),
            idCobrador = cursor.getInt(cursor.getColumnIndexOrThrow("id_cobrador")),
            idPuesto = cursor.getInt(cursor.getColumnIndexOrThrow("id_puesto")),
            monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
            recibido = cursor.getDouble(cursor.getColumnIndexOrThrow("recibido")),
            vuelto = cursor.getDouble(cursor.getColumnIndexOrThrow("vuelto")),
            fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
            latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")),
            longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
        )
    }
}