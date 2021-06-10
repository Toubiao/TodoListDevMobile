package ch.hearc.todolist.persistence
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task): Long

    @Update
    fun update(task: Task)

    @Query("delete from tbl_task where id = :id")
    fun deleteById(id: Int)

    @Delete
    fun delete(task: Task)

    @Query("select * from tbl_task")
    fun getAllTasks():LiveData<List<Task>>
}