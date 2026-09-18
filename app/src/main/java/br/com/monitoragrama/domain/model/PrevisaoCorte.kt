package br.com.monitoragrama.domain.model

import java.time.LocalDate

enum class StatusCorte { OK, ATENCAO, CRITICO }

/** Resultado do algoritmo de previsão para um trecho. */
data class PrevisaoCorte(
    val alturaEstimadaCm: Double,
    val taxaCrescimentoCmDia: Double,
    val diasParaCorte: Long,
    val dataProximoCorte: LocalDate,
    val status: StatusCorte
)

/** Trecho acompanhado da sua previsão (null quando ainda não há medições). */
data class TrechoMonitorado(
    val trecho: Trecho,
    val previsao: PrevisaoCorte?
)
