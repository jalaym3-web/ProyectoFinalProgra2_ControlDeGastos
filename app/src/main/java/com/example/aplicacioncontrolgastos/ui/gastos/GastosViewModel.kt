package com.example.aplicacioncontrolgastos.ui.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacioncontrolgastos.data.local.MovimientoEntity
import com.example.aplicacioncontrolgastos.data.repository.MovimientoRepository
import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FiltroMovimientos { TODOS, GASTOS, INGRESOS }

class GastosViewModel(
    private val repository: MovimientoRepository,
    private val usuarioId: Long
) : ViewModel() {

    private val todos: StateFlow<List<Movimiento>> = repository.movimientosDe(usuarioId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filtro = MutableStateFlow(FiltroMovimientos.TODOS)

    // Lista que ve la pantalla, según la pestaña elegida
    val movimientos: StateFlow<List<Movimiento>> = combine(todos, _filtro) { lista, filtro ->
        when (filtro) {
            FiltroMovimientos.TODOS -> lista
            FiltroMovimientos.GASTOS -> lista.filter { it.tipo == TipoMovimiento.GASTO }
            FiltroMovimientos.INGRESOS -> lista.filter { it.tipo == TipoMovimiento.INGRESO }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIngresos: StateFlow<Double> = todos
        .map { l -> l.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalGastos: StateFlow<Double> = todos
        .map { l -> l.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val balance: StateFlow<Double> = combine(totalIngresos, totalGastos) { i, g -> i - g }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun filtrar(filtro: FiltroMovimientos) { _filtro.value = filtro }
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


    fun eliminar(movimiento: Movimiento) {
        viewModelScope.launch { repository.eliminar(movimiento.id) }
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