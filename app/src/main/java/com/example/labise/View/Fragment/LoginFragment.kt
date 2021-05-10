package com.example.labise.View.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.labise.Model.ChatContact
import com.example.labise.Model.ChatConversation
import com.example.labise.Model.ChatConversationUtilisateur
import com.example.labise.Model.ChatUtilisateur
import com.example.labise.R
import com.example.labise.View.Activity.MainActivity
import com.example.labise.ViewModel.FirebaseViewModel
import com.example.labise.ViewModel.FormatterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DEBUG_PROPERTY_NAME


class LoginFragment : Fragment() {

    lateinit var signinButton: Button
    lateinit var googleButton: ImageButton
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.database

        signinButton = view.findViewById(R.id.signinbutton)

        signinButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.login_activity_fragment_container,
                    SigninFragment()
                ).commit()
            }
        })

        mAuth = Firebase.auth
        mAuth = FirebaseAuth.getInstance()

        googleButton = view.findViewById(R.id.googleButton)

        //connexion google
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            var intent = Intent(this.requireContext(), MainActivity::class.java)
            startActivity(intent)
        } else {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            googleButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    when (v?.id) {
                        R.id.googleButton -> {
                            signIn()
                        }
                    }
                }

            })
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Google Auth : ", "signInWithCredential:success")

                val user = mAuth.currentUser

                if (user != null) {

                    val userId = user.getUid()

                    val context = requireContext()

                    val eventListener: ValueEventListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                var email = FormatterViewModel.formatForFirebaseDatabase(user.email!!)
                                var name = FormatterViewModel.formatForFirebaseDatabase(user.displayName!!)
                                val newContact = ChatContact(
                                    email,
                                    name,
                                )
                                db.reference.child(FirebaseViewModel.USER_SECTION).child(userId).setValue(newContact)
                                var tabName : List<String> = name.split(' ')

                                if(tabName.size == 2){
                                    var chatUtilisateur = ChatUtilisateur(tabName[1],tabName[0], user.photoUrl.toString())
                                    db.reference.child(FirebaseViewModel.UTILISATEUR_SECTION).child(email).setValue(chatUtilisateur)
                                }

                                var intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                            }else{
                                var intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    }

                    db.reference.child(FirebaseViewModel.USER_SECTION).child(userId).addListenerForSingleValueEvent(eventListener)


                }

            } else {
                // If sign in fails, display a message to the user.
                Log.w("Google Auth : ", "signInWithCredential:failure", task.exception)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("token google : ", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                Log.w("Fail", "signInResult:failed code= ${e.statusCode}")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
            }
    }
}
