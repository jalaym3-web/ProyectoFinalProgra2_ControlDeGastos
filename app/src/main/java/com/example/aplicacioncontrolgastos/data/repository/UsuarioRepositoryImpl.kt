package com.example.aplicacioncontrolgastos.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.aplicacioncontrolgastos.data.local.UsuarioDao
import com.example.aplicacioncontrolgastos.data.local.UsuarioEntity
import com.example.core.model.Usuario
import com.example.core.repository.CorreoDuplicadoException
import com.example.core.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// UsuarioRepositoryImpl: solo cambia la primera línea y los override
class UsuarioRepositoryImpl(private val usuarioDao: UsuarioDao) : UsuarioRepository {

    override suspend fun registrar(nombre: String, apellido: String, correo: String): Usuario {
        val entity = UsuarioEntity(nombre = nombre, apellido = apellido, correo = correo)
        val id = try {
            usuarioDao.insertar(entity)
        } catch (e: SQLiteConstraintException) {
            throw CorreoDuplicadoException()
        }
        return Usuario(id = id, nombre = nombre, apellido = apellido, correo = correo)
    }

    override fun obtenerUsuarios(): Flow<List<Usuario>> =
        usuarioDao.obtenerTodos().map { lista ->
            lista.map { Usuario(id = it.id, nombre = it.nombre, apellido = it.apellido, correo = it.correo) }
        }
}