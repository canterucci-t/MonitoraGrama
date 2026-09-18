package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.domain.repository.TrechoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Lista os trechos com previsão, ordenados do mais urgente para o menos urgente. */
class ObservarTrechosUseCase(
    private val repository: TrechoRepository,
    private val preverCorte: PreverCorteUseCase
) {
    operator fun invoke(): Flow<List<TrechoMonitorado>> =
        repository.observarTrechos().map { trechos ->
            trechos
                .map { TrechoMonitorado(it, preverCorte(it)) }
                .sortedBy { it.previsao?.diasParaCorte ?: Long.MAX_VALUE }
        }
}
