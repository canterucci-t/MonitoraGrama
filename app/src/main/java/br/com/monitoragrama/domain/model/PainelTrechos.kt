package br.com.monitoragrama.domain.model

/**
 * Tudo que o painel precisa exibir, já calculado no domínio: a lista visível
 * (afetada pelo filtro) e os totais por status, que contam todos os trechos.
 */
data class PainelTrechos(
    val trechos: List<TrechoMonitorado> = emptyList(),
    val totalCritico: Int = 0,
    val totalAtencao: Int = 0,
    val totalOk: Int = 0,
    val somenteCriticos: Boolean = false
)
