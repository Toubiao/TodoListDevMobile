package ch.hearc.todolist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ch.hearc.todolist.persistence.Task
import ch.hearc.todolist.repository.TaskRepository
import javax.inject.Inject

class TaskViewModel @Inject constructor(
    val taskRepository: TaskRepository
) : ViewModel() {


  fun insert(task: Task) {
        return taskRepository.insert(task)
    }

    fun delete(task: Task) {
        taskRepository.delete(task)
    }

    fun deleteById(id:Int){
        taskRepository.deleteById(id)
    }

    fun update(task: Task) {
        taskRepository.update(task)
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return taskRepository.getAllTasks()
    }


}