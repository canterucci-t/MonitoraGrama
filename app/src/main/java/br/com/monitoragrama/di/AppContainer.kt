package br.com.monitoragrama.di

import br.com.monitoragrama.data.local.TrechoLocalDataSource
import br.com.monitoragrama.data.repository.TrechoRepositoryImpl
import br.com.monitoragrama.domain.repository.TrechoRepository
import br.com.monitoragrama.domain.usecase.ObservarTrechoUseCase
import br.com.monitoragrama.domain.usecase.ObservarTrechosUseCase
import br.com.monitoragrama.domain.usecase.PreverCorteUseCase
import br.com.monitoragrama.domain.usecase.RegistrarMedicaoUseCase
import br.com.monitoragrama.domain.usecase.ValidarLoginUseCase

/** Injeção de dependências manual: cria cada objeto uma única vez. */
class AppContainer {
    private val repository: TrechoRepository = TrechoRepositoryImpl(TrechoLocalDataSource())
    private val preverCorte = PreverCorteUseCase()

    val observarTrechos = ObservarTrechosUseCase(repository, preverCorte)
    val observarTrecho = ObservarTrechoUseCase(repository, preverCorte)
    val registrarMedicao = RegistrarMedicaoUseCase(repository)
    val validarLogin = ValidarLoginUseCase()
}
