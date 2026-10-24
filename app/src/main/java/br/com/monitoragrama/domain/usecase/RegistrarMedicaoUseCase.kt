package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.RegrasCorte
import br.com.monitoragrama.domain.repository.TrechoRepository
import java.time.LocalDate

sealed interface ResultadoMedicao {
    data object Sucesso : ResultadoMedicao
    data object AlturaInvalida : ResultadoMedicao
    data object FalhaAoSalvar : ResultadoMedicao
}

/** Valida e registra uma nova medição de altura para o trecho. */
class RegistrarMedicaoUseCase(
    private val repository: TrechoRepository,
    private val hoje: () -> LocalDate = { LocalDate.now() }
) {
    suspend operator fun invoke(trechoId: String, alturaCm: Double?): ResultadoMedicao {
        if (alturaCm == null || !alturaCm.isFinite() || alturaCm < 0.1 ||
            alturaCm > RegrasCorte.ALTURA_MAXIMA_VALIDA_CM) {
            return ResultadoMedicao.AlturaInvalida
        }
        return repository.registrarMedicao(trechoId, Medicao(hoje(), alturaCm)).fold(
            onSuccess = { ResultadoMedicao.Sucesso },
            onFailure = { ResultadoMedicao.FalhaAoSalvar }
        )
    }
}
