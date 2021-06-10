package ch.hearc.todolist.di

import ch.hearc.todolist.ui.MainActivity
import ch.hearc.todolist.ui.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector



@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = [ViewModelModule::class, FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class, FragmentBuildersModule::class])
    abstract fun contributeSplashActivity(): SplashActivity

}