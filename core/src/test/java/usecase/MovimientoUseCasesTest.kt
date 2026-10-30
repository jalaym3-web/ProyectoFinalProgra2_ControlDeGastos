package usecase

import FakeMovimientoRepository
import com.example.core.model.FiltroMovimientos
import com.example.core.model.Movimiento
import com.example.core.model.TipoMovimiento
import com.example.core.usecase.AgregarMovimientoUseCase
import com.example.core.usecase.CalcularResumenUseCase
import com.example.core.usecase.EditarMovimientoUseCase
import com.example.core.usecase.FiltrarMovimientosUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovimientoUseCasesTest {

    private val repo = FakeMovimientoRepository()

    private fun movimiento(
        tipo: TipoMovimiento = TipoMovimiento.GASTO,
        monto: Double = 100.0,
        descripcion: String = "Comida"
    ) = Movimiento(usuarioId = 1, tipo = tipo, monto = monto, descripcion = descripcion, fecha = 0L)

    @Test
    fun `agregar guarda un movimiento valido`(): Unit = runBlocking {
        val resultado = AgregarMovimientoUseCase(repo)(movimiento())
        assertTrue(resultado.isSuccess)
        assertEquals(1, repo.guardados.size)
    }

    @Test
    fun `agregar rechaza monto cero y no guarda`(): Unit = runBlocking {
        val resultado = AgregarMovimientoUseCase(repo)(movimiento(monto = 0.0))
        assertTrue(resultado.isFailure)
        assertTrue(repo.guardados.isEmpty())
    }

    @Test
    fun `agregar rechaza descripcion vacia`(): Unit = runBlocking {
        val resultado = AgregarMovimientoUseCase(repo)(movimiento(descripcion = "   "))
        assertTrue(resultado.isFailure)
        assertTrue(repo.guardados.isEmpty())
    }

    @Test
    fun `editar valido llega al repositorio`(): Unit = runBlocking {
        val resultado = EditarMovimientoUseCase(repo)(movimiento(monto = 250.0))
        assertTrue(resultado.isSuccess)
        assertEquals(1, repo.actualizados.size)
    }

    @Test
    fun `editar rechaza monto negativo`(): Unit = runBlocking {
        val resultado = EditarMovimientoUseCase(repo)(movimiento(monto = -5.0))
        assertTrue(resultado.isFailure)
        assertTrue(repo.actualizados.isEmpty())
    }

    @Test
    fun `filtrar devuelve solo los gastos`() {
        val lista = listOf(
            movimiento(tipo = TipoMovimiento.INGRESO),
            movimiento(tipo = TipoMovimiento.GASTO)
        )
        val resultado = FiltrarMovimientosUseCase()(lista, FiltroMovimientos.GASTOS)
        assertEquals(1, resultado.size)
        assertEquals(TipoMovimiento.GASTO, resultado[0].tipo)
    }

    @Test
    fun `resumen de lista vacia es cero`() {
        val resumen = CalcularResumenUseCase()(emptyList())
        assertEquals(0.0, resumen.balance, 0.001)
    }

    @Test
    fun `balance es ingresos menos gastos`() {
        val lista = listOf(
            movimiento(tipo = TipoMovimiento.INGRESO, monto = 500.0),
            movimiento(tipo = TipoMovimiento.GASTO, monto = 120.0)
        )
        assertEquals(380.0, CalcularResumenUseCase()(lista).balance, 0.001)
    }

    @Test
    fun `agregar rechaza monto NaN`(): Unit = runBlocking {
        val resultado = AgregarMovimientoUseCase(repo)(movimiento(monto = Double.NaN))
        assertTrue(resultado.isFailure)
        assertTrue(repo.guardados.isEmpty())
    }
}