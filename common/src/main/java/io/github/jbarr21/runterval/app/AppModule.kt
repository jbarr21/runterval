package io.github.jbarr21.runterval.app

import android.app.AlarmManager
import android.app.Application
import android.os.Vibrator
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

  @Provides
  fun alarmManager(application: Application) = application.getSystemService<AlarmManager>()!!

  @Provides
  fun vibrator(application: Application) = application.getSystemService<Vibrator>()!!
}
