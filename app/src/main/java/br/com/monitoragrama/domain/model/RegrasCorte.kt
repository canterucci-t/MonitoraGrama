package br.com.monitoragrama.domain.model

/** Parâmetros de negócio definidos na proposta do projeto. */
object RegrasCorte {
    /** Altura máxima permitida antes do corte. */
    const val ALTURA_LIMITE_CM = 30.0

    /** Intervalo entre vistorias do carro com câmera (2 semanas). */
    const val CICLO_VISTORIA_DIAS = 14L

    /** Maior altura aceita em uma medição (filtra leituras inválidas). */
    const val ALTURA_MAXIMA_VALIDA_CM = 200.0

    const val TAMANHO_MINIMO_CREDENCIAL = 4
}
