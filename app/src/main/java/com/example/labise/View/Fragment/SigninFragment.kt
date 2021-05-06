package com.example.labise.View.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.example.labise.R
import com.example.labise.View.Activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SigninFragment : Fragment() {

    lateinit var loginButton : Button
    lateinit var googleButton : ImageButton
    lateinit var mGoogleSignInClient : GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginButton = view.findViewById(R.id.loginButton)
        googleButton = view.findViewById(R.id.googleButton)
        
        loginButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                parentFragmentManager.beginTransaction().replace(R.id.login_activity_fragment_container, LoginFragment()).commit()
            }
        })

        //connexion google
        var account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireContext())
        if(account != null){
            var intent = Intent(this.requireContext(), MainActivity::class.java)
            startActivity(intent)
        }else{

            var gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

            mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)
            googleButton.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    when (v?.id){
                        R.id.googleButton ->{
                            var signinIntent : Intent = mGoogleSignInClient.signInIntent
                            startActivityForResult(signinIntent,100)
                        }
                    }
                }

            })
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SigninFragment().apply {
            }
    }
}