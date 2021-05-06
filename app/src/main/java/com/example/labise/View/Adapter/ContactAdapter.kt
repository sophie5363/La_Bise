package com.example.labise.View.Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.labise.Model.ChatContact
import com.example.labise.Model.ChatConversation
import com.example.labise.Model.ChatConversationUtilisateur
import com.example.labise.R
import com.example.labise.ViewModel.FirebaseViewModel
import com.example.labise.ViewModel.FormatterViewModel
import com.example.labise.databinding.ContactItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ContactAdapter(private val options: FirebaseRecyclerOptions<ChatContact>, private val userEmail: String?, private val userName : String?, private val parent : Fragment) : FirebaseRecyclerAdapter<ChatContact, RecyclerView.ViewHolder>(options) {

    private lateinit var db: FirebaseDatabase
    private lateinit var user1 : String
    private lateinit var user2 : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("debug message : ", "Create ViewHolder")
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(R.layout.contact_item, parent, false)
        val binding = ContactItemBinding.bind(view)
        return MessageViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: ChatContact) {
        Log.d("debug message : ", "Binding ViewHolder")
        if (options.snapshots[position].name != null) {
            (holder as MessageViewHolder).bind(model)
            user1 = userName!!
            user2 = options.snapshots[position].name!!
        } else {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_TEXT
    }

    inner class MessageViewHolder(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatContact) {
            Log.d("debug message : ", "func bind ViewHolder")

            binding.contactItemAccountImageButton.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    db = Firebase.database
                    var createdConversation = "conversation_" + FormatterViewModel.formatForFirebaseDatabase(userEmail!!)+"_"+ item.email!! //+"_" + Date().time.
                    Log.d("conversation formatter", createdConversation)
                    var chatConversation = ChatConversation(createdConversation,user1, user2)
                    var chatConversationUtilisateur = ChatConversationUtilisateur(createdConversation)
                    db.reference.child(FirebaseViewModel.CONVERSATION_SECTION).child(createdConversation).setValue(chatConversation)
                    db.reference.child(FirebaseViewModel.UTILISATEUR_SECTION).child(FormatterViewModel.formatForFirebaseDatabase(userEmail)).child(FirebaseViewModel.UTILISATEUR_CONVERSATIONS).push().setValue(chatConversationUtilisateur)
                    parent.parentFragmentManager.popBackStack()
                }
            })
            binding.contactItemAccountTextView.text = item.name
            setTextColor(item.name, binding.contactItemAccountTextView)

            //if (item.photoUrl != null) {
            //    loadImageIntoView(binding.contactItemAccountImageView, item.photoUrl!!)
            //} else {
            binding.contactItemAccountImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
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
        const val TAG = "ContactAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }

}