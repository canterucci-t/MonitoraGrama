package br.com.monitoragrama.ui.common

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import br.com.monitoragrama.MonitoraGramaApp
import br.com.monitoragrama.di.AppContainer

/** Acesso ao container de dependências dentro das fábricas de ViewModel. */
fun CreationExtras.appContainer(): AppContainer =
    (checkNotNull(this[APPLICATION_KEY]) as MonitoraGramaApp).container

/** Mantém o Flow ativo por 5 s após a tela sair (ex.: rotação), evitando recarregar. */
const val TEMPO_INSCRICAO_MS = 5_000L
