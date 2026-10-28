package com.example.aplicacioncontrolgastos.data.repository

import com.example.aplicacioncontrolgastos.data.local.UsuarioDao
import com.example.aplicacioncontrolgastos.data.local.UsuarioEntity
import com.example.aplicacioncontrolgastos.data.local.toDomain
import com.example.core.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrar(nombre: String, apellido: String, correo: String): Usuario {
        val entity = UsuarioEntity(nombre = nombre, apellido = apellido, correo = correo)
        val id = usuarioDao.insertar(entity)
        return Usuario(id = id, nombre = nombre, apellido = apellido, correo = correo)
    }

    fun obtenerUsuarios(): Flow<List<Usuario>> =
        usuarioDao.obtenerTodos().map { lista ->
            lista.map { Usuario(id = it.id, nombre = it.nombre, apellido = it.apellido, correo = it.correo) }
        }
}