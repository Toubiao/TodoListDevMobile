package ch.hearc.todolist.ui.adapter

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.*
import com.google.android.material.chip.ChipGroup
import ch.hearc.todolist.R
import ch.hearc.todolist.persistence.Task
import com.pchmn.materialchips.ChipView
import kotlinx.android.synthetic.main.task_items.view.*



class TaskAdapter(
    taskList: List<Task>,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private val tasks = mutableListOf<Task>()

    init {
        tasks.addAll(taskList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.task_items, parent, false)
        return ViewHolder(
            view,
            interaction
        )
    }

    override fun getItemCount() = tasks.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item = tasks[position])
    }


    fun swap(tasks: List<Task>) {
        val diffCallback = DiffCallback(this.tasks, tasks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.tasks.clear()
        this.tasks.addAll(tasks)
        diffResult.dispatchUpdatesTo(this)
    }


    class ViewHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Task) {
            itemView.txtTitle.text = item.title
            itemView.txtDescription.text = item.description
            if (item.color?.length!! > 1){
                itemView.findViewById<CardView>(R.id.editCardView).setCardBackgroundColor(Color.parseColor("#"+item.color))
            }

            itemView.bgImage.setImageURI(Uri.parse(item.imgUri))
            itemView.itemDate.text = item.date

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition,item)
            }
            itemView.delete.setOnClickListener {
                interaction?.onBtnDeletedClicked(adapterPosition,item);
            }
            itemView.cbxDone.isChecked = (item.isDone == 1)
            itemView.cbxDone.setOnClickListener {
                item.isDone = if (itemView.cbxDone.isChecked) 1 else 0
                interaction?.onTaskDone(adapterPosition,item);
            }
            val cg: ChipGroup = itemView.findViewById(R.id.chipGroup)
            if(item.tag != null){
                val list: List<String> = item.tag!!.split(",")
                val iterator = list.iterator()
               iterator.forEach {
                   val cv = ChipView(itemView.context)
                   cv.setDeletable(false)
                   cv.label = it
                   cv.setLabelColor(Color.WHITE)
                   cv.setChipBackgroundColor(Color.DKGRAY)
                   cg.addView(cv)
               }
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Task)

        fun onBtnDeletedClicked(position: Int, item: Task)

        fun onTaskDone(position:Int, item: Task)
    }
}