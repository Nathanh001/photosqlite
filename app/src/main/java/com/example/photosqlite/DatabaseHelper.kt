package com.example.photosqlite

// DatabaseHelper.kt
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Constantes de la base de datos
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Photography.db"

        // Constantes de la tabla
        const val TABLE_NAME = "photographs"
        const val COLUMN_ID = BaseColumns._ID // _id (buena práctica)
        const val COLUMN_IMAGE = "image_blob"
        const val COLUMN_DESCRIPTION = "description_string"
    }

    // 1. Se llama al crear la base de datos
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSql = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_IMAGE BLOB,
                $COLUMN_DESCRIPTION TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableSql)
    }

    // 2. Se llama si actualizas la versión de la BD
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 3. Método para GUARDAR (Insertar)
    fun addPhotograph(image: ByteArray, description: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IMAGE, image)
            put(COLUMN_DESCRIPTION, description)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id // Retorna el ID de la nueva fila
    }

    // 4. Método para LEER (Consultar)
    fun getAllPhotographs(): List<Photograph> {
        val photoList = ArrayList<Photograph>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                    val image = it.getBlob(it.getColumnIndexOrThrow(COLUMN_IMAGE))
                    val description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

                    photoList.add(Photograph(id, image, description))
                } while (it.moveToNext())
            }
        }
        // cursor.close() es llamado automáticamente por 'use'
        db.close()
        return photoList
    }
}