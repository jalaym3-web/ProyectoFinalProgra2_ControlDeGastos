package com.example.core.usecase

import com.example.core.model.FiltroMovimientos
import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento

// FiltrarMovimientosUseCase.kt
class FiltrarMovimientosUseCase {
    operator fun invoke(movimientos: List<Movimiento>, filtro: FiltroMovimientos): List<Movimiento> =
        when (filtro) {
            FiltroMovimientos.TODOS -> movimientos
            FiltroMovimientos.GASTOS -> movimientos.filter { it.tipo == TipoMovimiento.GASTO }
            FiltroMovimientos.INGRESOS -> movimientos.filter { it.tipo == TipoMovimiento.INGRESO }
        }
}