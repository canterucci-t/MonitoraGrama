package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.RegrasCorte
import br.com.monitoragrama.domain.repository.TrechoRepository
import java.time.LocalDate

sealed interface ResultadoMedicao {
    data object Sucesso : ResultadoMedicao
    data object AlturaInvalida : ResultadoMedicao
}

/** Valida e registra uma nova medição de altura para o trecho. */
class RegistrarMedicaoUseCase(
    private val repository: TrechoRepository,
    private val hoje: () -> LocalDate = { LocalDate.now() }
) {
    suspend operator fun invoke(trechoId: String, alturaCm: Double?): ResultadoMedicao {
        if (alturaCm == null || alturaCm <= 0.0 || alturaCm > RegrasCorte.ALTURA_MAXIMA_VALIDA_CM) {
            return ResultadoMedicao.AlturaInvalida
        }
        repository.registrarMedicao(trechoId, Medicao(hoje(), alturaCm))
        return ResultadoMedicao.Sucesso
    }
}
