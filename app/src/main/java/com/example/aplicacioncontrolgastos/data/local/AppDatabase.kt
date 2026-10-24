package com.example.aplicacioncontrolgastos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplicacioncontrolgastos.data.local.MovimientoDao
import com.example.aplicacioncontrolgastos.data.local.MovimientoEntity
import com.example.aplicacioncontrolgastos.data.local.UsuarioDao
import com.example.aplicacioncontrolgastos.data.local.UsuarioEntity


@Database(entities = [UsuarioEntity::class, MovimientoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gastos.db"
                ).build().also { instancia = it }
            }
    }
}