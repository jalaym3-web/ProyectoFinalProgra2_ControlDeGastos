package com.example.aplicacioncontrolgastos.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.core.model.Usuario

@Entity(
    tableName = "usuarios",   // deja el nombre que ya tienes
    indices = [Index(value = ["correo"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val correo: String
)

fun UsuarioEntity.toDomain() = Usuario(
    id = id,
    nombre = nombre,
    apellido = apellido,
    correo = correo,
)

fun Usuario.toEntity() = UsuarioEntity(
    id = id,
    nombre = nombre,
    apellido = apellido,
    correo = correo
)
