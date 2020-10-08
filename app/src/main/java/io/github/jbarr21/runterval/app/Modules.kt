package io.github.jbarr21.runterval.app

import android.app.AlarmManager
import android.os.Vibrator
import io.github.jbarr21.runterval.data.Action
import io.github.jbarr21.runterval.data.AmbientStream
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.data.AppStore
import io.github.jbarr21.runterval.data.Reducers
import io.github.jbarr21.runterval.data.WorkoutState
import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.util.SampleData
import me.tatarka.redux.Dispatcher
import me.tatarka.redux.Reducer
import me.tatarka.redux.SimpleStore
import me.tatarka.redux.Store
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

@Suppress("DEPRECATION")
object Modules {
  private val appModule : Module = applicationContext {
    provide { androidApplication().getSystemService(AlarmManager::class.java) as AlarmManager }
    provide { androidApplication().getSystemService(Vibrator::class.java) }
  }

  private val store = AppStore()

  private val dataModule : Module = applicationContext {
    provide { AmbientStream() }
    provide { store }
    provide { store as Store<AppState> }
    provide { Dispatcher.forStore(get(), Reducers.app) }
    provide { Reducer { _: Action, state: AppState -> state } as Reducer<Action, AppState> }
  }

  private val uiModule : Module = applicationContext {

  }

  val modules = listOf(
    appModule,
    dataModule,
    uiModule
  )
}