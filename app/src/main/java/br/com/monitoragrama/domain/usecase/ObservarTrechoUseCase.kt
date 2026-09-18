package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.domain.repository.TrechoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObservarTrechoUseCase(
    private val repository: TrechoRepository,
    private val preverCorte: PreverCorteUseCase
) {
    operator fun invoke(id: String): Flow<TrechoMonitorado?> =
        repository.observarTrecho(id).map { trecho ->
            trecho?.let { TrechoMonitorado(it, preverCorte(it)) }
        }
}
