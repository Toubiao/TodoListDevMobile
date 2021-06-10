package ch.hearc.todolist.repository

import androidx.lifecycle.LiveData
import ch.hearc.todolist.persistence.TaskDao
import ch.hearc.todolist.persistence.Task
import kotlinx.coroutines.*
import javax.inject.Inject

class TaskRepository @Inject constructor(val taskDao: TaskDao) {

    fun insert(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.insert(task)
        }
    }

    fun delete(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.delete(task)
        }
    }

    fun deleteById(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.deleteById(id)
        }
    }

    fun update(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.update(task)

        }
    }

    fun getAllTasks(): LiveData<List<Task>>{
        return taskDao.getAllTasks()
    }
}