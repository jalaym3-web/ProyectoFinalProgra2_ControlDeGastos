package com.example.aplicacioncontrolgastos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun obtenerUsuario(): UsuarioEntity?
}
