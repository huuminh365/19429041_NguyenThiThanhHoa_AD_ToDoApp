package thanhhoa.a19429041_nguyenthithanhhoa_ad_todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.task.*
import thanhhoa.a19429041_nguyenthithanhhoa_ad_todoapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), DialogAddTask.DialogAddItemListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var adapter : TaskAdapter
    val tasks = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }
        adapter = TaskAdapter(this,R.layout.task ,tasks, binding, this)
        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView.adapter = adapter

        Task.getRecent()
            .addOnSuccessListener { querySnapshot ->
                val documents = querySnapshot.documents
                for (doc in documents) {
                    val task = Task(doc)
                    tasks.add(task)
                    adapter!!.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    "",
                    "fromCloudFirestore: Error loading ContactInfo data from Firestore - " + e.message
                );
            };
        adapter?.notifyDataSetChanged()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, isEdit: Boolean?, currentItem: Task?, currentItemPosition: Int?) {
        val taskDesc: EditText = dialog.dialog!!.findViewById(R.id.add_task)
        val deadline: EditText = dialog.dialog!!.findViewById(R.id.add_deadline)
        val uniqueID = UUID.randomUUID().toString()
        val task = Task(uniqueID, taskDesc.text.toString(), false, deadline.text.toString())
        if(isEdit == true){
             if(deadline.text.toString() != "")
                updateTask(currentItem!!, taskDesc.text.toString(), deadline.text.toString())
            else if(deadline.text.toString() == "") {
                 updateDesc(currentItem!!, taskDesc.text.toString())
            }
        }
        else {
            writeNewTask(task)
        }
        refresh()
        Toast.makeText(
            baseContext,
            "save sucessfuly!!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment, isEdit: Boolean?, currentItem: Task?, currentItemPosition: Int?) {
        Toast.makeText(
            baseContext,
            "cancel",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun writeNewTask(task :Task) {
        Firebase.firestore.collection("task").document(task.id.toString())
            .set(task)
    }

    // nếu sửa cả ngày tháng và tên task
    fun updateTask(task : Task, desc:String?=null, deadline:String?=null){
        Firebase.firestore.collection("task").document(task.id.toString())
            .update("desc",  desc)
        Firebase.firestore.collection("task").document(task.id.toString())
            .update("deadline",  deadline)
    }

    // chỉ sửa tên task
    fun updateDesc(task : Task, desc:String?=null){
        Firebase.firestore.collection("task").document(task.id.toString())
            .update("desc",  desc)
    }
    fun refresh() {
        tasks.clear()
        adapter.notifyDataSetChanged()
        Task.getRecent()
            .addOnSuccessListener { query ->
                val documents = query.documents
                for (doc: DocumentSnapshot in documents) {
                    val task = Task(doc)
                    tasks.add(task)
                    adapter.notifyItemInserted(tasks.size - 1)
                }
            }
    }

    fun showDialog(titleDialog:String?="Add New Task",  isEdit: Boolean?=false,currentItem: Task?=null, currentItemPosition: Int?=null) {
        val addItemFragment = DialogAddTask(titleDialog, isEdit, currentItem, currentItemPosition)
        addItemFragment.show(supportFragmentManager, "AddItemTag")
    }

}