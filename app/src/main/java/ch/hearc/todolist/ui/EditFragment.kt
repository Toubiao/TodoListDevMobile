package ch.hearc.todolist.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.github.dhaval2404.imagepicker.ImagePicker
import ch.hearc.todolist.R
import ch.hearc.todolist.persistence.Task
import ch.hearc.todolist.util.ViewModelProviderFactory
import com.pchmn.materialchips.ChipsInput
import com.pchmn.materialchips.model.Chip
import com.pchmn.materialchips.model.ChipInterface
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_edit.*
import java.text.DateFormat
import java.util.*
import javax.inject.Inject


class EditFragment : DaggerFragment() {

    @Inject
    lateinit var viewmodelProviderFactory: ViewModelProviderFactory

    lateinit var taskViewModel: TaskViewModel
    var cal = Calendar.getInstance()
    var imgUri: String? = ""
    var colorBg: String? = ""
    var isDone: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val uri: Uri = data?.data!!
            imgUri = uri.toString()
            editImageView.setImageURI(Uri.parse(imgUri))
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, "Une erreur est survenu ", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Pas d'image selectionné ", Toast.LENGTH_SHORT).show()

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareTaskForEditing()
        setupViewModel()

        val colorPickerBuilder = ColorPickerDialog.Builder(view.context)
            .setTitle("ColorPicker Dialog")
            .setPositiveButton("Confirmer",
                ColorEnvelopeListener { envelope, fromUser -> setLayoutColor(envelope) })
            .setNegativeButton(
                "Annuler"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true) // the default value is true.
            .attachBrightnessSlideBar(true) // the default value is true.
            .setBottomSpace(12)

        val colorPickerView: ColorPickerView = colorPickerBuilder.colorPickerView
        val alerrtColor = colorPickerBuilder.create()


        val bubbleFlag = BubbleFlag(view.context)
        bubbleFlag.flagMode = FlagMode.FADE
        colorPickerView.setFlagView(bubbleFlag)

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)
                editDate.text = df.format(cal.time)

            }
        }

        btnEditAddDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    view.context,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })
        btnEditAddColor.setOnClickListener {
            alerrtColor.show()
        }
        btnEditTakeImg.setOnClickListener {
            ImagePicker.with(this)
//                .saveDir("/storage/sdcard0/Android/data/package/files/" + Environment.DIRECTORY_PICTURES)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }


        editChips_input.editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                val textTag = chips_input.editText.text.toString()
                chips_input.addChip(textTag, textTag)
                return@OnKeyListener true
            }
            false
        })
        editChips_input.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                val textTag = editChips_input.editText.text.toString()
                editChips_input.addChip(textTag, textTag)
                return@OnKeyListener true
            }
            false
        })
        editChips_input.addChipsListener(object : ChipsInput.ChipsListener {
            override fun onChipAdded(chip: ChipInterface?, newSize: Int) {

            }

            override fun onChipRemoved(chip: ChipInterface?, newSize: Int) {

            }

            override fun onTextChanged(text: CharSequence?) {
                if (text != null && text.length > 0) {
                    val size: Int = text.length
                    if (text.get(size - 1).equals(' ')) {
                        val textTag = text.subSequence(0, size - 1).toString()
                        editChips_input.addChip(textTag, textTag)
                    }
                }
            }
        })


        btnEdit.setOnClickListener {
            Navigation.findNavController(requireActivity(),R.id.container).popBackStack()
        }
    }

    private fun setLayoutColor(envelope: ColorEnvelope?) {
        colorBg = envelope?.hexCode;
        editCardView.setCardBackgroundColor(Color.parseColor("#" + colorBg))
    }

    // Method #2
    private fun saveTaskToDatabase() {

        (activity as MainActivity?)?.showFloatingButton()

        if (validations()) {
            Toast.makeText(activity, "Task is saved", Toast.LENGTH_SHORT).show()
            saveTask()
            val id:Int = EditFragmentArgs.fromBundle(arguments!!).task?.id!!

        } else {
            Toast.makeText(activity, "Task is Discarded", Toast.LENGTH_SHORT).show()
            val id: Int = EditFragmentArgs.fromBundle(arguments!!).task?.id!!
            taskViewModel.deleteById(id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveTaskToDatabase()
    }

    private fun saveTask() {

        val id:Int? = EditFragmentArgs.fromBundle(arguments!!).task?.id

        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)


        val chips: List<Chip> = editChips_input.selectedChipList as List<Chip>

        val tags = chips.map { chip -> chip.label  }.joinToString(",")
        val task = Task(
            id!!,
            editTitle.text.toString(),
            editDescription.text.toString(),
            tags,
            colorBg,
            imgUri,
            df.format(cal.time),
            isDone
        )


        if (editTitle.text.isNullOrEmpty()) {
            task.title = "Empty Title"

            taskViewModel.insert(task)

        } else {
            taskViewModel.insert(task)
        }
    }

    fun validations(): Boolean {
        return !(editTitle.text.isNullOrEmpty()
                && editDescription.text.isNullOrEmpty())
    }


    private fun setupViewModel() {
        taskViewModel = ViewModelProvider(this,viewmodelProviderFactory).get(TaskViewModel::class.java)
    }


    private fun prepareTaskForEditing() {
        arguments?.let {
            val safeArgs = EditFragmentArgs.fromBundle(it)
            val task = safeArgs.task
            editTitle.setText(task?.title)
            editDescription.setText(task?.description)
            editDate.text = task?.date
            colorBg = task?.color;
            imgUri = task?.imgUri
            isDone = task?.isDone!!

            if (task?.color?.length!! > 1){
                editCardView.setCardBackgroundColor(Color.parseColor("#"+colorBg))
            }

            editImageView.setImageURI(Uri.parse(imgUri))
            if(task?.tag != null){
                val list: List<String> = task?.tag!!.split(",")
                val iterator = list.iterator()
                iterator.forEach {
                    editChips_input.addChip(it, it)
                }
            }
        }

    }
}

