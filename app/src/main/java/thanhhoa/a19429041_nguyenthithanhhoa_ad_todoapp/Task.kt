package thanhhoa.a19429041_nguyenthithanhhoa_ad_todoapp


import android.widget.EditText
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class Task(
    var id: String?=null,
    var desc: String?= null,
    var status: Boolean = false,
    var deadline: String? = null){

    private var isSelected : Boolean=false

    constructor(desc: String, status: Boolean, deadline: String) : this() {
        this.desc = desc
        this.status = status
        this.deadline = deadline
    }
    constructor(doc: DocumentSnapshot) : this() {
        id = doc.id
        desc = doc.getString("desc")
        status = doc.getBoolean("status") == true
        deadline = doc.getString("deadline")

    }

    companion object {
        fun get() : Task<QuerySnapshot> {
            return Firebase.firestore.collection("task").get()
        }

        fun getRecent() : Task<QuerySnapshot> {

            return Firebase.firestore.collection("task")
                .orderBy("deadline")
//                .whereEqualTo("status", false)
                .get()
        }

        fun get(id : String) : Task<DocumentSnapshot> {
            return Firebase.firestore.collection("task").document(id).get()
        }
    }

    private fun setSelected(selected : Boolean){
        isSelected = selected
    }

}
