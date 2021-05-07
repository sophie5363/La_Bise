package com.example.labise.View.Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class ContactAdapter(private val options: ArrayList<ChatContact>, private val userEmail: String?, private val userName : String?, private val parent : Fragment) : RecyclerView.Adapter<ContactAdapter.MessageViewHolder>() {

    private lateinit var db: FirebaseDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        Log.d("debug message : ", "Create ViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact_item, parent, false)
        val binding = ContactItemBinding.bind(view)
        return MessageViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        Log.d("debug message : ", "Binding ViewHolder")
        if (options[position].name != null) {
            val emailFormatted = FormatterViewModel.formatForFirebaseDatabase(userEmail!!)

            if(options[position].email == emailFormatted){
                holder.itemView.visibility = View.GONE
                holder.itemView.isEnabled = false
            }else{
                val user1 = userName!!
                val user2 = options[position].name!!
                (holder as MessageViewHolder).bind(options[position],user1,user2)
            }
        } else {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_TEXT
    }

    inner class MessageViewHolder(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatContact, user1 : String, user2 : String) {
            Log.d("debug message : ", "func bind ViewHolder")

            this.itemView

            binding.contactItemAccountImageButton.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    db = Firebase.database
                    var createdConversation = "conversation_" + FormatterViewModel.formatForFirebaseDatabase(userEmail!!)+"_"+ item.email!!
                    var chatConversation = ChatConversation(createdConversation,user1, user2)
                    db.reference.child(FirebaseViewModel.CONVERSATION_SECTION).child(createdConversation).setValue(chatConversation)
                    db.reference.child(FirebaseViewModel.UTILISATEUR_SECTION).child(FormatterViewModel.formatForFirebaseDatabase(userEmail)).child(FirebaseViewModel.UTILISATEUR_CONVERSATIONS).push().setValue(chatConversation)
                    db.reference.child(FirebaseViewModel.UTILISATEUR_SECTION).child(FormatterViewModel.formatForFirebaseDatabase(item.email!!)).child(FirebaseViewModel.UTILISATEUR_CONVERSATIONS).push().setValue(chatConversation)
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

    override fun getItemCount(): Int {
        return options.size
    }

}