package br.com.monitoragrama.data.repository

import br.com.monitoragrama.data.local.TrechoComMedicoes
import br.com.monitoragrama.data.local.TrechoDao
import br.com.monitoragrama.data.local.TrechosIniciais
import br.com.monitoragrama.data.local.paraDominio
import br.com.monitoragrama.data.local.paraEntidade
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.Trecho
import kotlinx.coroutines.CancellationException
import br.com.monitoragrama.domain.repository.TrechoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Lê e grava os trechos no SQLite via Room, traduzindo entidades em modelos de domínio. */
class TrechoRepositoryImpl(private val dao: TrechoDao) : TrechoRepository {

    private val cargaInicial = Mutex()

    override fun observarTrechos(): Flow<List<Trecho>> =
        dao.observarTodos()
            .onStart { prepararDados() }
            .map { lista -> lista.map(TrechoComMedicoes::paraDominio) }

    override fun observarTrecho(id: String): Flow<Trecho?> =
        dao.observarPorId(id)
            .onStart { prepararDados() }
            .map { it?.paraDominio() }

    override suspend fun registrarMedicao(trechoId: String, medicao: Medicao): Result<Unit> =
        runCatching { dao.salvarMedicao(medicao.paraEntidade(trechoId)) }
            .onFailure { if (it is CancellationException) throw it }

    /** A consulta aguarda a carga; uma falha chega à tela e permite tentar novamente. */
    private suspend fun prepararDados() = cargaInicial.withLock {
        if (dao.contar() > 0) return@withLock
        val trechos = TrechosIniciais.lista()
        dao.salvarCarga(
            trechos = trechos.map { it.paraEntidade() },
            medicoes = trechos.flatMap { trecho -> trecho.medicoes.map { it.paraEntidade(trecho.id) } }
        )
    }
}
