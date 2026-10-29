package com.example.aplicacioncontrolgastos.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.repository.UsuarioRepository
import com.example.core.usecase.RegistrarUsuarioUseCase
import com.example.core.usecase.ResultadoRegistro
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
    private val repository: UsuarioRepository,
    private val registrarUsuario: RegistrarUsuarioUseCase = RegistrarUsuarioUseCase(repository)
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
        viewModelScope.launch {
            when (val resultado = registrarUsuario(estado.nombre, estado.apellido, estado.correo)) {
                is ResultadoRegistro.Invalido -> _uiState.update {
                    it.copy(
                        errorNombre = resultado.errores.nombre,
                        errorApellido = resultado.errores.apellido,
                        errorCorreo = resultado.errores.correo
                    )
                }

                is ResultadoRegistro.Exito -> _uiState.update {
                    it.copy(registrado = true, usuarioId = resultado.usuario.id)
                }
            }
        }
    }
}