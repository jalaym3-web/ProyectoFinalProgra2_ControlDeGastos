package com.example.core.usecase

import com.example.core.model.Movimiento
import com.example.core.repository.MovimientoRepository



// AgregarMovimientoUseCase.kt
class AgregarMovimientoUseCase(private val repository: MovimientoRepository) {

    suspend operator fun invoke(movimiento: Movimiento): Result<Unit> {
        errorDeValidacion(movimiento.monto, movimiento.descripcion)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        repository.agregar(movimiento)
        return Result.success(Unit)
    }
}