package com.example.labise.View.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.labise.View.Activity.LoginActivity
import com.example.labise.R
import com.example.labise.View.Adapter.ChatAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfilFragment : Fragment() {

    lateinit var disconnectionButton : Button
    lateinit var mGoogleSignInClient : GoogleSignInClient

    lateinit var profilNameTextView: TextView

    lateinit var backButton: ImageView

    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var context = requireContext()

        backButton = view.findViewById(R.id.profil_fragment_top_bar_image_button)

        backButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                parentFragmentManager.popBackStack()
            }
        })

        profilNameTextView = view.findViewById(R.id.profil_fragment_name_user_text_view)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            profilNameTextView.text = getUserName()
            Glide.with(requireContext()).load(getPhotoUrl()).placeholder(R.drawable.baseline_account_circle_black_24dp).override(400)
                .into(view.findViewById(R.id.profil_fragment_profil_image_view))
        }


        disconnectionButton = view.findViewById(R.id.profil_fragment_logout_button)
        disconnectionButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                FirebaseAuth.getInstance().signOut()
                var gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                mGoogleSignInClient = GoogleSignIn.getClient(requireContext(),gso)
                mGoogleSignInClient.signOut()
                Firebase.auth.signOut()
                var intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfilFragment().apply {
            }
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ""
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

}