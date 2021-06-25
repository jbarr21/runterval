package io.github.jbarr21.runterval.data

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.tatarka.redux.Dispatcher
import me.tatarka.redux.Reducer
import me.tatarka.redux.SimpleStore
import me.tatarka.redux.Store
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

  @Singleton
  @Provides
  fun packageManager(application: Application) = application.packageManager

  @Singleton
  @Provides
  fun ambientStream() = AmbientStream()

  @Singleton
  @Provides
  fun store(): Store<AppState> {
    return SimpleStore(
      AppState(workoutState = WorkoutState.WorkoutSelection(SampleData.WORKOUTS), paused = true)
    )
  }

  @Singleton
  @Provides
  fun dispatcher(store: Store<AppState>, reducer: Reducer<Action, AppState>): Dispatcher<Action, Action> {
    return Dispatcher.forStore(store, reducer)
  }

  @Singleton
  @Provides
  fun reducer(): Reducer<Action, AppState> = Reducers.app
}
