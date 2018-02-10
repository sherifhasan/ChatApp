package example.android.chatapp.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import example.android.chatapp.R
import example.android.chatapp.models.UserDataClass

class UserProfileActivity : AppCompatActivity() {
    private val RC_PHOTO_PICKER = 2
    private var profilePic: CircleImageView? = null
    private var editButton: ImageButton? = null
    var userNameTextView: TextView? = null
    var emailTextView: TextView? = null
    var aboutTextView: TextView? = null
    private val CAMERA_PIC_REQUEST = 100
    private var mFirebaseStorage: FirebaseStorage? = null
    private var mProfilePhotosStorageReference: StorageReference? = null
    private var userDescription: String? = null
    private var currentUser: UserDataClass? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        userNameTextView = findViewById(R.id.edit_user_name) as TextView
        emailTextView = findViewById(R.id.user_email) as TextView
        aboutTextView = findViewById(R.id.user_about) as TextView
        emailTextView?.text = FirebaseAuth.getInstance().currentUser?.email
        supportActionBar?.setTitle("Profile")
        // progressBar = (ProgressBar) findViewById(R.id.progressBarProfilePhoto);
        profilePic = findViewById<CircleImageView>(R.id.profile_image_editable)
        mFirebaseStorage = FirebaseStorage.getInstance()
        mProfilePhotosStorageReference = mFirebaseStorage?.reference?.child("profile_photos")

        val databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentUser = dataSnapshot.getValue<UserDataClass>(UserDataClass::class.java)
                userDescription = currentUser?.description
                aboutTextView?.text = currentUser?.description
                userNameTextView?.text = currentUser?.name
                profilePic?.visibility = VISIBLE
                Glide.with(profilePic?.context)
                        .load(currentUser?.photoUrl)
                        .into(profilePic)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


        editButton = findViewById(R.id.myButton) as ImageButton
        editButton?.setOnClickListener(View.OnClickListener {
            // progressBar.setVisibility(VISIBLE);
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
        })


    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data.data

            val mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)


            // Get a reference to store file at chat_photos/<FILENAME>
            val photoRef = mProfilePhotosStorageReference?.child(selectedImageUri!!.lastPathSegment)

            // Upload file to Firebase Storage
            photoRef?.putFile(selectedImageUri)
                    ?.addOnSuccessListener(this, { taskSnapshot ->
                        // When the image has successfully uploaded, we get its download URL
                        val downloadUrl = taskSnapshot.downloadUrl
                        Toast.makeText(application, "Photo edited successfully", Toast.LENGTH_SHORT).show()
                        //  progressBar.setVisibility(INVISIBLE);

                        // Set the download URL to the message box, so that the user can send it to the database
                        mDatabase.child("photoUrl").setValue(downloadUrl!!.toString())
                    })
        }
    }

    fun editName(v: View) {

        val moveToEditUserNameActivity = Intent(this@UserProfileActivity, EditUserNameActivity::class.java)
        moveToEditUserNameActivity.putExtra("name", currentUser?.name)
        startActivity(moveToEditUserNameActivity)
    }

    fun editAbout(v: View) {
        val moveToEditUserDescriptionActivity = Intent(this@UserProfileActivity, EditUserDescriptionActivity::class.java)
        moveToEditUserDescriptionActivity.putExtra("description", userDescription)
        startActivity(moveToEditUserDescriptionActivity)
    }

}
