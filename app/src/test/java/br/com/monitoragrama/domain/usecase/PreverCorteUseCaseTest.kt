package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.StatusCorte
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class PreverCorteUseCaseTest {

    private val hoje = LocalDate.of(2026, 9, 18)
    private val prever = PreverCorteUseCase { hoje }

    private fun trecho(alturaCm: Double, diasAtras: Long, vegetacao: TipoVegetacao, clima: Clima) = Trecho(
        id = "t",
        rodovia = "SP-000",
        kmInicial = 0,
        kmFinal = 1,
        vegetacao = vegetacao,
        clima = clima,
        medicoes = listOf(Medicao(hoje.minusDays(diasAtras), alturaCm))
    )

    @Test
    fun `acima de 30 cm o status e critico com corte hoje`() {
        // 27 cm + 1,68 cm/dia * 2 dias = 30,36 cm
        val previsao = prever(trecho(27.0, 2, TipoVegetacao.BRAQUIARIA, Clima.CHUVOSO))!!
        assertEquals(StatusCorte.CRITICO, previsao.status)
        assertEquals(0L, previsao.diasParaCorte)
        assertEquals(hoje, previsao.dataProximoCorte)
    }

    @Test
    fun `corte antes da proxima vistoria gera atencao`() {
        // 19 cm + 0,8 * 2 = 20,6 cm -> faltam 9,4 cm -> 12 dias
        val previsao = prever(trecho(19.0, 2, TipoVegetacao.BATATAIS, Clima.TEMPERADO))!!
        assertEquals(StatusCorte.ATENCAO, previsao.status)
        assertEquals(12L, previsao.diasParaCorte)
    }

    @Test
    fun `crescimento lento em clima seco fica em dia`() {
        val previsao = prever(trecho(9.0, 2, TipoVegetacao.ESMERALDA, Clima.SECO))!!
        assertEquals(StatusCorte.OK, previsao.status)
        assertEquals(0.3, previsao.taxaCrescimentoCmDia, 0.0001)
    }

    @Test
    fun `trecho sem medicoes nao tem previsao`() {
        val semMedicao = trecho(0.0, 0, TipoVegetacao.BATATAIS, Clima.SECO).copy(medicoes = emptyList())
        assertNull(prever(semMedicao))
    }
}
