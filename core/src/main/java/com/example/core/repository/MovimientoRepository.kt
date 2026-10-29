package com.example.core.repository
import com.example.core.model.Movimiento
import kotlinx.coroutines.flow.Flow

interface MovimientoRepository {
        suspend fun agregar(movimiento: Movimiento)
        suspend fun actualizar(movimiento: Movimiento)
        suspend fun eliminar(movimientoId: Long)
        fun movimientosDe(usuarioId: Long): Flow<List<Movimiento>>
}