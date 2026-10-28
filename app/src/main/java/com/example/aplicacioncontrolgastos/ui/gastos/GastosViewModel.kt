package com.example.aplicacioncontrolgastos.ui.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacioncontrolgastos.data.local.MovimientoEntity
import com.example.aplicacioncontrolgastos.data.repository.MovimientoRepository
import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GastosViewModel(
    private val repository: MovimientoRepository,
    private val usuarioId: Long
) : ViewModel() {

    val movimientos: StateFlow<List<Movimiento>> = repository.movimientosDe(usuarioId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val balance: StateFlow<Double> = movimientos
        .map { lista ->
            lista.sumOf { if (it.tipo == TipoMovimiento.INGRESO) it.monto else -it.monto }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun agregar(descripcion: String, monto: Double, tipo: TipoMovimiento) {
        viewModelScope.launch {
            repository.agregar(
                Movimiento(
                    usuarioId = usuarioId,
                    tipo = tipo,
                    monto = monto,
                    descripcion = descripcion,
                    fecha = System.currentTimeMillis()
                )
            )
        }
    }
}

class GastosViewModelFactory(
    private val repository: MovimientoRepository,
    private val usuarioId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        GastosViewModel(repository, usuarioId) as T
}