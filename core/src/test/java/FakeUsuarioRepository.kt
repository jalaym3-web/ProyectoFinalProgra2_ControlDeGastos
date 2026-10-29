

import com.example.core.model.Usuario
import com.example.core.repository.CorreoDuplicadoException
import com.example.core.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// core/src/test/java/com/example/core/FakeUsuarioRepository.kt
class FakeUsuarioRepository : UsuarioRepository {
    val registrados = mutableListOf<Usuario>()

    override suspend fun registrar(nombre: String, apellido: String, correo: String): Usuario {
        if (registrados.any { it.correo == correo }) throw CorreoDuplicadoException()
        val usuario = Usuario(id = registrados.size + 1L, nombre = nombre, apellido = apellido, correo = correo)
        registrados.add(usuario)
        return usuario
    }
    override fun obtenerUsuarios(): Flow<List<Usuario>> = flowOf(registrados)
}