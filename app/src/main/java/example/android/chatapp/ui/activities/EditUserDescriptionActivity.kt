package example.android.chatapp.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import example.android.chatapp.R

class EditUserDescriptionActivity : AppCompatActivity() {
    var editUserDescription: EditText? = null
    var firebaseUser: FirebaseUser? = null
    var editButton: Button? = null
    var cancelButton: Button? = null
    var errorMessage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_description)
        supportActionBar?.title = "Edit Description"
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((width * 0.95).toInt(), (height * 0.4).toInt())
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val bundle = intent.extras
        val description = bundle!!.getString("description")


        editUserDescription = findViewById<EditText>(R.id.user_about_edit_text)
        errorMessage = findViewById<TextView>(R.id.error_message_user_about)

        editUserDescription?.setText(description)
        editButton = findViewById<Button>(R.id.edit_user_about_button)

        cancelButton = findViewById<Button>(R.id.cancel_user_about_button)

        editButton?.setOnClickListener({
            val userName = editUserDescription?.text.toString()

            val databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid)
            databaseReference.child("description").setValue(userName)
            errorMessage?.text = ""
            finish()
        })
        cancelButton?.setOnClickListener({
            errorMessage?.text = ""
            finish()
        })
    }
}
