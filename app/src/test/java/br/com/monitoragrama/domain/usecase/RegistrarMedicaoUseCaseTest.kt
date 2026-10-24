package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.FakeTrechoRepository
import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RegistrarMedicaoUseCaseTest {

    private val hoje = LocalDate.of(2026, 3, 10)

    private val trecho = Trecho(
        id = "sp348-20",
        rodovia = "SP-348 Bandeirantes",
        kmInicial = 20,
        kmFinal = 35,
        vegetacao = TipoVegetacao.BRAQUIARIA,
        clima = Clima.CHUVOSO,
        medicoes = emptyList()
    )

    private fun useCase(repository: FakeTrechoRepository) =
        RegistrarMedicaoUseCase(repository) { hoje }

    @Test
    fun `medicao valida e gravada com a data de hoje`() = runBlocking {
        val repository = FakeTrechoRepository(listOf(trecho))

        val resultado = useCase(repository)("sp348-20", 12.5)

        assertEquals(ResultadoMedicao.Sucesso, resultado)
        val salvas = repository.observarTrecho("sp348-20").first()?.medicoes.orEmpty()
        assertEquals(1, salvas.size)
        assertEquals(hoje, salvas.first().data)
        assertEquals(12.5, salvas.first().alturaCm, 0.001)
    }

    @Test
    fun `altura ausente e rejeitada antes de tocar o banco`() = runBlocking {
        val repository = FakeTrechoRepository(listOf(trecho))

        assertEquals(ResultadoMedicao.AlturaInvalida, useCase(repository)("sp348-20", null))
        assertEquals(0, repository.observarTrecho("sp348-20").first()?.medicoes?.size)
    }

    @Test
    fun `altura fora da faixa aceita e rejeitada`() = runBlocking {
        val repository = FakeTrechoRepository(listOf(trecho))
        val registrar = useCase(repository)

        assertEquals(ResultadoMedicao.AlturaInvalida, registrar("sp348-20", 0.0))
        assertEquals(ResultadoMedicao.AlturaInvalida, registrar("sp348-20", -3.0))
        assertEquals(ResultadoMedicao.AlturaInvalida, registrar("sp348-20", 250.0))
        assertEquals(ResultadoMedicao.AlturaInvalida, registrar("sp348-20", 0.01))
        assertEquals(ResultadoMedicao.AlturaInvalida, registrar("sp348-20", Double.NaN))
        assertEquals(ResultadoMedicao.AlturaInvalida, registrar("sp348-20", Double.POSITIVE_INFINITY))
    }

    @Test
    fun `falha do banco virou resultado tratado em vez de excecao`() = runBlocking {
        val repository = FakeTrechoRepository(listOf(trecho), falharAoSalvar = true)

        assertEquals(ResultadoMedicao.FalhaAoSalvar, useCase(repository)("sp348-20", 12.5))
    }
}
