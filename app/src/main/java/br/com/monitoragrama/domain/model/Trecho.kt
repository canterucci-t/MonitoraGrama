package br.com.monitoragrama.domain.model

import java.time.LocalDate

/** Trecho de rodovia monitorado pela câmera de visão computacional. */
data class Trecho(
    val id: String,
    val rodovia: String,
    val kmInicial: Int,
    val kmFinal: Int,
    val vegetacao: TipoVegetacao,
    val clima: Clima,
    val medicoes: List<Medicao>
)

/** Altura da grama medida em uma vistoria. */
data class Medicao(
    val data: LocalDate,
    val alturaCm: Double
)

/** Espécie de vegetação: cada uma cresce em um ritmo diferente. */
enum class TipoVegetacao(val taxaCrescimentoCmDia: Double) {
    BRAQUIARIA(1.2),
    BATATAIS(0.8),
    ESMERALDA(0.5)
}

/** Condição climática da região: acelera ou desacelera o crescimento. */
enum class Clima(val fatorCrescimento: Double) {
    CHUVOSO(1.4),
    TEMPERADO(1.0),
    SECO(0.6)
}
