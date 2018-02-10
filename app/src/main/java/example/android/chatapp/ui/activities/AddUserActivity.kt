package example.android.chatapp.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import example.android.chatapp.R
import example.android.chatapp.models.MessageDataClass
import example.android.chatapp.models.UserDataClass
import java.util.regex.Pattern

class AddUserActivity : AppCompatActivity() {
    var addButton: Button? = null
    var cancelButton: Button? = null
    var userEmail: EditText? = null
    var errorMessage: TextView? = null
    var flag = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((width * 0.95).toInt(), (height * 0.4).toInt())

        addButton = findViewById<Button>(R.id.add_button)
        cancelButton = findViewById<Button>(R.id.cancel_button)
        userEmail = findViewById<EditText>(R.id.user_email_edit_text)
        errorMessage = findViewById<TextView>(R.id.error_message)
        val currentUser = FirebaseAuth.getInstance().currentUser


        addButton?.setOnClickListener({
            flag = 0
            val email = userEmail?.text.toString()

            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)

            if (!matcher.matches()) {
                val textColor = Color.parseColor("#ff3300")
                errorMessage?.setTextColor(textColor)
                errorMessage?.text = "Sorry! Invalid email address"

            } else {

                val mDatabase1: DatabaseReference
                val mDatabase2: DatabaseReference
                mDatabase1 = FirebaseDatabase.getInstance().reference.child("users")
                mDatabase2 = FirebaseDatabase.getInstance().reference.child("friends").child(FirebaseAuth.getInstance().currentUser!!.uid)


                mDatabase1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        for (userSnapshot in dataSnapshot.children) {
                            // TODO: handle the post
                            val userDataClass = userSnapshot.getValue<UserDataClass>(UserDataClass::class.java)
                            if (userDataClass?.email.equals(email)) {

                                flag = 1
                                mDatabase2.addListenerForSingleValueEvent(object : ValueEventListener {

                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        // for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                                        // String friendId = friendSnapshot.getKey();
                                        if (!snapshot.hasChild(userDataClass?.id)) {
                                            val chatId = userDataClass?.id + FirebaseAuth.getInstance().currentUser!!.uid
                                            val welcome = MessageDataClass("You are now connected on ChatWithMe", "app", "app", null)
                                            var chats: DatabaseReference
                                            chats = FirebaseDatabase.getInstance().reference.child("chats")
                                            chats.child(chatId).child("message").push().setValue(welcome)
                                            chats = FirebaseDatabase.getInstance().reference.child("friends")
                                            chats.child(FirebaseAuth.getInstance().currentUser!!.uid).child(userDataClass?.id).child(chatId).setValue("1")

                                            chats = FirebaseDatabase.getInstance().reference.child("friends")
                                            chats.child(userDataClass?.id).child(FirebaseAuth.getInstance().currentUser?.uid).child(chatId).setValue("1")


                                            flag = 2
                                            val textColor = Color.parseColor("#4cee3d")
                                            errorMessage?.setTextColor(textColor)
                                            errorMessage?.text = "Friend added succeefully"

                                        }
                                    }

                                    // }

                                    override fun onCancelled(databaseError: DatabaseError) {

                                    }
                                })


                            }
                        }

                        if (flag == 1) {
                            val textColor = Color.parseColor("#4cee3d")
                            errorMessage?.setTextColor(textColor)
                            errorMessage?.text = "Friend has already added"
                        }

                        if (flag != 1 && flag != 2) {
                            val textColor = Color.parseColor("#ff3300")
                            errorMessage?.setTextColor(textColor)
                            errorMessage?.text = "Sorry! this user is not available now"
                        }


                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        // ...
                    }
                })


            }
        })

        cancelButton?.setOnClickListener(View.OnClickListener {
            errorMessage?.text = ""
            userEmail?.setText("")
            finish()
        })

    }
}
