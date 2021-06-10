package ch.hearc.todolist.di


import ch.hearc.todolist.ui.AddFragment
import ch.hearc.todolist.ui.EditFragment
import ch.hearc.todolist.ui.ListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector



@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeListFragment(): ListFragment

    @ContributesAndroidInjector
    abstract fun contributeAddFragment(): AddFragment

    @ContributesAndroidInjector
    abstract fun contributeEditFragment(): EditFragment
}