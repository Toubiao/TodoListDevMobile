package ch.hearc.todolist.di

import android.app.Application
import androidx.room.Room
import ch.hearc.todolist.persistence.TaskDao
import ch.hearc.todolist.persistence.TaskDatabase
import ch.hearc.todolist.repository.TaskRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton



@Module
class AppModule {


    @Singleton
    @Provides
    fun providesAppDatabase(app:Application): TaskDatabase {
        return Room.databaseBuilder(app, TaskDatabase::class.java,"task_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesTaskDao(db: TaskDatabase): TaskDao {
        return db.taskDao()
    }

    @Provides
    fun providesRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepository(taskDao)
    }
}