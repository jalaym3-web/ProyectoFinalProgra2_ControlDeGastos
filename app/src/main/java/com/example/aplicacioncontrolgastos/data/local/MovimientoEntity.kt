package com.example.aplicacioncontrolgastos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento

@Entity(
    tableName = "movimientos",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("usuarioId")]
)

data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val descripcion: String,
    val monto: Double,
    val esIngreso: Boolean,
    val fecha: Long          // nuevo: milisegundos (System.currentTimeMillis())
)

fun MovimientoEntity.toDomain() = Movimiento(
    id = id,
    usuarioId = usuarioId,
    tipo = if (esIngreso) TipoMovimiento.INGRESO else TipoMovimiento.GASTO,
    monto = monto,
    descripcion = descripcion,
    fecha = fecha
)

fun Movimiento.toEntity() = MovimientoEntity(
    id = id,
    usuarioId = usuarioId,
    descripcion = descripcion,
    monto = monto,
    esIngreso = tipo == TipoMovimiento.INGRESO,
    fecha = fecha
)