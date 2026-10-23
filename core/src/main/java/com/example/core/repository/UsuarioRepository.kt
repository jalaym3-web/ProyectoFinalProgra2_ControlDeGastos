package com.example.core.repository

import com.example.core.model.Usuario

interface UsuarioRepository {
    fun guardar(usuario: Usuario): Long
    fun obtenerActual(): Usuario?
}