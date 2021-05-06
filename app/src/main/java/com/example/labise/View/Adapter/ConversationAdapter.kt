package com.example.labise.View.Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ConversationAdapter(private val options: FirebaseRecyclerOptions<ChatConversation>, private val displayName : String, private val parent : Fragment) : FirebaseRecyclerAdapter<ChatConversation, RecyclerView.ViewHolder>(options)  {

    private lateinit var db: FirebaseDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("debug message : ", "Create ViewHolder")
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(R.layout.conversation_item, parent, false)
        val binding = ConversationItemBinding.bind(view)
        return MessageViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: ChatConversation) {
        Log.d("debug message : ", "Binding ViewHolder")
        if (options.snapshots[position].nomUser2 != null) {
            (holder as MessageViewHolder).bind(model)
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

            binding.conversationItemAccountTextView.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    FirebaseViewModel.CONVERSATION_ID = item.conversationName!!
                    parent.parentFragmentManager.beginTransaction().addToBackStack("ChatFragment")
                        .setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit,R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
                        .replace(R.id.main_activity_fragment_container, ChatFragment()).commit()
                }
            })
            if(displayName != item.nomUser1){
                binding.conversationItemAccountTextView.text = item.nomUser1
                setTextColor(item.nomUser1, binding.conversationItemAccountTextView)
            }else{
                binding.conversationItemAccountTextView.text = item.nomUser2
                setTextColor(item.nomUser2, binding.conversationItemAccountTextView)
            }


            //if (item.photoUrl != null) {
            //    loadImageIntoView(binding.contactItemAccountImageView, item.photoUrl!!)
            //} else {
            binding.conversationItemAccountImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
            //}
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != ANONYMOUS && userName != null) {
                textView.setBackgroundResource(R.color.black)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.color.grey)
                textView.setTextColor(Color.BLACK)
            }
        }

    }

    companion object {
        const val TAG = "ConversationAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}