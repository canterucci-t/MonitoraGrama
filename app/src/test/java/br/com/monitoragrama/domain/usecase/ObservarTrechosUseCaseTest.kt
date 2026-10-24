package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.FakeSessaoRepository
import br.com.monitoragrama.domain.FakeTrechoRepository
import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservarTrechosUseCaseTest {

    private val hoje = LocalDate.of(2026, 3, 10)

    /** Braquiária em clima chuvoso já acima do limite: entra como crítico. */
    private val critico = trecho("critico", TipoVegetacao.BRAQUIARIA, Clima.CHUVOSO, 32.0)

    /** Grama esmeralda em clima seco e baixa: ainda longe do corte. */
    private val emDia = trecho("em-dia", TipoVegetacao.ESMERALDA, Clima.SECO, 4.0)

    private fun trecho(id: String, vegetacao: TipoVegetacao, clima: Clima, altura: Double) = Trecho(
        id = id,
        rodovia = "SP-000",
        kmInicial = 0,
        kmFinal = 10,
        vegetacao = vegetacao,
        clima = clima,
        medicoes = listOf(Medicao(hoje, altura))
    )

    private fun useCase(somenteCriticos: Boolean) = ObservarTrechosUseCase(
        trechos = FakeTrechoRepository(listOf(emDia, critico)),
        sessao = FakeSessaoRepository(somenteCriticos = somenteCriticos),
        preverCorte = PreverCorteUseCase { hoje }
    )

    @Test
    fun `painel ordena do mais urgente para o menos urgente`() = runBlocking {
        val painel = useCase(somenteCriticos = false)().first()

        assertEquals(listOf("critico", "em-dia"), painel.trechos.map { it.trecho.id })
    }

    @Test
    fun `painel conta os totais por status`() = runBlocking {
        val painel = useCase(somenteCriticos = false)().first()

        assertEquals(1, painel.totalCritico)
        assertEquals(1, painel.totalOk)
        assertEquals(0, painel.totalAtencao)
    }

    @Test
    fun `filtro salvo nas preferencias esconde os trechos em dia sem mudar os totais`() = runBlocking {
        val painel = useCase(somenteCriticos = true)().first()

        assertEquals(listOf("critico"), painel.trechos.map { it.trecho.id })
        assertEquals(1, painel.totalOk)
    }

    @Test
    fun `painel vazio quando nao ha trechos salvos`() = runBlocking {
        val useCase = ObservarTrechosUseCase(
            trechos = FakeTrechoRepository(),
            sessao = FakeSessaoRepository(),
            preverCorte = PreverCorteUseCase { hoje }
        )

        val painel = useCase().first()

        assertEquals(emptyList<String>(), painel.trechos.map { it.trecho.id })
        assertEquals(0, painel.totalCritico)
    }
}
