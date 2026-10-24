package br.com.monitoragrama.domain.usecase

import br.com.monitoragrama.domain.repository.SessaoRepository

/** Valida as credenciais e, dando certo, guarda a matrícula para a próxima abertura. */
class EntrarUseCase(
    private val validarLogin: ValidarLoginUseCase,
    private val sessao: SessaoRepository
) {
    operator fun invoke(matricula: String, senha: String): ResultadoLogin {
        val resultado = validarLogin(matricula, senha)
        if (resultado == ResultadoLogin.SUCESSO) sessao.iniciarSessao(matricula.trim())
        return resultado
    }
}

class SairUseCase(private val sessao: SessaoRepository) {
    operator fun invoke() = sessao.encerrarSessao()
}

/** Decide se o app abre direto no painel ou na tela de login. */
class SessaoAtivaUseCase(private val sessao: SessaoRepository) {
    operator fun invoke(): Boolean = sessao.matriculaLogada() != null
}

class DefinirFiltroCriticosUseCase(private val sessao: SessaoRepository) {
    operator fun invoke(valor: Boolean) = sessao.definirSomenteCriticos(valor)
}
