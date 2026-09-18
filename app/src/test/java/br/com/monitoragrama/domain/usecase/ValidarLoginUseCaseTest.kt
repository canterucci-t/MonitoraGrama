package br.com.monitoragrama.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class ValidarLoginUseCaseTest {

    private val validar = ValidarLoginUseCase()

    @Test
    fun `campos vazios sao rejeitados`() {
        assertEquals(ResultadoLogin.CAMPOS_VAZIOS, validar("", "1234"))
    }

    @Test
    fun `credenciais curtas sao rejeitadas`() {
        assertEquals(ResultadoLogin.TAMANHO_INVALIDO, validar("12", "1234"))
    }

    @Test
    fun `credenciais validas sao aceitas`() {
        assertEquals(ResultadoLogin.SUCESSO, validar("rm12345", "senha"))
    }
}
