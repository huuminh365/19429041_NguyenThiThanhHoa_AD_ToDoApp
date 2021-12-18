package thanhhoa.a19429041_nguyenthithanhhoa_ad_todoapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*



/**
 * A simple [Fragment] subclass.
 * Use the [DialogAddTask.newInstance] factory method to
 * create an instance of this fragment.
 */
class DialogAddTask(val titleDialog: String?= "Add New Task",
                    private val isEdit: Boolean? = false,
                    private val currentItem: Task?,
                    private val currentItemPosition: Int?
    ) : DialogFragment() {

    internal lateinit var listener: DialogAddItemListener
    val cal: Calendar = Calendar.getInstance()
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    interface DialogAddItemListener {
        fun onDialogPositiveClick(dialog: DialogFragment, isEdit: Boolean?=false, currentItem: Task?, currentItemPosition: Int?)
        fun onDialogNegativeClick(dialog: DialogFragment, isEdit: Boolean?= false, currentItem: Task?, currentItemPosition: Int?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_dialog_add_task, root, false)
            val builder = AlertDialog.Builder(it)
            builder.setTitle(titleDialog)
                .setView(view)
                .setPositiveButton(
                    R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogPositiveClick(this, isEdit, currentItem, currentItemPosition)
                    })
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
//                        listener.onDialogNegativeClick(this)
                    })
            val deadline : EditText = view.findViewById(R.id.add_deadline)
            deadline.setOnClickListener {
                val date  =
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        deadline.setText(format.format(cal.time))
                    }
                DatePickerDialog(
                    requireContext(), date, cal
                        .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as DialogAddItemListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }
}