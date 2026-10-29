package com.example.aplicacioncontrolgastos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplicacioncontrolgastos.data.local.MovimientoDao
import com.example.aplicacioncontrolgastos.data.local.MovimientoEntity
import com.example.aplicacioncontrolgastos.data.local.UsuarioDao
import com.example.aplicacioncontrolgastos.data.local.UsuarioEntity
import com.example.core.model.Usuario
import com.example.core.model.Movimiento
@Database(entities = [UsuarioEntity::class, MovimientoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "control_gastos.db"
                ).build().also { INSTANCE = it }
            }
    }
}