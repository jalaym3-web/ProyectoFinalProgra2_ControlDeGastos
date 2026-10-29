package com.example.core.usecase

import com.example.core.model.Usuario
import com.example.core.repository.CorreoDuplicadoException
import com.example.core.repository.UsuarioRepository

data class ErroresRegistro(
    val nombre: String? = null,
    val apellido: String? = null,
    val correo: String? = null
) {
    val hayErrores: Boolean get() = nombre != null || apellido != null || correo != null
}

sealed interface ResultadoRegistro {
    data class Exito(val usuario: Usuario) : ResultadoRegistro
    data class Invalido(val errores: ErroresRegistro) : ResultadoRegistro
}

class RegistrarUsuarioUseCase(private val repository: UsuarioRepository) {

    private val formatoCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    suspend operator fun invoke(nombre: String, apellido: String, correo: String): ResultadoRegistro {
        val n = nombre.trim()
        val a = apellido.trim()
        val c = correo.trim()

        val errores = ErroresRegistro(
            nombre = if (n.isEmpty()) "El nombre es obligatorio" else null,
            apellido = if (a.isEmpty()) "El apellido es obligatorio" else null,
            correo = when {
                c.isEmpty() -> "El correo es obligatorio"
                !formatoCorreo.matches(c) -> "Correo no válido"
                else -> null
            }
        )
        if (errores.hayErrores) return ResultadoRegistro.Invalido(errores)

        return try {
            ResultadoRegistro.Exito(repository.registrar(n, a, c.lowercase()))
        } catch (e: CorreoDuplicadoException) {
            ResultadoRegistro.Invalido(ErroresRegistro(correo = "Ya existe un usuario con ese correo"))
        }
    }
}