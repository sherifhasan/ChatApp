package example.android.chatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import example.android.chatapp.R
import example.android.chatapp.models.UserDataClass
import example.android.chatapp.ui.adapters.ChatHeadsAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    private var chatHeadsListView: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var cUser: FirebaseUser? = null
    private var mProgressBar: ProgressBar? = null
    private var chatHeadsAdapter: ChatHeadsAdapter? = null
    var usersData: ArrayList<UserDataClass>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cUser = FirebaseAuth.getInstance().currentUser
        if (cUser == null) {
            finish()
        }
        chatHeadsListView = findViewById<RecyclerView>(R.id.chat_heads_list)
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        mProgressBar = findViewById<ProgressBar>(R.id.progressBarMain)
        mProgressBar?.visibility = ProgressBar.VISIBLE
        usersData = ArrayList()

        val userCheck = FirebaseDatabase.getInstance().reference.child("users")
        userCheck.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.hasChild(cUser?.uid)) {

                    val userDataClass = UserDataClass(cUser?.displayName, cUser?.email, cUser?.uid, "", cUser?.photoUrl?.toString())
                    val usersRef = FirebaseDatabase.getInstance().getReference("users")
                    usersRef.child(cUser?.uid).setValue(userDataClass)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        swipeRefreshLayout?.setOnRefreshListener(
                SwipeRefreshLayout.OnRefreshListener {
                    //   Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                    // This method performs the actual data-refresh operation.
                    // The method calls setRefreshing(false) when it's finished.
                    usersData = ArrayList()
                    uploadChatHeads()
                }
        )


        // function calling
        usersData = ArrayList()
        uploadChatHeads()

    }

    fun addNewUser(v: View) {

        startActivity(Intent(this@MainActivity, AddUserActivity::class.java))

    }


    fun uploadChatHeads() {

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            finish()
        }
        usersData?.clear()
        val mDatabase: DatabaseReference
        mDatabase = FirebaseDatabase.getInstance().reference.child("friends").child(cUser?.uid)

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                for (userSnapshot in dataSnapshot.children) {

                    val Uid = userSnapshot.key
                    val userObjectDBRef: DatabaseReference

                    userObjectDBRef = FirebaseDatabase.getInstance().reference.child("users").child(Uid)


                    usersData?.clear()
                    userObjectDBRef.addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            val user = dataSnapshot.getValue(UserDataClass::class.java)
                            usersData?.add(user!!)

                            // chatHeadsAdapter.notifyDataSetChanged();
                            //chatHeadsAdapter.getItemId(usersData.size() - 1);
                            Toast.makeText(applicationContext, Integer.toString(usersData?.size
                                    ?: -1), Toast.LENGTH_SHORT).show()
                            chatHeadsAdapter = usersData?.let { ChatHeadsAdapter(application, it) }
                            chatHeadsListView?.adapter = chatHeadsAdapter
                            swipeRefreshLayout?.isRefreshing = false
                            mProgressBar?.visibility = ProgressBar.INVISIBLE

                        }


                        override fun onCancelled(databaseError: DatabaseError) {
                        }
                    })

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.profile_view -> {
                val userProfileActivty = Intent(this@MainActivity, UserProfileActivity::class.java)
                startActivity(userProfileActivty)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
