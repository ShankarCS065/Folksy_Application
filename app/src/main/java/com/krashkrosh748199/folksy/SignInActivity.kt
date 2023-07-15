package com.krashkrosh748199.folksy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.krashkrosh748199.folksy.Daos.UserDao
import com.krashkrosh748199.folksy.models.User
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 123
    private  val TAG="SignInActivity Tag"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth=Firebase.auth
        findViewById<com.google.android.gms.common.SignInButton>(R.id.signInButton).setOnClickListener {
            signIn()
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @DelicateCoroutinesApi
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
          if (requestCode==RC_SIGN_IN){
              val task= GoogleSignIn.getSignedInAccountFromIntent(data)
              handleSignInResult(task)
          }
    }

    @DelicateCoroutinesApi
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
                try {
                    val account = completedTask.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                }catch (e:ApiException){
                    Log.w(TAG,"SignInResult:failed code="+ e.statusCode)
                }
    }

    @DelicateCoroutinesApi
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential=GoogleAuthProvider.getCredential(idToken,null)
        findViewById<com.google.android.gms.common.SignInButton>(R.id.signInButton).visibility= View.GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO){
            val auth=auth.signInWithCredential(credential).await()
            val firebaseUser=auth.user
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }

        }

    }

    @DelicateCoroutinesApi
    private fun updateUI(firebaseUser: FirebaseUser?) {
           if (firebaseUser!=null){
               val user= User(firebaseUser.uid,firebaseUser.displayName,firebaseUser.photoUrl.toString())
               val userDao=UserDao()
               userDao.addUser(user)
               val mainActivityIntent= Intent(this,MainActivity::class.java)
               startActivity(mainActivityIntent)
               finish()
           } else{
               findViewById<com.google.android.gms.common.SignInButton>(R.id.signInButton).visibility=View.VISIBLE
               findViewById<ProgressBar>(R.id.progressBar).visibility=View.GONE

           }
    }

}