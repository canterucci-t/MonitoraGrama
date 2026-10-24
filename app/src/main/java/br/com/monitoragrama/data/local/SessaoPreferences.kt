package br.com.monitoragrama.data.local

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Dados simples do app em SharedPreferences: a matrícula do operador logado e o
 * filtro do painel. O filtro também é exposto como Flow para a tela reagir à troca.
 */
class SessaoPreferences(context: Context) {

    private val preferencias = context.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)

    private val _somenteCriticos = MutableStateFlow(
        preferencias.getBoolean(SOMENTE_CRITICOS, PADRAO_SOMENTE_CRITICOS)
    )
    val somenteCriticos: StateFlow<Boolean> = _somenteCriticos.asStateFlow()

    fun matricula(): String? = preferencias.getString(MATRICULA, null)

    fun salvarMatricula(matricula: String) = preferencias.edit { putString(MATRICULA, matricula) }

    fun limparMatricula() = preferencias.edit { remove(MATRICULA) }

    fun definirSomenteCriticos(valor: Boolean) {
        preferencias.edit { putBoolean(SOMENTE_CRITICOS, valor) }
        _somenteCriticos.value = valor
    }

    private companion object {
        const val ARQUIVO = "monitoragrama"
        const val MATRICULA = "matricula"
        const val SOMENTE_CRITICOS = "somente_criticos"
        const val PADRAO_SOMENTE_CRITICOS = false
    }
}
