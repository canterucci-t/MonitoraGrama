package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.model.PrevisaoCorte
import br.com.monitoragrama.domain.model.RegrasCorte
import br.com.monitoragrama.domain.model.StatusCorte
import br.com.monitoragrama.domain.model.Trecho
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

/**
 * Previsão inteligente de corte: parte da última medição e projeta o crescimento
 * usando a taxa da espécie ajustada pelo clima da região.
 */
class PreverCorteUseCase(
    private val hoje: () -> LocalDate = { LocalDate.now() }
) {
    operator fun invoke(trecho: Trecho): PrevisaoCorte? {
        val ultima = trecho.medicoes.maxByOrNull { it.data } ?: return null
        val dataHoje = hoje()

        val taxa = trecho.vegetacao.taxaCrescimentoCmDia * trecho.clima.fatorCrescimento
        val diasDesdeMedicao = ChronoUnit.DAYS.between(ultima.data, dataHoje).coerceAtLeast(0)
        val alturaEstimada = ultima.alturaCm + taxa * diasDesdeMedicao

        val diasParaCorte = if (alturaEstimada >= RegrasCorte.ALTURA_LIMITE_CM) {
            0L
        } else {
            ceil((RegrasCorte.ALTURA_LIMITE_CM - alturaEstimada) / taxa).toLong()
        }

        val status = when {
            diasParaCorte == 0L -> StatusCorte.CRITICO
            diasParaCorte <= RegrasCorte.CICLO_VISTORIA_DIAS -> StatusCorte.ATENCAO
            else -> StatusCorte.OK
        }

        return PrevisaoCorte(
            alturaEstimadaCm = alturaEstimada,
            taxaCrescimentoCmDia = taxa,
            diasParaCorte = diasParaCorte,
            dataProximoCorte = dataHoje.plusDays(diasParaCorte),
            status = status
        )
    }
}
