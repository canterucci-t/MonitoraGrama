package br.com.monitoragrama.data.repository

import br.com.monitoragrama.data.local.SessaoPreferences
import br.com.monitoragrama.domain.repository.SessaoRepository
import kotlinx.coroutines.flow.Flow

/** Expõe as SharedPreferences ao domínio pelo contrato de sessão. */
class SessaoRepositoryImpl(private val preferencias: SessaoPreferences) : SessaoRepository {

    override fun matriculaLogada(): String? = preferencias.matricula()

    override fun iniciarSessao(matricula: String) = preferencias.salvarMatricula(matricula)

    override fun encerrarSessao() = preferencias.limparMatricula()

    override fun observarSomenteCriticos(): Flow<Boolean> = preferencias.somenteCriticos

    override fun definirSomenteCriticos(valor: Boolean) = preferencias.definirSomenteCriticos(valor)
}
