package br.com.monitoragrama.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import java.time.LocalDate

@Entity(tableName = "trechos")
data class TrechoEntity(
    @PrimaryKey val id: String,
    val rodovia: String,
    val kmInicial: Int,
    val kmFinal: Int,
    val vegetacao: TipoVegetacao,
    val clima: Clima
)

/**
 * A chave composta (trecho + data) garante uma medição por dia: ao salvar
 * novamente no mesmo dia o banco substitui a anterior em vez de duplicar.
 */
@Entity(
    tableName = "medicoes",
    primaryKeys = ["trechoId", "data"],
    foreignKeys = [
        ForeignKey(
            entity = TrechoEntity::class,
            parentColumns = ["id"],
            childColumns = ["trechoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MedicaoEntity(
    val trechoId: String,
    val data: LocalDate,
    val alturaCm: Double
)

/** Trecho com o histórico que o Room monta juntando as duas tabelas. */
data class TrechoComMedicoes(
    @Embedded val trecho: TrechoEntity,
    @Relation(parentColumn = "id", entityColumn = "trechoId")
    val medicoes: List<MedicaoEntity>
)

fun TrechoComMedicoes.paraDominio() = Trecho(
    id = trecho.id,
    rodovia = trecho.rodovia,
    kmInicial = trecho.kmInicial,
    kmFinal = trecho.kmFinal,
    vegetacao = trecho.vegetacao,
    clima = trecho.clima,
    medicoes = medicoes.map { Medicao(it.data, it.alturaCm) }.sortedBy { it.data }
)

fun Trecho.paraEntidade() = TrechoEntity(id, rodovia, kmInicial, kmFinal, vegetacao, clima)

fun Medicao.paraEntidade(trechoId: String) = MedicaoEntity(trechoId, data, alturaCm)
