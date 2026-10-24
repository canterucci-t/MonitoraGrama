package br.com.monitoragrama.domain.repository

import kotlinx.coroutines.flow.Flow

/** Contrato dos dados simples que o app guarda entre execuções. */
interface SessaoRepository {
    fun matriculaLogada(): String?
    fun iniciarSessao(matricula: String)
    fun encerrarSessao()
    fun observarSomenteCriticos(): Flow<Boolean>
    fun definirSomenteCriticos(valor: Boolean)
}
