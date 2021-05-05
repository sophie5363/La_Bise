package com.example.labise.View.Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.labise.Model.ChatContact
import com.example.labise.R
import com.example.labise.databinding.ContactItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ContactAdapter(private val options: FirebaseRecyclerOptions<ChatContact>) : FirebaseRecyclerAdapter<ChatContact, RecyclerView.ViewHolder>(options) {

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