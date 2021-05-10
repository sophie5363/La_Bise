package com.example.labise.View.Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.labise.Model.ChatContact
import com.example.labise.Model.ChatConversation
import com.example.labise.R
import com.example.labise.View.Fragment.ChatFragment
import com.example.labise.ViewModel.FirebaseViewModel
import com.example.labise.ViewModel.FormatterViewModel
import com.example.labise.databinding.ContactItemBinding
import com.example.labise.databinding.ConversationItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ConversationAdapter(
    private val options: ArrayList<ChatConversation>,
    private val email: String,
    private val parent: Fragment,
    private val displayName : String,
) : RecyclerView.Adapter<ConversationAdapter.MessageViewHolder>() {

    private lateinit var db: FirebaseDatabase

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationAdapter.MessageViewHolder {
        Log.d("debug message : ", "Create ViewHolder")
        val inflater = LayoutInflater.from(parent.context)

        db = Firebase.database

        val view = inflater.inflate(R.layout.conversation_item, parent, false)
        val binding = ConversationItemBinding.bind(view)
        return MessageViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ConversationAdapter.MessageViewHolder, position: Int) {
        Log.d("debug message : ", "Binding ViewHolder")
        if (options[position].nomUser2 != null) {
            (holder as MessageViewHolder).bind(options[position])
        } else {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_TEXT
    }

    inner class MessageViewHolder(private val binding: ConversationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatConversation) {
            Log.d("debug message : ", "func bind ViewHolder")

            binding.conversationItemAccountTextView.setOnClickListener(object :
                View.OnClickListener {
                override fun onClick(p0: View?) {
                    FirebaseViewModel.CONVERSATION_ID = item.conversationName!!
                    parent.parentFragmentManager.beginTransaction().addToBackStack("ChatFragment")
                        .setCustomAnimations(
                            R.anim.fragment_fade_enter,
                            R.anim.fragment_fade_exit,
                            R.anim.fragment_fade_enter,
                            R.anim.fragment_fade_exit
                        )
                        .replace(R.id.main_activity_fragment_container, ChatFragment()).commit()
                }
            })

            if(displayName == item.nomUser1){
                binding.conversationItemAccountTextView.text = item.nomUser2
            }else{
                binding.conversationItemAccountTextView.text = item.nomUser1
            }


            binding.conversationItemAccountImageView.setOnClickListener(object :
                View.OnClickListener {
                override fun onClick(p0: View?) {
                    parent.parentFragmentManager.beginTransaction().addToBackStack("ChatFragment")
                        .setCustomAnimations(
                            R.anim.fragment_fade_enter,
                            R.anim.fragment_fade_exit,
                            R.anim.fragment_fade_enter,
                            R.anim.fragment_fade_exit
                        )
                        .replace(R.id.main_activity_fragment_container, ChatFragment()).commit()
                }
            })

            var conversationName = item.conversationName
            conversationName = conversationName!!.replace("conversation","")
            conversationName = conversationName!!.replace(email,"")
            val othersEmail = conversationName!!.replace("_","")

            val requestImage = db.reference.child(FirebaseViewModel.UTILISATEUR_SECTION).child(othersEmail).child(FirebaseViewModel.UTILISATEUR_PHOTOURL)

            requestImage.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var url = snapshot.getValue(String::class.java)!!
                        Glide.with(parent.requireContext()).load(url).placeholder(R.drawable.baseline_account_circle_black_24dp).override(400)
                            .into(binding.conversationItemAccountImageView)
                    }else{
                        binding.conversationItemAccountImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

    }

    companion object {
        const val TAG = "ConversationAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }

    override fun getItemCount(): Int {
        return options.size
    }
}