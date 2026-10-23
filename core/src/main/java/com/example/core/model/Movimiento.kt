package com.example.core.model

data class Movimiento(
    val id: Long = 0,
    val usuarioId: Long,
    val tipo: TipoMovimiento,
    val monto: Double,
    val descripcion: String,
    val fecha: Long
)
