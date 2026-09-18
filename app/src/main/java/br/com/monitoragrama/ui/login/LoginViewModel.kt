package br.com.monitoragrama.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.usecase.ResultadoLogin
import br.com.monitoragrama.domain.usecase.ValidarLoginUseCase
import br.com.monitoragrama.ui.common.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val matricula: String = "",
    val senha: String = "",
    @param:StringRes val erro: Int? = null,
    val autenticado: Boolean = false
)

class LoginViewModel(private val validarLogin: ValidarLoginUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onMatriculaChange(valor: String) = _uiState.update { it.copy(matricula = valor, erro = null) }

    fun onSenhaChange(valor: String) = _uiState.update { it.copy(senha = valor, erro = null) }

    fun entrar() {
        val estado = _uiState.value
        val erro = when (validarLogin(estado.matricula, estado.senha)) {
            ResultadoLogin.SUCESSO -> null
            ResultadoLogin.CAMPOS_VAZIOS -> R.string.login_erro_campos
            ResultadoLogin.TAMANHO_INVALIDO -> R.string.login_erro_tamanho
        }
        _uiState.update { it.copy(erro = erro, autenticado = erro == null) }
    }

    /** Consome o evento de navegação para que não dispare novamente. */
    fun onNavegacaoConcluida() = _uiState.update { it.copy(autenticado = false) }

    companion object {
        val Factory = viewModelFactory {
            initializer { LoginViewModel(appContainer().validarLogin) }
        }
    }
}
