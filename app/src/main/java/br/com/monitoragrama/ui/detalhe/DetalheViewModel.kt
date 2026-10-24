package br.com.monitoragrama.ui.detalhe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.domain.usecase.ObservarTrechoUseCase
import br.com.monitoragrama.navigation.Rotas
import br.com.monitoragrama.ui.common.TEMPO_INSCRICAO_MS
import br.com.monitoragrama.ui.common.UiState
import br.com.monitoragrama.ui.common.appContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class DetalheViewModel(
    savedStateHandle: SavedStateHandle,
    observarTrecho: ObservarTrechoUseCase
) : ViewModel() {

    private val trechoId: String = checkNotNull(savedStateHandle[Rotas.ARG_TRECHO_ID])

    private val tentativa = MutableStateFlow(0)

    val uiState: StateFlow<UiState<TrechoMonitorado>> = tentativa
        .flatMapLatest {
            observarTrecho(trechoId)
                .map<TrechoMonitorado?, UiState<TrechoMonitorado>> { item ->
                    if (item == null) UiState.Erro(R.string.detalhe_nao_encontrado) else UiState.Sucesso(item)
                }
                .onStart { emit(UiState.Carregando) }
                .catch { emit(UiState.Erro(R.string.erro_carregar_trecho)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TEMPO_INSCRICAO_MS), UiState.Carregando)

    fun recarregar() = tentativa.update { it + 1 }

    companion object {
        val Factory = viewModelFactory {
            initializer { DetalheViewModel(createSavedStateHandle(), appContainer().observarTrecho) }
        }
    }
}
