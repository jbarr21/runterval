package io.github.jbarr21.runterval.app

import android.app.AlarmManager
import android.os.Vibrator
import io.github.jbarr21.runterval.data.AmbientStream
import io.github.jbarr21.runterval.data.AppStore
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

object Modules {
  private val appModule : Module = applicationContext {
    provide { androidApplication().getSystemService(AlarmManager::class.java) as AlarmManager }
    provide { androidApplication().getSystemService(Vibrator::class.java) }
  }

  private val dataModule : Module = applicationContext {
    provide { AmbientStream() }
    provide { AppStore() }
  }

  private val uiModule : Module = applicationContext {

  }

  val modules = listOf(
    appModule,
    dataModule,
    uiModule
  )
}