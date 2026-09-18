package br.com.monitoragrama.domain.repository

import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.Trecho
import kotlinx.coroutines.flow.Flow

/** Contrato de acesso aos dados; a implementação fica na camada data. */
interface TrechoRepository {
    fun observarTrechos(): Flow<List<Trecho>>
    fun observarTrecho(id: String): Flow<Trecho?>
    suspend fun registrarMedicao(trechoId: String, medicao: Medicao)
}
