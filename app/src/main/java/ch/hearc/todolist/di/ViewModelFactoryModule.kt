package ch.hearc.todolist.di

import androidx.lifecycle.ViewModelProvider
import ch.hearc.todolist.util.ViewModelProviderFactory
import dagger.Binds
import dagger.Module


@Module
abstract class ViewModelFactoryModule {


    @Binds
    abstract fun bindViewModelFactory(viewModelProvideFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}