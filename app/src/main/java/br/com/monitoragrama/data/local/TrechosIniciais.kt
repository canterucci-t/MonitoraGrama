package br.com.monitoragrama.data.local

import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import java.time.LocalDate

/**
 * Carga inicial gravada no banco na primeira execução, simulando o que a câmera
 * enviou nas três últimas vistorias. As datas são relativas a hoje para que a
 * demonstração sempre mostre trechos críticos, em atenção e em dia.
 */
object TrechosIniciais {

    fun lista(hoje: LocalDate = LocalDate.now()): List<Trecho> = listOf(
        trecho("sp348-20", "SP-348 Bandeirantes", 20, 35, TipoVegetacao.BRAQUIARIA, Clima.CHUVOSO, hoje, 8.0, 16.0, 27.0),
        trecho("sp330-50", "SP-330 Anhanguera", 50, 62, TipoVegetacao.BATATAIS, Clima.TEMPERADO, hoje, 6.0, 12.0, 19.0),
        trecho("br116-210", "BR-116 Via Dutra", 210, 225, TipoVegetacao.BRAQUIARIA, Clima.TEMPERADO, hoje, 5.0, 13.0, 21.0),
        trecho("sp021-5", "SP-021 Rodoanel", 5, 12, TipoVegetacao.BATATAIS, Clima.CHUVOSO, hoje, 3.0, 5.0, 11.0),
        trecho("sp280-30", "SP-280 Castello Branco", 30, 45, TipoVegetacao.ESMERALDA, Clima.SECO, hoje, 4.0, 6.0, 9.0)
    )

    private fun trecho(
        id: String,
        rodovia: String,
        kmInicial: Int,
        kmFinal: Int,
        vegetacao: TipoVegetacao,
        clima: Clima,
        hoje: LocalDate,
        alturaVistoria1: Double,
        alturaVistoria2: Double,
        alturaVistoria3: Double
    ) = Trecho(
        id = id,
        rodovia = rodovia,
        kmInicial = kmInicial,
        kmFinal = kmFinal,
        vegetacao = vegetacao,
        clima = clima,
        medicoes = listOf(
            Medicao(hoje.minusDays(30), alturaVistoria1),
            Medicao(hoje.minusDays(16), alturaVistoria2),
            Medicao(hoje.minusDays(2), alturaVistoria3)
        )
    )
}
