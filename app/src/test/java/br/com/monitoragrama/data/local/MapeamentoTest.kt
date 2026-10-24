package br.com.monitoragrama.data.local

import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** Garante que o que sai do banco chega íntegro ao domínio e vice-versa. */
class MapeamentoTest {

    private val entidade = TrechoEntity(
        id = "sp348-20",
        rodovia = "SP-348 Bandeirantes",
        kmInicial = 20,
        kmFinal = 35,
        vegetacao = TipoVegetacao.BRAQUIARIA,
        clima = Clima.CHUVOSO
    )

    @Test
    fun `junção do banco virou trecho de dominio com historico em ordem cronologica`() {
        val junto = TrechoComMedicoes(
            trecho = entidade,
            medicoes = listOf(
                MedicaoEntity("sp348-20", LocalDate.of(2026, 3, 10), 18.0),
                MedicaoEntity("sp348-20", LocalDate.of(2026, 1, 5), 7.0)
            )
        )

        val trecho = junto.paraDominio()

        assertEquals("sp348-20", trecho.id)
        assertEquals(TipoVegetacao.BRAQUIARIA, trecho.vegetacao)
        assertEquals(35, trecho.kmFinal)
        assertEquals(listOf(7.0, 18.0), trecho.medicoes.map { it.alturaCm })
    }

    @Test
    fun `trecho de dominio virou entidade mantendo o vinculo da medicao`() {
        val trecho = Trecho(
            id = "sp330-50",
            rodovia = "SP-330 Anhanguera",
            kmInicial = 50,
            kmFinal = 62,
            vegetacao = TipoVegetacao.BATATAIS,
            clima = Clima.SECO,
            medicoes = listOf(Medicao(LocalDate.of(2026, 2, 1), 9.5))
        )

        assertEquals("sp330-50", trecho.paraEntidade().id)
        assertEquals(Clima.SECO, trecho.paraEntidade().clima)

        val medicao = trecho.medicoes.first().paraEntidade(trecho.id)
        assertEquals("sp330-50", medicao.trechoId)
        assertEquals(9.5, medicao.alturaCm, 0.001)
    }

    @Test
    fun `data vai e volta do formato guardado no sqlite`() {
        val conversores = Conversores()
        val data = LocalDate.of(2026, 3, 10)

        assertEquals(data, conversores.paraData(conversores.paraDia(data)))
        assertEquals(null, conversores.paraDia(null))
        assertEquals(null, conversores.paraData(null))
    }
}
