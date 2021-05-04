package com.example.labise.View.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.labise.R
import com.google.firebase.auth.FirebaseAuth

class ListeContactFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profilButton : ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilButton = view.findViewById(R.id.liste_contact_fragment_top_bar_profil_image_button)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            Glide.with(requireContext()).load(getPhotoUrl()).placeholder(R.drawable.baseline_account_circle_black_24dp).override(150)
                .into(view.findViewById(R.id.liste_contact_fragment_top_bar_profil_image_button))

            profilButton.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit,R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
                        .replace(R.id.fragment_container, ProfilFragment()).commit()
                }
            })
        }

    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

}