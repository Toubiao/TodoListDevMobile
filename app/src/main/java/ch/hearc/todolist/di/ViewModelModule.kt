package ch.hearc.todolist.di

import androidx.lifecycle.ViewModel
import ch.hearc.todolist.ui.TaskViewModel
import ch.hearc.todolist.ui.TimerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(TaskViewModel::class)
    abstract fun bindMainViewModel(taskViewModel: TaskViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TimerViewModel::class)
    abstract fun bindTimerViewModel(timerViewModel: TimerViewModel): ViewModel
}
