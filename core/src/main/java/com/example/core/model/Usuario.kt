package com.example.core.model

data class Usuario(val id: Long = 0, val nombre: String, val apellido: String, val correo: String,     val movimientos: List<Movimiento> = emptyList()
) {
    val balance: Double
        get() = movimientos.sumOf {
            when (it.tipo) {
                TipoMovimiento.INGRESO -> it.monto
                TipoMovimiento.GASTO -> -it.monto
            }
        }
}