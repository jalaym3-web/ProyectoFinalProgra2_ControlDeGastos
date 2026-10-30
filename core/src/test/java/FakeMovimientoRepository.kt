import com.example.core.model.Movimiento
import com.example.core.repository.MovimientoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeMovimientoRepository : MovimientoRepository {
    val guardados = mutableListOf<Movimiento>()
    val actualizados = mutableListOf<Movimiento>()

    override suspend fun agregar(movimiento: Movimiento) { guardados.add(movimiento) }
    override suspend fun actualizar(movimiento: Movimiento) { actualizados.add(movimiento) }
    override suspend fun eliminar(movimientoId: Long) {}
    override fun movimientosDe(usuarioId: Long): Flow<List<Movimiento>> = flowOf(guardados)
}