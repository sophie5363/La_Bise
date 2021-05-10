package com.example.labise.View.Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.labise.Model.ChatMessage
import com.example.labise.R
import com.example.labise.databinding.MessageChatBinding
import com.example.labise.databinding.MessageChatImageBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.zip.Inflater

class ChatAdapter (private val options: FirebaseRecyclerOptions<ChatMessage>, private val currentUserName: String?) : FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder>(options) {

    lateinit var view : View
    lateinit var inflater : LayoutInflater
    lateinit var parent : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("debug message : ","Create ViewHolder")
        inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            this.parent = parent
            view = inflater.inflate(R.layout.message_chat, parent, false)
            val binding = MessageChatBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            view = inflater.inflate(R.layout.message_chat_image, parent, false)
            val binding = MessageChatImageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: ChatMessage) {
        Log.d("debug message : ","Binding ViewHolder")
        if (options.snapshots[position].text != null) {
            (holder as MessageViewHolder).bind(model)
        } else {
            (holder as ImageMessageViewHolder).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    inner class MessageViewHolder(private val binding: MessageChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatMessage) {

            if(item.name == currentUserName){
                Log.d("creating new inflater","now")
                view = inflater.inflate(R.layout.message_chat_alt, parent, false)
                val binding = MessageChatBinding.bind(view)
                MessageViewHolder(binding)
            }

            Log.d("debug message : ","func bind ViewHolder")
            binding.messageTextView.text = item.text
            setTextColor(item.name, binding.messageTextView)

            if (item.photoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
            }
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != ANONYMOUS && currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.color.black)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.color.grey)
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    inner class ImageMessageViewHolder(private val binding: MessageChatImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatMessage) {
            loadImageIntoView(binding.messageImageView, item.imageUrl!!)

            binding.messengerTextView.text = if (item.name == null) ANONYMOUS else item.name
            if (item.photoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
            }
        }
    }

    private fun loadImageIntoView(view: ImageView, url: String) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Glide.with(view.context)
                        .load(downloadUrl)
                        .into(view)
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            Glide.with(view.context).load(url).into(view)
        }
    }

    companion object {
        const val TAG = "ChatAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
        const val ANONYMOUS = "anonymous"
        private const val REQUEST_IMAGE = 2
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }

}