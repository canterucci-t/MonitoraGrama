package br.com.monitoragrama.ui.detalhe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.domain.usecase.ObservarTrechoUseCase
import br.com.monitoragrama.ui.common.TEMPO_INSCRICAO_MS
import br.com.monitoragrama.ui.common.appContainer
import br.com.monitoragrama.ui.navigation.Rotas
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DetalheUiState(
    val carregando: Boolean = true,
    val item: TrechoMonitorado? = null
)

class DetalheViewModel(
    savedStateHandle: SavedStateHandle,
    observarTrecho: ObservarTrechoUseCase
) : ViewModel() {

    val trechoId: String = checkNotNull(savedStateHandle[Rotas.ARG_TRECHO_ID])

    val uiState: StateFlow<DetalheUiState> = observarTrecho(trechoId)
        .map { DetalheUiState(carregando = false, item = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TEMPO_INSCRICAO_MS), DetalheUiState())

    companion object {
        val Factory = viewModelFactory {
            initializer { DetalheViewModel(createSavedStateHandle(), appContainer().observarTrecho) }
        }
    }
}
