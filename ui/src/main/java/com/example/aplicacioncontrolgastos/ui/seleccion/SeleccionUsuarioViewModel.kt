package com.example.aplicacioncontrolgastos.ui.seleccion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.model.Usuario
import com.example.core.repository.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
class SeleccionUsuarioViewModel(repository: UsuarioRepository) : ViewModel() {
    val usuarios: StateFlow<List<Usuario>> = repository.obtenerUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

class SeleccionUsuarioViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SeleccionUsuarioViewModel(repository) as T
}