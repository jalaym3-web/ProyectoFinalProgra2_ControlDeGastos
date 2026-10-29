package com.example.aplicacioncontrolgastos.ui.gastos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacioncontrolgastos.data.local.AppDatabase
import com.example.aplicacioncontrolgastos.data.repository.MovimientoRepositoryImpl
import com.example.core.model.FiltroMovimientos
import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.core.repository.MovimientoRepository
import com.example.core.usecase.AgregarMovimientoUseCase
import com.example.core.usecase.CalcularResumenUseCase
import com.example.core.usecase.EditarMovimientoUseCase
import com.example.core.usecase.EliminarMovimientoUseCase
import com.example.core.usecase.FiltrarMovimientosUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GastosViewModel(
    private val repository: MovimientoRepository,
    private val usuarioId: Long,
    private val agregarMovimiento: AgregarMovimientoUseCase = AgregarMovimientoUseCase(repository),
    private val editarMovimiento: EditarMovimientoUseCase = EditarMovimientoUseCase(repository),
    private val eliminarMovimiento: EliminarMovimientoUseCase = EliminarMovimientoUseCase(repository),
    private val filtrarMovimientos: FiltrarMovimientosUseCase = FiltrarMovimientosUseCase(),
    private val calcularResumen: CalcularResumenUseCase = CalcularResumenUseCase()
) : ViewModel() {

    private val todos: StateFlow<List<Movimiento>> = repository.movimientosDe(usuarioId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filtro = MutableStateFlow(FiltroMovimientos.TODOS)

    val movimientos: StateFlow<List<Movimiento>> =
        combine(todos, _filtro) { lista, filtro -> filtrarMovimientos(lista, filtro) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val resumen = todos.map { calcularResumen(it) }

    val totalIngresos: StateFlow<Double> = resumen.map { it.totalIngresos }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalGastos: StateFlow<Double> = resumen.map { it.totalGastos }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val balance: StateFlow<Double> = resumen.map { it.balance }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Mensajes de error para mostrar en pantalla (Toast, Snackbar, etc.)
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun filtrar(filtro: FiltroMovimientos) {
        _filtro.value = filtro
    }

    fun agregar(descripcion: String, monto: Double, tipo: TipoMovimiento) {
        viewModelScope.launch {
            val movimiento = Movimiento(
                usuarioId = usuarioId,
                tipo = tipo,
                monto = monto,
                descripcion = descripcion,
                fecha = System.currentTimeMillis()
            )
            agregarMovimiento(movimiento).onFailure {
                _error.emit(
                    it.message ?: "Error al guardar"
                )
            }
        }
    }

    fun editar(original: Movimiento, descripcion: String, monto: Double, tipo: TipoMovimiento) {
        viewModelScope.launch {
            val editado = original.copy(descripcion = descripcion, monto = monto, tipo = tipo)
            editarMovimiento(editado).onFailure { _error.emit(it.message ?: "Error al editar") }
        }
    }

    fun eliminar(movimientoId: Long) {
        viewModelScope.launch { eliminarMovimiento(movimientoId) }
    }


    fun eliminar(movimiento: Movimiento) {
        viewModelScope.launch { repository.eliminar(movimiento.id) }
    }


    class GastosViewModelFactory(
        private val repository: MovimientoRepository,
        private val usuarioId: Long
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GastosViewModel(repository, usuarioId) as T
        }
    }
}