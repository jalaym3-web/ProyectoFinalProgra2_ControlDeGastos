package usecase

import FakeUsuarioRepository
import com.example.core.usecase.RegistrarUsuarioUseCase
import com.example.core.usecase.ResultadoRegistro
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistrarUsuarioUseCaseTest {
    private val repo = FakeUsuarioRepository()
    private val useCase = RegistrarUsuarioUseCase(repo)

    @Test
    fun `correo invalido devuelve error y no guarda`(): Unit = runBlocking {
        val resultado = useCase("Ana", "López", "ana@")
        assertTrue(resultado is ResultadoRegistro.Invalido)
        assertTrue(repo.registrados.isEmpty())
    }

    @Test
    fun `datos validos registran al usuario`(): Unit = runBlocking {
        val resultado = useCase("Ana", "López", "ana@correo.com")
        assertTrue(resultado is ResultadoRegistro.Exito)
        assertEquals(1, repo.registrados.size)
    }

    @Test
    fun `correo repetido devuelve error y no duplica`(): Unit = runBlocking {
        useCase("Ana", "López", "ana@correo.com")
        val resultado = useCase("Luis", "Pérez", "ANA@correo.com")
        assertTrue(resultado is ResultadoRegistro.Invalido)
        assertEquals(1, repo.registrados.size)
    }
}