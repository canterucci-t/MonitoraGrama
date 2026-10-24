package br.com.monitoragrama

import android.app.Application
import br.com.monitoragrama.di.AppContainer

class MonitoraGramaApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
