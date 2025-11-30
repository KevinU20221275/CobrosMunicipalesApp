package net.irivas.cobrosapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CobrosDBHelper(context: Context) : SQLiteOpenHelper(context, "cobros.dp", null, 3){
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
                nombre TEXT NOT NULL,
                telefono TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE puesto (
                id_puesto INTEGER PRIMARY KEY AUTOINCREMENT,
                numero INTEGER NOT NULL,
                tarifa REAL NOT NULL,
                disponible INTEGER NOT NULL DEFAULT 1
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS comerciante_puesto (
                id_comerciante INTEGER NOT NULL,
                id_puesto INTEGER NOT NULL UNIQUE,
                FOREIGN KEY (id_comerciante) REFERENCES comerciante(id_comerciante),
                FOREIGN KEY (id_puesto) REFERENCES puesto(id_puesto)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE cobro (
                id_cobro INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cobrador INTEGER NOT NULL,
                id_comerciante INTEGER NOT NULL,
                id_puesto INTEGER NOT NULL,
                monto REAL NOT NULL,
                recibido REAL NOT NULL,
                vuelto REAL NOT NULL,
                fecha TEXT NOT NULL,
                latitud REAL,
                longitud REAL,
                FOREIGN KEY (id_cobrador) REFERENCES cobrador(id_cobrador),
                FOREIGN KEY (id_comerciante) REFERENCES comerciante(id_comerciante),
                FOREIGN KEY (id_puesto) REFERENCES puesto(id_puesto)
            )
        """)

        // Insertar usuario inicial (Rosa)
        db.execSQL("""
            INSERT INTO cobrador (nombre, usuario, contrasena)
            VALUES ('Rosa Gómez', 'rosa', '1234')
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS cobro")
        db.execSQL("DROP TABLE IF EXISTS comerciante_puesto")
        db.execSQL("DROP TABLE IF EXISTS puesto")
        db.execSQL("DROP TABLE IF EXISTS comerciante")
        db.execSQL("DROP TABLE IF EXISTS cobrador")
        onCreate(db)
    }

    // INSERTAR Y ACTUALIZAR
    fun guardarCobro(cobro: Cobro): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        values.put("id_cobrador", cobro.idCobrador)
        values.put("id_comerciante", cobro.idComerciante)
        values.put("id_puesto", cobro.idPuesto)
        values.put("monto", cobro.monto)
        values.put("recibido", cobro.recibido)
        values.put("vuelto", cobro.vuelto)
        values.put("fecha", cobro.fecha)
        values.put("latitud", cobro.latitud)
        values.put("longitud", cobro.longitud)

        return if (cobro.idCobro > 0){
            val res = db.update("cobro", values, "id_cobro = ?", arrayOf(cobro.idCobro.toString()))
            res > 0
        } else {
            val res = db.insert("cobro", null, values)
            res != -1L
        }
    }

    fun guardarComerciante(idComerciante: Int?, nombre: String, telefono: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("nombre", nombre)
        values.put("telefono", telefono)

        return if (idComerciante != null && idComerciante > 0){
            val res = db.update("comerciante", values, "id_comerciante = ?",
                arrayOf(idComerciante.toString()))
            res > 0
        } else {
            val res = db.insert("comerciante", null, values)
            res != -1L
        }

    }

    fun guardarPuesto(idPuesto: Int?, numero: Int, tarifa: Double): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("numero", numero)
        values.put("tarifa", tarifa)

        return if (idPuesto != null && idPuesto > 0) {
            val cursor = db.rawQuery(
                "SELECT id_puesto FROM puesto WHERE numero = ? AND id_puesto != ?",
                arrayOf(numero.toString(), idPuesto.toString())
            )
            val existe = cursor.count > 0
            cursor.close()

            if (existe) {
                false // No se puede actualizar, número duplicado
            } else {
                val res = db.update("puesto", values, "id_puesto = ?", arrayOf(idPuesto.toString()))
                res > 0
            }
        } else {
            // Insertar
            val res = db.insert("puesto", null, values)
            res != -1L
        }
    }

    fun asignarPuestosAComerciante(idComerciante: Int, puestos: List<Int>): Boolean {
        val db = writableDatabase
        return try {
            for (idPuesto in puestos) {
                // Insertar relación en la tabla intermedia
                db.execSQL("""
                INSERT INTO comerciante_puesto (id_comerciante, id_puesto)
                VALUES (?, ?)
            """.trimIndent(), arrayOf(idComerciante, idPuesto))

                // Actualizar disponibilidad del puesto
                db.execSQL("""
                UPDATE puesto SET disponible = 0
                WHERE id_puesto = ?
            """.trimIndent(), arrayOf(idPuesto))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun liberarPuesto(idPuesto: Int) {
        val db = writableDatabase
        db.execSQL("DELETE FROM comerciante_puesto WHERE id_puesto = ?", arrayOf(idPuesto))
        db.execSQL("UPDATE puesto SET disponible = 1 WHERE id_puesto = ?", arrayOf(idPuesto))
        db.close()
    }

    fun insertarCobrador(nombre: String, usuario: String, contrasena: String): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("nombre", nombre)
        values.put("usuario", usuario)
        values.put("contrasena", contrasena)

        return db.insert("cobrador", null, values)
    }

    // SELECTS
    fun obtenerPuesto(idPuesto: Int): Puesto? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id_puesto, numero, tarifa, disponible FROM puesto WHERE id_puesto = ?", arrayOf(idPuesto.toString()))
        val puesto = if (cursor.moveToFirst()) {
            Puesto(
                cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getInt(3)
            )
        } else null
        cursor.close()
        db.close()
        return puesto
    }

    fun obtenerPuestosDisponibles(): List<Puesto> {
        val lista = mutableListOf<Puesto>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT id_puesto, numero, tarifa, disponible 
            FROM puesto 
            WHERE disponible = 1
        """, null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Puesto(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getInt(3))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerPuestos(): List<Puesto> {
        val lista = mutableListOf<Puesto>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT id_puesto, numero, tarifa, disponible 
            FROM puesto
        """, null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Puesto(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getInt(3))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerComerciante(idComerciante: Int): Comerciante {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM comerciante WHERE id_comerciante = ?", arrayOf(idComerciante.toString()))
        cursor.moveToFirst()
        val comerciante = Comerciante(
            idComerciante = cursor.getInt(0),
            nombre = cursor.getString(1),
            telefono = cursor.getString(2)
        )
        cursor.close()
        return comerciante
    }

    fun obtenerComerciantes(): List<Comerciante> {
        val lista = mutableListOf<Comerciante>()

        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id_comerciante, nombre, telefono FROM comerciante ORDER BY id_comerciante ASC", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(Comerciante(cursor.getInt(0), cursor.getString(1), cursor.getString(2)))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return lista
    }

    fun obtenerPuestosPorComerciante(idComerciante: Int): List<Puesto> {
        val lista = mutableListOf<Puesto>()
        val db = readableDatabase

        val sql = """
        SELECT p.id_puesto, p.numero, p.tarifa, p.disponible
        FROM puesto p
        INNER JOIN comerciante_puesto cp
            ON p.id_puesto = cp.id_puesto
        WHERE cp.id_comerciante = ?
    """.trimIndent()

        var cursor = db.rawQuery(sql, arrayOf(idComerciante.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val numero = cursor.getString(1)
                val tarifa = cursor.getDouble(2)
                val disponible = cursor.getInt(3)

                lista.add(Puesto(id = id, numero = numero, tarifa = tarifa, disponible = disponible))
            } while (cursor.moveToNext())
        } else {
            Log.d("DB_DEBUG", "No se encontraron puestos para el comerciante $idComerciante")
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerCobrosConInfo(): List<CobroDTO> {
        val lista = mutableListOf<CobroDTO>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT c.id_cobro, com.nombre, p.numero, c.monto, c.recibido, c.vuelto, c.fecha
            FROM cobro c
            INNER JOIN comerciante com ON c.id_comerciante = com.id_comerciante
            INNER JOIN puesto p ON c.id_puesto = p.id_puesto
            ORDER BY c.id_cobro DESC
        """.trimIndent(), null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    CobroDTO(
                        cursor.getInt(0),
                        cursor.getStringOrNull(1),
                        cursor.getStringOrNull(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerCobrosConInfoPorFecha(fecha: String): List<CobroDTO> {
        val lista = mutableListOf<CobroDTO>()
        val db = readableDatabase

        val cursor = db.rawQuery("""
        SELECT c.id_cobro, com.nombre, p.numero, c.monto, c.recibido, c.vuelto, c.fecha
        FROM cobro c
        INNER JOIN comerciante com ON c.id_comerciante = com.id_comerciante
        INNER JOIN puesto p ON c.id_puesto = p.id_puesto
        WHERE c.fecha = ?
        ORDER BY c.id_cobro DESC
    """.trimIndent(), arrayOf(fecha))

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    CobroDTO(
                        cursor.getInt(0),
                        cursor.getStringOrNull(1),
                        cursor.getStringOrNull(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerCobrosConInfoPorFecha(inicio: String, fin: String): List<CobroDTO> {
        val lista = mutableListOf<CobroDTO>()
        val db = readableDatabase

        val cursor = db.rawQuery("""
        SELECT c.id_cobro, com.nombre, p.numero, c.monto, c.recibido, c.vuelto, c.fecha
        FROM cobro c
        INNER JOIN comerciante com ON c.id_comerciante = com.id_comerciante
        INNER JOIN puesto p ON c.id_puesto = p.id_puesto
        WHERE c.fecha BETWEEN ? AND ?
        ORDER BY c.id_cobro DESC
    """.trimIndent(), arrayOf(inicio, fin))

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    CobroDTO(
                        cursor.getInt(0),
                        cursor.getStringOrNull(1),
                        cursor.getStringOrNull(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }


    fun obtenerCobroParaEditar(idCobro: Int): CobroEditarDTO? {
        val db = readableDatabase

        val cursor = db.rawQuery("""
        SELECT id_cobro, id_comerciante, id_puesto, monto, recibido, vuelto, fecha, latitud, longitud
        FROM cobro WHERE id_cobro = ? LIMIT 1
        """.trimIndent(), arrayOf(idCobro.toString()))

        var cobro: CobroEditarDTO? = null

        if (cursor.moveToFirst()) {
            cobro = CobroEditarDTO(
                cursor.getInt(0),  // idCobro
                cursor.getInt(1),  // idComerciante
                cursor.getInt(2),  // idPuesto
                cursor.getDouble(3),
                cursor.getDouble(4),
                cursor.getDouble(5),
                cursor.getString(6),
                cursor.getDouble(7),
                cursor.getDouble(8)
            )
        }

        cursor.close()
        db.close()
        return cobro
    }

    // Eliminar
    fun eliminarCobro(id:Int):Boolean {
        val db = writableDatabase
        val resultado = db.delete("cobro", "id_cobro = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    // funcion para validar el usuario
    fun validarUsuario(usuario: String, contrasena: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM cobrador WHERE usuario = ? AND contrasena = ?",
            arrayOf(usuario, contrasena)
        )

        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    private fun Cursor.getStringOrNull(index: Int): String? {
        return if (isNull(index)) null else getString(index)
    }

    // funcion para convertir cursor a objeto Cobro
    private fun cursorACobro(cursor: Cursor): Cobro {
        return Cobro(
            idCobro = cursor.getInt(cursor.getColumnIndexOrThrow("id_cobro")),
            idComerciante = cursor.getInt(cursor.getColumnIndexOrThrow("id_comerciante")),
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