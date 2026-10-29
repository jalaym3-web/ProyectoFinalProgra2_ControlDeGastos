package com.example.core.repository

import com.example.core.model.Usuario
import kotlinx.coroutines.flow.Flow


interface UsuarioRepository {
    suspend fun registrar(nombre: String, apellido: String, correo: String): Usuario
    fun obtenerUsuarios(): Flow<List<Usuario>>
}