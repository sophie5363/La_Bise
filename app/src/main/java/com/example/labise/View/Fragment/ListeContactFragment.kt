package com.example.labise.View.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.labise.Model.ChatContact
import com.example.labise.Model.ChatMessage
import com.example.labise.R
import com.example.labise.View.Adapter.ChatAdapter
import com.example.labise.View.Adapter.ContactAdapter
import com.example.labise.ViewModel.FirebaseViewModel
import com.example.labise.ViewModel.ScrollToBottomObserverContact
import com.example.labise.databinding.FragmentChatBinding
import com.example.labise.databinding.FragmentListeContactBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ListeContactFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ContactAdapter
    private lateinit var binding: FragmentListeContactBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var backButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListeContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.liste_contact_fragment_top_bar_profil_image_button)

        backButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                parentFragmentManager.popBackStack()
            }
        })

        auth = FirebaseAuth.getInstance()

        db = Firebase.database
        //Log.d("path", )
        val messagesRef = db.reference.child(FirebaseViewModel.USER_SECTION)
        Log.d("liste contact", "Created view")
        // The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
        // See: https://github.com/firebase/FirebaseUI-Android
        val options = FirebaseRecyclerOptions.Builder<ChatContact>()
            .setQuery(messagesRef, ChatContact::class.java)
            .build()
        Log.d("liste contact", "option done")
        Log.d("liste contact options", options.toString())
        val user = auth.currentUser
        if(user != null){
            adapter = ContactAdapter(options, user.email, user.displayName, this)
            Log.d("Liste contact adapter",adapter.toString())
            binding.listeContactProgressBar.visibility = ProgressBar.INVISIBLE
            manager = LinearLayoutManager(requireContext())
            manager.stackFromEnd = true
            binding.listeContactFragmentContactRecyclerView.layoutManager = manager
            binding.listeContactFragmentContactRecyclerView.adapter = adapter

            // Scroll down when a new message arrives
            // See MyScrollToBottomObserver for details
            adapter.registerAdapterDataObserver(
                ScrollToBottomObserverContact(binding.listeContactFragmentContactRecyclerView, adapter, manager)
            )
        }
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

}