package br.com.monitoragrama.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.monitoragrama.domain.model.StatusCorte
import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.domain.usecase.ObservarTrechosUseCase
import br.com.monitoragrama.ui.common.TEMPO_INSCRICAO_MS
import br.com.monitoragrama.ui.common.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val carregando: Boolean = true,
    val trechos: List<TrechoMonitorado> = emptyList(),
    val totalCritico: Int = 0,
    val totalAtencao: Int = 0,
    val totalOk: Int = 0
)

class DashboardViewModel(observarTrechos: ObservarTrechosUseCase) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = observarTrechos()
        .map { trechos ->
            fun contar(status: StatusCorte) = trechos.count { it.previsao?.status == status }
            DashboardUiState(
                carregando = false,
                trechos = trechos,
                totalCritico = contar(StatusCorte.CRITICO),
                totalAtencao = contar(StatusCorte.ATENCAO),
                totalOk = contar(StatusCorte.OK)
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TEMPO_INSCRICAO_MS), DashboardUiState())

    companion object {
        val Factory = viewModelFactory {
            initializer { DashboardViewModel(appContainer().observarTrechos) }
        }
    }
}
