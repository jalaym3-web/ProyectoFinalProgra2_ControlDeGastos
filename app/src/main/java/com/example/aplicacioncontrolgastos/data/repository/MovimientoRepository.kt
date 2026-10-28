package com.example.aplicacioncontrolgastos.data.repository

import com.example.aplicacioncontrolgastos.data.local.MovimientoDao
import com.example.aplicacioncontrolgastos.data.local.toDomain
import com.example.aplicacioncontrolgastos.data.local.toEntity
import com.example.core.model.Movimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovimientoRepository(private val dao: MovimientoDao) {

    suspend fun agregar(movimiento: Movimiento) {
        dao.insertar(movimiento.toEntity())
    }

    fun movimientosDe(usuarioId: Long): Flow<List<Movimiento>> =
        dao.movimientosDe(usuarioId).map { lista -> lista.map { it.toDomain() } }
}