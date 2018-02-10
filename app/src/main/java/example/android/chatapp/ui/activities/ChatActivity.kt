package example.android.chatapp.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import example.android.chatapp.R
import example.android.chatapp.models.MessageDataClass
import example.android.chatapp.ui.adapters.ChatAdapter

class ChatActivity : AppCompatActivity() {
    private val TAG = "ChatActivity"
    val DEFAULT_MSG_LENGTH_LIMIT = 1000
    private val RC_PHOTO_PICKER = 2

    val RC_SIGN_IN = 1

    private var mProgressBar: ProgressBar? = null
    private var mMessageEditText: EditText? = null
    private var mSendButton: Button? = null
    private var mPhotoPickerButton: ImageButton? = null


    private var mUser: FirebaseUser? = null

    // Firebase instance variables
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mMessagesDatabaseReference: DatabaseReference? = null
    private var mChildEventListener: ChildEventListener? = null
    private var mFirebaseAuth: FirebaseUser? = null
    private var mFirebaseStorage: FirebaseStorage? = null
    private var mChatPhotosStorageReference: StorageReference? = null

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ChatAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var friendlyMessages: ArrayList<MessageDataClass>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val bundle = intent.extras
        val chatId = bundle?.getString("chatId")
        val myFriend = bundle.getString("myFriend")

        supportActionBar?.title = myFriend


        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance().currentUser
        mFirebaseStorage = FirebaseStorage.getInstance()

        mUser = FirebaseAuth.getInstance().currentUser


        mMessagesDatabaseReference = mFirebaseDatabase?.reference?.child("chats")?.child(chatId)?.child("message")
        mChatPhotosStorageReference = mFirebaseStorage?.reference?.child("chat_photos")

        // Initialize references to views
        mProgressBar = findViewById(R.id.progressBar)
        mMessageEditText = findViewById(R.id.messageEditText)
        mSendButton = findViewById(R.id.sendButton)
        mRecyclerView = findViewById(R.id.message_view)
        mLayoutManager = LinearLayoutManager(applicationContext)
        mRecyclerView?.layoutManager = mLayoutManager
        mPhotoPickerButton = findViewById(R.id.photoPickerButton)


        // Initialize message ListView and its adapter
        friendlyMessages = ArrayList()
        mAdapter = ChatAdapter(friendlyMessages ?: ArrayList(), this)
        mRecyclerView?.adapter = mAdapter

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        mRecyclerView?.layoutManager = layoutManager

        // Initialize progress bar
        mProgressBar?.visibility = ProgressBar.VISIBLE

        mPhotoPickerButton?.setOnClickListener(View.OnClickListener {
            // TODO: Fire an intent to show an image picker
        })

        // Enable Send button when there's text to send
        mMessageEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                mSendButton?.isEnabled = charSequence.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        // to limit message limit (message length)
        mMessageEditText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT))

        // Send button sends a message and clears the EditText
        mSendButton?.setOnClickListener({
            val friendlyMessage = MessageDataClass(mMessageEditText?.text.toString(), mUser?.displayName, mUser?.uid, null)
            mMessagesDatabaseReference?.push()?.setValue(friendlyMessage)

            // Clear input box
            mMessageEditText?.setText("")
        })
        mPhotoPickerButton?.setOnClickListener({
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
        })

        attachDatabaseReaderListener()
    }

    private fun attachDatabaseReaderListener() {

        if (mChildEventListener == null) {
            mChildEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String) {
                    val friendlyMessage = dataSnapshot.getValue(MessageDataClass::class.java)
                    friendlyMessage?.let { friendlyMessages?.add(it) }
                    mProgressBar?.visibility = ProgressBar.INVISIBLE
                    mAdapter?.notifyItemRangeChanged(0, friendlyMessages?.size ?: -1)
                    mAdapter?.notifyItemInserted(friendlyMessages?.size?.minus(1) ?: 1)
                    mRecyclerView?.scrollToPosition(mAdapter?.itemCount?.minus(1) ?: 1)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {}

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}

                override fun onCancelled(databaseError: DatabaseError) {}
            }
            mMessagesDatabaseReference?.addChildEventListener(mChildEventListener)
        }

    }

    private fun detachDatabaseReaderListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference?.removeValue(mChildEventListener as DatabaseReference.CompletionListener)
            mChildEventListener = null
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data.data

            // Get a reference to store file at chat_photos/<FILENAME>
            val photoRef = mChatPhotosStorageReference?.child(selectedImageUri?.lastPathSegment
                    ?: "")

            // Upload file to Firebase Storage
            photoRef?.putFile(selectedImageUri)
                    ?.addOnSuccessListener(this) { taskSnapshot ->
                        // When the image has successfully uploaded, we get its download URL
                        val downloadUrl = taskSnapshot.downloadUrl

                        // Set the download URL to the message box, so that the user can send it to the database
                        val friendlyMessage = MessageDataClass(null, FirebaseAuth.getInstance().currentUser?.displayName, FirebaseAuth.getInstance().currentUser?.uid, downloadUrl?.toString())
                        mMessagesDatabaseReference?.push()?.setValue(friendlyMessage)
                    }
        }
    }
}
