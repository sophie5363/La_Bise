package com.example.labise.View.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.example.labise.R
import com.example.labise.View.Adapter.ChatAdapter
import com.example.labise.View.Adapter.ContactAdapter
import com.example.labise.databinding.FragmentChatBinding
import com.example.labise.databinding.FragmentConversationBinding
import com.google.firebase.auth.FirebaseAuth

class ConversationFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profilButton: ImageButton
    private lateinit var contactButton: ImageButton
    private lateinit var binding: FragmentConversationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilButton = view.findViewById(R.id.conversation_fragment_top_bar_profil_image_button)
        contactButton = view.findViewById(R.id.conversation_fragment_top_bar_add_contact_image_button)

        contactButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                parentFragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.fragment_fade_enter,
                    R.anim.fragment_fade_exit,
                    R.anim.fragment_fade_enter,
                    R.anim.fragment_fade_exit
                )
                    .replace(R.id.fragment_container, ListeContactFragment()).commit()
            }
        })

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            Glide.with(requireContext()).load(getPhotoUrl())
                .placeholder(R.drawable.baseline_account_circle_black_24dp).override(150)
                .into(view.findViewById(R.id.conversation_fragment_top_bar_profil_image_button))

            profilButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fragment_fade_enter,
                            R.anim.fragment_fade_exit,
                            R.anim.fragment_fade_enter,
                            R.anim.fragment_fade_exit
                        )
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
