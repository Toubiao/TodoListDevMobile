package ch.hearc.todolist.ui

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
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
import kotlinx.android.synthetic.main.task_items.view.*
import java.text.DateFormat
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject


class AddFragment : DaggerFragment() {

    @Inject
    lateinit var viewmodelProviderFactory: ViewModelProviderFactory

    lateinit var taskViewModel: TaskViewModel
    lateinit var timerViewModel: TimerViewModel
    var cal = Calendar.getInstance()
    var imgUri: String? = ""
    var colorBg: String? = ""



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val uri: Uri = data?.data!!
            imgUri = uri.toString()
            imageView.setImageURI(Uri.parse(imgUri))
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, "Une erreur est survenu ", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Pas d'image selectionné ", Toast.LENGTH_SHORT).show()

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                date.text = df.format(cal.time)

            }
        }

        btnAddDate.setOnClickListener(object : View.OnClickListener {
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
        btnAddColor.setOnClickListener {
            alerrtColor.show()
        }
        btnTakeImg.setOnClickListener {
            ImagePicker.with(this)
//                .saveDir("/storage/sdcard0/Android/data/package/files/" + Environment.DIRECTORY_PICTURES)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        chips_input.editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                val textTag = chips_input.editText.text.toString()
                chips_input.addChip(textTag, textTag)
                return@OnKeyListener true
            }
            false
        })
        chips_input.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                val textTag = chips_input.editText.text.toString()
                chips_input.addChip(textTag, textTag)
                return@OnKeyListener true
            }
            false
        })
        chips_input.addChipsListener(object : ChipsInput.ChipsListener {
            override fun onChipAdded(chip: ChipInterface?, newSize: Int) {

            }

            override fun onChipRemoved(chip: ChipInterface?, newSize: Int) {

            }

            override fun onTextChanged(text: CharSequence?) {
                if (text != null && text.length > 0) {
                    val size: Int = text.length
                    if (text.get(size - 1).equals(' ')) {
                        val textTag = text.subSequence(0, size - 1).toString()
                        chips_input.addChip(textTag, textTag)
                    }
                }
            }
        })

        btnAdd.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.container).popBackStack()
        }
        createChannel("TODOLISTCHANNEL","todolist")
    }



    private fun setLayoutColor(envelope: ColorEnvelope?) {
        colorBg = envelope?.hexCode;
        addCardView.setCardBackgroundColor(Color.parseColor("#" + colorBg))
    }


    private fun saveTaskToDatabase() {


        (activity as MainActivity?)?.showFloatingButton()

        if (validations()) {
            Toast.makeText(activity, "Task is saved", Toast.LENGTH_SHORT).show()
            saveTask()
        } else
            Toast.makeText(activity, "Task is Discarded", Toast.LENGTH_SHORT).show()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        saveTaskToDatabase()
    }


    private fun saveTask() {

        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)


        val chips: List<Chip> = chips_input.selectedChipList as List<Chip>

        val tags = chips.map { chip -> chip.label  }.joinToString(",")
        val task = Task(
            0,
            addTitle.text.toString(),
            addDescription.text.toString(),
            tags,
            colorBg,
            imgUri,
            df.format(cal.time),
            0
        )


        //If title is null set Empty Title
        if (addTitle.text.isNullOrEmpty()) {
            task.title = "Empty Title"

            //Call viewmodel to save the data
            taskViewModel.insert(task)

        } else {
            //Call viewmodel to save the data
            taskViewModel.insert(task)
        }
        val diff = ChronoUnit.HOURS.between(Calendar.getInstance().time.toInstant(),cal.time.toInstant())
        timerViewModel.setTimeSelected(diff.toInt())
        timerViewModel.setAlarm(true);
    }

    fun validations(): Boolean {
        return !(addTitle.text.isNullOrEmpty()
                && addDescription.text.isNullOrEmpty())
    }

    private fun createChannel(channelId: String, channelName: String) {

            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Todolist Reminder"

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

    }


    private fun setupViewModel() {
        taskViewModel =
            ViewModelProvider(this, viewmodelProviderFactory).get(TaskViewModel::class.java)
        timerViewModel =
            ViewModelProvider(this, viewmodelProviderFactory).get(TimerViewModel::class.java)
    }
}