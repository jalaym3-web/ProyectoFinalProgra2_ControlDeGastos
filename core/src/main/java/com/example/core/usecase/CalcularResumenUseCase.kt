package com.example.core.usecase

import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento

// CalcularResumenUseCase.kt
data class ResumenMovimientos(val totalIngresos: Double, val totalGastos: Double) {
    val balance: Double get() = totalIngresos - totalGastos
}

class CalcularResumenUseCase {
    operator fun invoke(movimientos: List<Movimiento>): ResumenMovimientos =
        ResumenMovimientos(
            totalIngresos = movimientos.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto },
            totalGastos = movimientos.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }
        )
}