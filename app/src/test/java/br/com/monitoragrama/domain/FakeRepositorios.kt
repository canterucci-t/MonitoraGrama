package br.com.monitoragrama.domain

import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.Trecho
import br.com.monitoragrama.domain.repository.SessaoRepository
import br.com.monitoragrama.domain.repository.TrechoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Dublês dos repositórios: permitem testar as regras de negócio sem Android,
 * sem banco e sem emulador. É o ganho prático do desacoplamento por interfaces.
 */
class FakeTrechoRepository(
    trechos: List<Trecho> = emptyList(),
    private val falharAoSalvar: Boolean = false
) : TrechoRepository {

    private val estado = MutableStateFlow(trechos)

    override fun observarTrechos(): Flow<List<Trecho>> = estado

    override fun observarTrecho(id: String): Flow<Trecho?> =
        estado.map { lista -> lista.find { it.id == id } }

    override suspend fun registrarMedicao(trechoId: String, medicao: Medicao): Result<Unit> {
        if (falharAoSalvar) return Result.failure(IllegalStateException("banco indisponível"))
        estado.value = estado.value.map { trecho ->
            if (trecho.id == trechoId) trecho.copy(medicoes = trecho.medicoes + medicao) else trecho
        }
        return Result.success(Unit)
    }
}

class FakeSessaoRepository(
    private var matricula: String? = null,
    somenteCriticos: Boolean = false
) : SessaoRepository {

    private val filtro = MutableStateFlow(somenteCriticos)

    override fun matriculaLogada(): String? = matricula

    override fun iniciarSessao(matricula: String) {
        this.matricula = matricula
    }

    override fun encerrarSessao() {
        matricula = null
    }

    override fun observarSomenteCriticos(): Flow<Boolean> = filtro

    override fun definirSomenteCriticos(valor: Boolean) {
        filtro.value = valor
    }
}
