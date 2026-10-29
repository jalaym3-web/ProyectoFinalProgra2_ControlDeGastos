package com.example.aplicacioncontrolgastos.data.repository

import com.example.aplicacioncontrolgastos.data.local.MovimientoDao
import com.example.aplicacioncontrolgastos.data.local.toDomain
import com.example.aplicacioncontrolgastos.data.local.toEntity
import com.example.core.model.Movimiento
import com.example.core.repository.MovimientoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovimientoRepositoryImpl(private val dao: MovimientoDao) : MovimientoRepository {

    override suspend fun agregar(movimiento: Movimiento) {
        dao.insertar(movimiento.toEntity())
    }

    override suspend fun actualizar(movimiento: Movimiento) {
        dao.actualizar(movimiento.toEntity())
    }

    override suspend fun eliminar(movimientoId: Long) {
        dao.eliminarPorId(movimientoId)
    }

    override fun movimientosDe(usuarioId: Long): Flow<List<Movimiento>> =
        dao.movimientosDe(usuarioId).map { lista -> lista.map { it.toDomain() } }
}

