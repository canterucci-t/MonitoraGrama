package br.com.monitoragrama.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

/** O SQLite não conhece LocalDate, então a data é guardada como número do dia. */
class Conversores {

    @TypeConverter
    fun paraDia(data: LocalDate?): Long? = data?.toEpochDay()

    @TypeConverter
    fun paraData(dia: Long?): LocalDate? = dia?.let(LocalDate::ofEpochDay)
}
