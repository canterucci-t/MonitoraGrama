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
import br.com.monitoragrama.domain.usecase.ObservarTrechoUseCase
import br.com.monitoragrama.domain.usecase.RegistrarMedicaoUseCase
import br.com.monitoragrama.domain.usecase.ResultadoMedicao
import br.com.monitoragrama.ui.common.TEMPO_INSCRICAO_MS
import br.com.monitoragrama.ui.common.appContainer
import br.com.monitoragrama.ui.navigation.Rotas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NovaMedicaoUiState(
    val alturaTexto: String = "",
    @param:StringRes val erro: Int? = null,
    val salvando: Boolean = false,
    val salvo: Boolean = false
)

class NovaMedicaoViewModel(
    savedStateHandle: SavedStateHandle,
    observarTrecho: ObservarTrechoUseCase,
    private val registrarMedicao: RegistrarMedicaoUseCase
) : ViewModel() {

    private val trechoId: String = checkNotNull(savedStateHandle[Rotas.ARG_TRECHO_ID])

    val trecho: StateFlow<Trecho?> = observarTrecho(trechoId)
        .map { it?.trecho }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TEMPO_INSCRICAO_MS), null)

    private val _uiState = MutableStateFlow(NovaMedicaoUiState())
    val uiState: StateFlow<NovaMedicaoUiState> = _uiState.asStateFlow()

    fun onAlturaChange(valor: String) {
        val filtrado = valor.filter { it.isDigit() || it == ',' || it == '.' }
        _uiState.update { it.copy(alturaTexto = filtrado, erro = null) }
    }

    fun salvar() {
        if (_uiState.value.salvando) return
        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            val altura = _uiState.value.alturaTexto.replace(',', '.').toDoubleOrNull()
            when (registrarMedicao(trechoId, altura)) {
                ResultadoMedicao.Sucesso -> _uiState.update { it.copy(salvando = false, salvo = true) }
                ResultadoMedicao.AlturaInvalida -> _uiState.update {
                    it.copy(salvando = false, erro = R.string.medicao_erro_altura)
                }
            }
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
