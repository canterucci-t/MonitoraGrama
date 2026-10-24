package br.com.monitoragrama.di

import android.content.Context
import br.com.monitoragrama.data.local.AppDatabase
import br.com.monitoragrama.data.local.SessaoPreferences
import br.com.monitoragrama.data.repository.SessaoRepositoryImpl
import br.com.monitoragrama.data.repository.TrechoRepositoryImpl
import br.com.monitoragrama.domain.repository.SessaoRepository
import br.com.monitoragrama.domain.usecase.DefinirFiltroCriticosUseCase
import br.com.monitoragrama.domain.usecase.EntrarUseCase
import br.com.monitoragrama.domain.usecase.ObservarTrechoUseCase
import br.com.monitoragrama.domain.usecase.ObservarTrechosUseCase
import br.com.monitoragrama.domain.usecase.PreverCorteUseCase
import br.com.monitoragrama.domain.usecase.RegistrarMedicaoUseCase
import br.com.monitoragrama.domain.usecase.SairUseCase
import br.com.monitoragrama.domain.usecase.SessaoAtivaUseCase
import br.com.monitoragrama.domain.usecase.ValidarLoginUseCase

/** Injeção de dependências manual: cria cada objeto uma única vez. */
class AppContainer(context: Context) {

    private val dao = AppDatabase.criar(context).trechoDao()
    private val trechos = TrechoRepositoryImpl(dao)
    private val sessao: SessaoRepository = SessaoRepositoryImpl(SessaoPreferences(context))
    private val preverCorte = PreverCorteUseCase()

    val observarTrechos = ObservarTrechosUseCase(trechos, sessao, preverCorte)
    val observarTrecho = ObservarTrechoUseCase(trechos, preverCorte)
    val registrarMedicao = RegistrarMedicaoUseCase(trechos)
    val entrar = EntrarUseCase(ValidarLoginUseCase(), sessao)
    val sair = SairUseCase(sessao)
    val sessaoAtiva = SessaoAtivaUseCase(sessao)
    val definirFiltroCriticos = DefinirFiltroCriticosUseCase(sessao)

}
