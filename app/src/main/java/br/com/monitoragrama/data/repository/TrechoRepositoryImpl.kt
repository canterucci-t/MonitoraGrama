package br.com.monitoragrama.data.repository

import br.com.monitoragrama.data.local.TrechoLocalDataSource
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.Trecho
import br.com.monitoragrama.domain.repository.TrechoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Repositório em memória: mantém o estado e notifica a UI via Flow. */
class TrechoRepositoryImpl(dataSource: TrechoLocalDataSource) : TrechoRepository {

    private val trechos = MutableStateFlow(dataSource.carregarTrechos())

    override fun observarTrechos(): Flow<List<Trecho>> = trechos.asStateFlow()

    override fun observarTrecho(id: String): Flow<Trecho?> =
        trechos.map { lista -> lista.find { it.id == id } }

    override suspend fun registrarMedicao(trechoId: String, medicao: Medicao) {
        trechos.update { lista ->
            lista.map { trecho ->
                if (trecho.id != trechoId) {
                    trecho
                } else {
                    // Uma medição por dia: a nova substitui a do mesmo dia.
                    val historico = trecho.medicoes.filterNot { it.data == medicao.data } + medicao
                    trecho.copy(medicoes = historico.sortedBy { it.data })
                }
            }
        }
    }
}
