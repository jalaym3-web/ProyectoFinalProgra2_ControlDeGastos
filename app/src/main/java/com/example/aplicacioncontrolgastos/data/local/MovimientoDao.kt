package com.example.aplicacioncontrolgastos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface MovimientoDao {
    @Insert
    suspend fun insertar(mov: MovimientoEntity)

    @Query("SELECT * FROM movimientos WHERE usuarioId = :usuarioId")
    fun movimientosDe(usuarioId: Long): Flow<List<MovimientoEntity>>
}

@Query("DELETE FROM movimientos WHERE id = :id")
suspend fun eliminarPorId(id: Long)