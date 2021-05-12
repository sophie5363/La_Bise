package com.example.labise.Model

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.labise.R
import com.example.labise.ViewModel.FirebaseViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DialogCustom  : DialogFragment() {
    var dialog = this
    var list : ArrayList<ChatConversation>? = null
    lateinit var erase : LinearLayout
    lateinit var cancel : LinearLayout
    var nomConv : String? = null
    val db = Firebase.database
    var email : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_alert_custom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        erase = view.findViewById(R.id.dialog_erase)
        cancel = view.findViewById(R.id.dialog_cancel)

        erase.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(nomConv != null && email != null){
                    var convRef = db.reference.child(FirebaseViewModel.UTILISATEUR_SECTION).child(email!!).child(FirebaseViewModel.UTILISATEUR_CONVERSATIONS)
                    convRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                list = ArrayList()
                                for (data in snapshot.children) {
                                    var dataChild = data.getValue(ChatConversation::class.java)!!
                                    if(dataChild.conversationName.equals(nomConv)){
                                        data.ref.removeValue()
                                    }
                                }
                                dialog.dismiss()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
            }
        })

        cancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
            }
        })
    }
}