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
import com.example.labise.R
import com.example.labise.View.Activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    lateinit var signinButton : Button
    lateinit var googleButton : ImageButton
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        signinButton = view.findViewById(R.id.signinbutton)

        signinButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    SigninFragment()
                ).commit()
            }
        })

        mAuth = Firebase.auth
        mAuth = FirebaseAuth.getInstance()

        googleButton = view.findViewById(R.id.googleButton)

        //connexion google
        val currentUser = mAuth.currentUser

        if(currentUser != null){
            var intent = Intent(this.requireContext(), MainActivity::class.java)
            startActivity(intent)
        }else{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            googleButton.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    when (v?.id){
                        R.id.googleButton->{
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

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Google Auth : ", "signInWithCredential:failure", task.exception)

                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100){
            var task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)!!
                Log.d("token google : ", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
                var intent = Intent(this.requireContext(), MainActivity::class.java)
                startActivity(intent)
            }catch (e: ApiException){
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
