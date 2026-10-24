package br.com.monitoragrama.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TrechoEntity::class, MedicaoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Conversores::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trechoDao(): TrechoDao

    companion object {
        private const val NOME = "monitoragrama.db"

        fun criar(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, NOME).build()
    }
}
