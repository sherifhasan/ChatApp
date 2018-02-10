package example.android.chatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import example.android.chatapp.R

class SignIn : AppCompatActivity() {
    private val RC_SIGN_IN = 1
    lateinit var mGoogleApiClient: GoogleApiClient
    private var mAuth: FirebaseAuth? = null
    private val TAG = "SignIn Activity"
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var username: String? = null
    private var firebaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

        FirebaseApp.initializeApp(applicationContext)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                firebaseUser = FirebaseAuth.getInstance().currentUser
                username = firebaseAuth.currentUser?.displayName
                startActivity(Intent(this@SignIn, ChatActivity::class.java))


            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)


        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { Toast.makeText(this@SignIn, "You Got an Error", Toast.LENGTH_LONG).show() }.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        signInButton.setOnClickListener { signIn() }


    }

    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(mAuthListener!!)
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                account?.let { firebaseAuthWithGoogle(it) }
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful) {
                        Log.w(TAG, "signInWithCredential", task.exception)
                        Toast.makeText(this@SignIn, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }
}
