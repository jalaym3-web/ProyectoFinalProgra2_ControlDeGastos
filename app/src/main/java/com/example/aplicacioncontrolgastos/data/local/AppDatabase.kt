package com.example.aplicacioncontrolgastos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aplicacioncontrolgastos.data.local.MovimientoDao
import com.example.aplicacioncontrolgastos.data.local.MovimientoEntity
import com.example.aplicacioncontrolgastos.data.local.UsuarioDao
import com.example.aplicacioncontrolgastos.data.local.UsuarioEntity


@Database(entities = [UsuarioEntity::class, MovimientoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun movimientoDao(): MovimientoDao

    // companion object igual que antes
}