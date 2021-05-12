package com.example.labise.View.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.labise.Model.ChatMessage
import com.example.labise.R
import com.example.labise.View.Adapter.ChatAdapter
import com.example.labise.View.Adapter.ChatAdapter.Companion.ANONYMOUS
import com.example.labise.ViewModel.FirebaseViewModel
import com.example.labise.ViewModel.ScrollToBottomObserverChat
import com.example.labise.databinding.FragmentChatBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ChatAdapter
    private lateinit var binding: FragmentChatBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var auth: FirebaseAuth
    private lateinit var backButton: ImageView
    private lateinit var profilName : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.chat_fragment_top_bar_image_button)

        backButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                parentFragmentManager.popBackStack()
            }
        })
        profilName = view.findViewById(R.id.chat_fragment_top_bar_text_view)

        auth = FirebaseAuth.getInstance()

        db = Firebase.database

        val messagesRef = db.reference.child(FirebaseViewModel.CONVERSATION_SECTION).child(
            FirebaseViewModel.CONVERSATION_ID
        ).child(FirebaseViewModel.CONVERSATION_TEXT_SECTION)

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
            ScrollToBottomObserverChat(binding.messageRecyclerView, adapter, manager)
        )

        binding.sendButton.setOnClickListener {
            val sdf = SimpleDateFormat("dd MMM yyyy 'at' HH:mm:ss z")
            val currentDateandTime: String = sdf.format(Date())
            Log.d("date : ", currentDateandTime)
            val chatMessage = ChatMessage(
                binding.messageEditText.text.toString(),
                currentDateandTime,
                FirebaseViewModel.CONVERSATION_ID,
                false,
                getUserName(),
                getUserEmail(),
                "text",
                getPhotoUrl()
            )
            db.reference.child(FirebaseViewModel.CONVERSATION_SECTION).child(FirebaseViewModel.CONVERSATION_ID).child(
                FirebaseViewModel.CONVERSATION_TEXT_SECTION
            ).push().setValue(chatMessage)
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

    private fun getUserEmail(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.email
        } else ANONYMOUS
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }


}