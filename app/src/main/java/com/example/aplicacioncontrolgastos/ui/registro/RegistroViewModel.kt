package com.example.aplicacioncontrolgastos.ui.registro

import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.example.aplicacioncontrolgastos.data.repository.UsuarioRepository
import com.example.core.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val errorNombre: String? = null,
    val errorApellido: String? = null,
    val errorCorreo: String? = null,
    val registrado: Boolean = false,
    val usuarioId: Long = 0
)



class RegistroViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()



    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = null) }
    }

    fun onApellidoChange(valor: String) {
        _uiState.update { it.copy(apellido = valor, errorApellido = null) }
    }

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null) }
    }

    fun registrar() {
        val estado = _uiState.value
        val nombre = estado.nombre.trim()
        val apellido = estado.apellido.trim()
        val correo = estado.correo.trim()

        val errorNombre = if (nombre.isEmpty()) "El nombre es obligatorio" else null
        val errorApellido = if (apellido.isEmpty()) "El apellido es obligatorio" else null
        val errorCorreo = when {
            correo.isEmpty() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> "Correo no válido"
            else -> null
        }

        if (errorNombre != null || errorApellido != null || errorCorreo != null) {
            _uiState.update {
                it.copy(
                    errorNombre = errorNombre,
                    errorApellido = errorApellido,
                    errorCorreo = errorCorreo
                )
            }
            return
        }

        viewModelScope.launch {
            val usuario = repository.registrar(nombre, apellido, correo)
            _uiState.update { it.copy(registrado = true, usuarioId = usuario.id) }
        }
    }
}