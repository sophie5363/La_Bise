package com.example.labise.View.Fragment

import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.labise.Model.ChatMessage
import com.example.labise.R
import com.example.labise.View.Adapter.ChatAdapter
import com.example.labise.View.Adapter.ChatAdapter.Companion.ANONYMOUS
import com.example.labise.View.Adapter.ChatAdapter.Companion.MESSAGES_CHILD
import com.example.labise.ViewModel.ScrollToBottomObserver
import com.example.labise.databinding.ActivityMainBinding
import com.example.labise.databinding.FragmentChatBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ChatAdapter
    private lateinit var binding: FragmentChatBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)

        // The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
        // See: https://github.com/firebase/FirebaseUI-Android
        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(messagesRef, ChatMessage::class.java)
            .build()
        adapter = ChatAdapter(options, getUserName())
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(requireContext())
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter

        // Scroll down when a new message arrives
        // See MyScrollToBottomObserver for details
        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
        )

        binding.sendButton.setOnClickListener {
            val chatMessage = ChatMessage(
                binding.messageEditText.text.toString(),
                getUserName(),
                getPhotoUrl(),
                null /* no image */
            )
            db.reference.child(MESSAGES_CHILD).push().setValue(chatMessage)
            binding.messageEditText.setText("")
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }


}