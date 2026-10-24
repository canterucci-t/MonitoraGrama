package br.com.monitoragrama.ui.common

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/** Os três estados que as telas do app podem assumir. */
sealed interface UiState<out T> {
    data object Carregando : UiState<Nothing>
    data class Sucesso<T>(val dados: T) : UiState<T>
    data class Erro(@param:StringRes val mensagem: Int) : UiState<Nothing>
}

/**
 * Converte um Flow de dados em Flow de estado. Uma exceção vinda do banco vira
 * estado de erro na tela em vez de propagar e encerrar o app.
 */
fun <T> Flow<T>.comoUiState(@StringRes mensagemErro: Int): Flow<UiState<T>> =
    map<T, UiState<T>> { UiState.Sucesso(it) }
        .onStart { emit(UiState.Carregando) }
        .catch { emit(UiState.Erro(mensagemErro)) }
