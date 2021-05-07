package com.example.labise.View.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.labise.Model.ChatContact
import com.example.labise.Model.ChatConversation
import com.example.labise.R
import com.example.labise.View.Adapter.ChatAdapter
import com.example.labise.View.Adapter.ContactAdapter
import com.example.labise.View.Adapter.ConversationAdapter
import com.example.labise.ViewModel.FirebaseViewModel
import com.example.labise.ViewModel.FormatterViewModel
import com.example.labise.ViewModel.ScrollToBottomObserverContact
import com.example.labise.ViewModel.ScrollToBottomObserverConversation
import com.example.labise.databinding.FragmentChatBinding
import com.example.labise.databinding.FragmentConversationBinding
import com.example.labise.databinding.FragmentListeContactBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ConversationFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profilButton: ImageButton
    private lateinit var contactButton: ImageButton
    private lateinit var binding: FragmentConversationBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ConversationAdapter
    private lateinit var manager: LinearLayoutManager

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
                parentFragmentManager.beginTransaction().addToBackStack("ListeContactFragment").setCustomAnimations(
                    R.anim.fragment_fade_enter,
                    R.anim.fragment_fade_exit,
                    R.anim.fragment_fade_enter,
                    R.anim.fragment_fade_exit
                )
                    .replace(R.id.main_activity_fragment_container, ListeContactFragment()).commit()
            }
        })

        auth = FirebaseAuth.getInstance()

        db = Firebase.database
        //val messagesRef = db.reference.child(FirebaseViewModel.CONVERSATION_SECTION)

        if (auth.currentUser != null) {
            Glide.with(requireContext()).load(getPhotoUrl())
                .placeholder(R.drawable.baseline_account_circle_black_24dp).override(150)
                .into(view.findViewById(R.id.conversation_fragment_top_bar_profil_image_button))

            val emailFormatted = FormatterViewModel.formatForFirebaseDatabase(auth.currentUser.email)

            val messagesRef = db.reference.child(FirebaseViewModel.UTILISATEUR_SECTION).child(FormatterViewModel.formatForFirebaseDatabase(emailFormatted)).child(FirebaseViewModel.UTILISATEUR_CONVERSATIONS)
            val options = FirebaseRecyclerOptions.Builder<ChatConversation>()
                .setQuery(messagesRef, ChatConversation::class.java)
                .build()

            profilButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    parentFragmentManager.beginTransaction().addToBackStack("ProfilFragment")
                        .setCustomAnimations(
                            R.anim.fragment_fade_enter,
                            R.anim.fragment_fade_exit,
                            R.anim.fragment_fade_enter,
                            R.anim.fragment_fade_exit
                        )
                        .replace(R.id.main_activity_fragment_container, ProfilFragment()).commit()
                }
            })
            adapter = ConversationAdapter(options, auth.currentUser!!.displayName!!, this)
            Log.d("Liste contact adapter", adapter.toString())
            binding.conversationProgressBar.visibility = ProgressBar.INVISIBLE
            manager = LinearLayoutManager(requireContext())
            manager.stackFromEnd = true
            binding.conversationFragmentContactRecyclerView.layoutManager = manager
            binding.conversationFragmentContactRecyclerView.adapter = adapter

            adapter.registerAdapterDataObserver(
                ScrollToBottomObserverConversation(binding.conversationFragmentContactRecyclerView, adapter, manager)
            )
        }
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("start","listening")
        adapter.startListening()
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

}
