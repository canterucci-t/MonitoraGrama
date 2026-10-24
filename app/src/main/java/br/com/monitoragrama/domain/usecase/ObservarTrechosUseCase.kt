package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.model.PainelTrechos
import br.com.monitoragrama.domain.model.StatusCorte
import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.domain.repository.SessaoRepository
import br.com.monitoragrama.domain.repository.TrechoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Monta o painel: calcula a previsão de cada trecho, ordena do mais urgente
 * para o menos urgente e aplica o filtro salvo nas preferências.
 */
class ObservarTrechosUseCase(
    private val trechos: TrechoRepository,
    private val sessao: SessaoRepository,
    private val preverCorte: PreverCorteUseCase
) {
    operator fun invoke(): Flow<PainelTrechos> =
        combine(trechos.observarTrechos(), sessao.observarSomenteCriticos()) { lista, somenteCriticos ->
            val monitorados = lista
                .map { TrechoMonitorado(it, preverCorte(it)) }
                .sortedBy { it.previsao?.diasParaCorte ?: Long.MAX_VALUE }

            fun contar(status: StatusCorte) = monitorados.count { it.previsao?.status == status }

            PainelTrechos(
                trechos = if (somenteCriticos) {
                    monitorados.filter { it.previsao?.status == StatusCorte.CRITICO }
                } else {
                    monitorados
                },
                totalCritico = contar(StatusCorte.CRITICO),
                totalAtencao = contar(StatusCorte.ATENCAO),
                totalOk = contar(StatusCorte.OK),
                somenteCriticos = somenteCriticos
            )
        }
}
