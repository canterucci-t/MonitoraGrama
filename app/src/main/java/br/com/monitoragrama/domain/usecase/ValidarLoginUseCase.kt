package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.model.RegrasCorte

enum class ResultadoLogin { SUCESSO, CAMPOS_VAZIOS, TAMANHO_INVALIDO }

/** Validação simples de acesso do operador (MVP sem backend). */
class ValidarLoginUseCase {
    operator fun invoke(matricula: String, senha: String): ResultadoLogin = when {
        matricula.isBlank() || senha.isBlank() -> ResultadoLogin.CAMPOS_VAZIOS
        matricula.trim().length < RegrasCorte.TAMANHO_MINIMO_CREDENCIAL ||
            senha.length < RegrasCorte.TAMANHO_MINIMO_CREDENCIAL -> ResultadoLogin.TAMANHO_INVALIDO
        else -> ResultadoLogin.SUCESSO
    }
}
