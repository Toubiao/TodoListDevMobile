package ch.hearc.todolist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.todolist.R
import ch.hearc.todolist.persistence.Task
import ch.hearc.todolist.ui.adapter.TaskAdapter
import ch.hearc.todolist.util.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

class ListFragment : DaggerFragment(),
    TaskAdapter.Interaction {

    private lateinit var taskAdapter: TaskAdapter

    private lateinit var taskViewModel: TaskViewModel

    @Inject
    lateinit var viewmodelProviderFactory: ViewModelProviderFactory

    lateinit var allTasks: List<Task>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        allTasks = mutableListOf()
        return inflater.inflate(R.layout.fragment_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        initRecyclerView()
        observerLiveData()

    }

    private fun observerLiveData() {
        taskViewModel.getAllTasks().observe(viewLifecycleOwner, Observer { lisOfTask ->
            lisOfTask?.let {
                allTasks = it
                taskAdapter.swap(it)
            }
        })
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            taskAdapter = TaskAdapter(
                allTasks,
                this@ListFragment
            )
            layoutManager = LinearLayoutManager(this@ListFragment.context)
            adapter = taskAdapter
            val swipe = ItemTouchHelper(initSwipeToDelete())
            swipe.attachToRecyclerView(recyclerView)
        }
    }


    private fun setupViewModel() {
        taskViewModel =
            ViewModelProvider(this, viewmodelProviderFactory).get(TaskViewModel::class.java)
    }


    private fun initSwipeToDelete(): ItemTouchHelper.SimpleCallback {

        return object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                taskViewModel.delete(allTasks.get(position))
                val task = allTasks.get(position)
                Toast.makeText(activity, "Task Deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onItemSelected(position: Int, item: Task) {
        val navDirection = ListFragmentDirections.actionListFragmentToEditFragment(item)
        findNavController().navigate(navDirection)
    }

    override fun onBtnDeletedClicked(position: Int, item: Task) {
        taskViewModel.delete(allTasks.get(position))
        val task = allTasks.get(position)
        Toast.makeText(activity, "Task Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onTaskDone(position: Int, item: Task) {
        taskViewModel.insert(item)
    }
}


