package com.example.aplicacioncontrolgastos.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacioncontrolgastos.data.repository.UsuarioRepositoryImpl

class RegistroViewModelFactory(
    private val repository: UsuarioRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        RegistroViewModel(repository) as T
}