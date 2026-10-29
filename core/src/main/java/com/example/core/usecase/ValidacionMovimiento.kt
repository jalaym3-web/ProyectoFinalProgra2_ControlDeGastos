package com.example.core.usecase

    internal fun errorDeValidacion(monto: Double, descripcion: String): String? = when {
        monto <= 0 -> "El monto debe ser mayor que 0"
        descripcion.isBlank() -> "La descripción no puede estar vacía"
        else -> null
    }
