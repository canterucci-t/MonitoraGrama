package br.com.monitoragrama.data.local

import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import java.time.LocalDate

/**
 * Simula o banco de dados que recebe as medições enviadas pela câmera
 * a cada vistoria (2 semanas). As datas são relativas a hoje para que a
 * demonstração sempre mostre trechos críticos, em atenção e em dia.
 */
class TrechoLocalDataSource(private val hoje: LocalDate = LocalDate.now()) {

    fun carregarTrechos(): List<Trecho> = listOf(
        trecho("sp348-20", "SP-348 Bandeirantes", 20, 35, TipoVegetacao.BRAQUIARIA, Clima.CHUVOSO, 8.0, 16.0, 27.0),
        trecho("sp330-50", "SP-330 Anhanguera", 50, 62, TipoVegetacao.BATATAIS, Clima.TEMPERADO, 6.0, 12.0, 19.0),
        trecho("br116-210", "BR-116 Via Dutra", 210, 225, TipoVegetacao.BRAQUIARIA, Clima.TEMPERADO, 5.0, 13.0, 21.0),
        trecho("sp021-5", "SP-021 Rodoanel", 5, 12, TipoVegetacao.BATATAIS, Clima.CHUVOSO, 3.0, 5.0, 11.0),
        trecho("sp280-30", "SP-280 Castello Branco", 30, 45, TipoVegetacao.ESMERALDA, Clima.SECO, 4.0, 6.0, 9.0)
    )

    /** Cria o histórico das três últimas vistorias (há 30, 16 e 2 dias). */
    private fun trecho(
        id: String,
        rodovia: String,
        kmInicial: Int,
        kmFinal: Int,
        vegetacao: TipoVegetacao,
        clima: Clima,
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
