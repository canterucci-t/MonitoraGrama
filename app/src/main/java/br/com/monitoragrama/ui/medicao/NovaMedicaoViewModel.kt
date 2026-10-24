package br.com.monitoragrama.ui.medicao

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.model.Trecho
import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.domain.usecase.ObservarTrechoUseCase
import br.com.monitoragrama.domain.usecase.RegistrarMedicaoUseCase
import br.com.monitoragrama.domain.usecase.ResultadoMedicao
import br.com.monitoragrama.navigation.Rotas
import br.com.monitoragrama.ui.common.TEMPO_INSCRICAO_MS
import br.com.monitoragrama.ui.common.UiState
import br.com.monitoragrama.ui.common.appContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NovaMedicaoUiState(
    val alturaTexto: String = "",
    @param:StringRes val erro: Int? = null,
    val salvando: Boolean = false,
    val salvo: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class NovaMedicaoViewModel(
    savedStateHandle: SavedStateHandle,
    observarTrecho: ObservarTrechoUseCase,
    private val registrarMedicao: RegistrarMedicaoUseCase
) : ViewModel() {

    private val trechoId: String = checkNotNull(savedStateHandle[Rotas.ARG_TRECHO_ID])

    private val tentativa = MutableStateFlow(0)

    val trecho: StateFlow<UiState<Trecho>> = tentativa
        .flatMapLatest {
            observarTrecho(trechoId)
                .map<TrechoMonitorado?, UiState<Trecho>> { item ->
                    if (item == null) UiState.Erro(R.string.detalhe_nao_encontrado)
                    else UiState.Sucesso(item.trecho)
                }
                .onStart { emit(UiState.Carregando) }
                .catch { emit(UiState.Erro(R.string.erro_carregar_trecho)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TEMPO_INSCRICAO_MS), UiState.Carregando)

    private val _uiState = MutableStateFlow(NovaMedicaoUiState())
    val uiState: StateFlow<NovaMedicaoUiState> = _uiState.asStateFlow()

    fun onAlturaChange(valor: String) {
        _uiState.update { it.copy(alturaTexto = valor, erro = null) }
    }

    fun recarregar() = tentativa.update { it + 1 }

    fun salvar() {
        if (_uiState.value.salvando || _uiState.value.salvo) return
        if (trecho.value !is UiState.Sucesso) return
        val altura = _uiState.value.alturaTexto.trim().replace(',', '.').toDoubleOrNull()
        _uiState.update { it.copy(salvando = true, erro = null) }
        viewModelScope.launch {
            val erro = when (registrarMedicao(trechoId, altura)) {
                ResultadoMedicao.Sucesso -> null
                ResultadoMedicao.AlturaInvalida -> R.string.medicao_erro_altura
                ResultadoMedicao.FalhaAoSalvar -> R.string.erro_salvar_medicao
            }
            _uiState.update { it.copy(salvando = false, erro = erro, salvo = erro == null) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                NovaMedicaoViewModel(
                    createSavedStateHandle(),
                    container.observarTrecho,
                    container.registrarMedicao
                )
            }
        }
    }
}
