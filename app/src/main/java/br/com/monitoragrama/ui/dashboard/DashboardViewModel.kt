package br.com.monitoragrama.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.model.PainelTrechos
import br.com.monitoragrama.domain.usecase.DefinirFiltroCriticosUseCase
import br.com.monitoragrama.domain.usecase.ObservarTrechosUseCase
import br.com.monitoragrama.domain.usecase.SairUseCase
import br.com.monitoragrama.ui.common.TEMPO_INSCRICAO_MS
import br.com.monitoragrama.ui.common.UiState
import br.com.monitoragrama.ui.common.appContainer
import br.com.monitoragrama.ui.common.comoUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    observarTrechos: ObservarTrechosUseCase,
    private val definirFiltroCriticos: DefinirFiltroCriticosUseCase,
    private val sair: SairUseCase
) : ViewModel() {

    /** Mudar esse contador reinscreve no banco: é o que o botão "tentar novamente" faz. */
    private val tentativa = MutableStateFlow(0)

    val uiState: StateFlow<UiState<PainelTrechos>> = tentativa
        .flatMapLatest { observarTrechos().comoUiState(R.string.erro_carregar_trechos) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TEMPO_INSCRICAO_MS), UiState.Carregando)

    fun onFiltroChange(somenteCriticos: Boolean) = definirFiltroCriticos(somenteCriticos)

    fun recarregar() = tentativa.update { it + 1 }

    fun onSair() = sair()

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                DashboardViewModel(
                    container.observarTrechos,
                    container.definirFiltroCriticos,
                    container.sair
                )
            }
        }
    }
}
