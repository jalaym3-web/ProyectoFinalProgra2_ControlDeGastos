package com.example.core.usecase

import com.example.core.repository.MovimientoRepository

// EliminarMovimientoUseCase.kt
class EliminarMovimientoUseCase(private val repository: MovimientoRepository) {
    suspend operator fun invoke(movimientoId: Long) = repository.eliminar(movimientoId)
}