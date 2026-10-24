package com.example.aplicacioncontrolgastos.data.repository

import com.example.aplicacioncontrolgastos.data.local.UsuarioDao
import com.example.aplicacioncontrolgastos.data.local.UsuarioEntity
import com.example.aplicacioncontrolgastos.data.local.toDomain
import com.example.core.model.Usuario


class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrar(nombre: String, apellido: String, correo: String) {
        usuarioDao.insertar(
            UsuarioEntity(nombre = nombre, apellido = apellido, correo = correo)
        )
    }

    suspend fun obtenerUsuario(): Usuario? =
        usuarioDao.obtenerUsuario()?.let {
            Usuario(nombre = it.nombre, apellido = it.apellido, correo = it.correo)
        }
}