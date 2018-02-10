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

class EditUserNameActivity : AppCompatActivity() {
    var editUserName: EditText? = null
    var firebaseUser: FirebaseUser? = null
    var editButton: Button? = null
    var cancelButton: Button? = null
    var errorMessage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_name)

        supportActionBar?.setTitle("Edit User Name")

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((width * 0.95).toInt(), (height * 0.4).toInt())
        firebaseUser = FirebaseAuth.getInstance().currentUser

        editUserName = findViewById<EditText>(R.id.user_name_edit_text)
        errorMessage = findViewById<TextView>(R.id.error_message_user_name)
        val bundle = intent.extras
        val userName = bundle?.getString("name")

        editUserName?.setText(userName)
        editButton = findViewById<Button>(R.id.edit_name_button)

        cancelButton = findViewById<Button>(R.id.cancel_button_user_name)

        editButton?.setOnClickListener({
            val userName = editUserName?.text.toString()
            if (userName.matches("".toRegex())) {
                errorMessage?.setText("You must enter user name")
            } else {
                val databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid)
                databaseReference.child("name").setValue(userName)
                errorMessage?.text = ""
                finish()
            }
        })
        cancelButton?.setOnClickListener({
            errorMessage?.text = ""
            finish()
        })


    }
}