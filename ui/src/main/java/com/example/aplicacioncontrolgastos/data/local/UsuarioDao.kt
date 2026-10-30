package com.example.aplicacioncontrolgastos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun obtenerTodos(): Flow<List<UsuarioEntity>>
}
