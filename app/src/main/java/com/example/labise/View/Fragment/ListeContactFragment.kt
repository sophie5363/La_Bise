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
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.labise.Model.ChatContact
import com.example.labise.Model.ChatMessage
import com.example.labise.R
import com.example.labise.View.Adapter.ChatAdapter
import com.example.labise.View.Adapter.ContactAdapter
import com.example.labise.ViewModel.FirebaseViewModel
import com.example.labise.ViewModel.FormatterViewModel
import com.example.labise.ViewModel.ScrollToBottomObserverContact
import com.example.labise.databinding.FragmentChatBinding
import com.example.labise.databinding.FragmentListeContactBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ListeContactFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ContactAdapter
    private lateinit var binding: FragmentListeContactBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var backButton: ImageView
    var list : ArrayList<ChatContact> = ArrayList()
    private lateinit var email : String
    private lateinit var displayName : String
    private lateinit var searchBar : SearchView
    var self = this

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

        val context = requireContext()

        searchBar = view.findViewById(R.id.liste_contact_search_view)
        backButton = view.findViewById(R.id.liste_contact_fragment_top_bar_profil_image_button)

        if(searchBar != null){
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    search(p0!!)
                    return true
                }

            })
        }

        backButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                parentFragmentManager.popBackStack()
            }
        })

        auth = FirebaseAuth.getInstance()

        db = Firebase.database

        val messagesRef = db.reference.child(FirebaseViewModel.USER_SECTION)
        val options = FirebaseRecyclerOptions.Builder<ChatContact>()
            .setQuery(messagesRef, ChatContact::class.java)
            .build()
        val user = auth.currentUser
        if(user != null){

            email = FormatterViewModel.formatForFirebaseDatabase(user.email)
            displayName = FormatterViewModel.formatForFirebaseDatabase(user.displayName)
            messagesRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        list = ArrayList()
                        for (data in snapshot.children){
                            var dataChild = data.getValue(ChatContact::class.java)!!
                            if(dataChild.email != email){
                                list.add(dataChild)
                            }
                        }
                        adapter = ContactAdapter(list,user.email, user.displayName, self)
                        //adapter = ContactAdapter(options, user.email, user.displayName, this)
                        //Log.d("Liste contact adapter",adapter.toString())
                        binding.listeContactProgressBar.visibility = ProgressBar.INVISIBLE
                        manager = LinearLayoutManager(context)
                        binding.listeContactFragmentContactRecyclerView.layoutManager = manager
                        binding.listeContactFragmentContactRecyclerView.adapter = adapter

                        // Scroll down when a new message arrives
                        // See MyScrollToBottomObserver for details
                        adapter.registerAdapterDataObserver(
                            ScrollToBottomObserverContact(binding.listeContactFragmentContactRecyclerView, adapter, manager)
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun search(str : String){
        var myList : ArrayList<ChatContact> = ArrayList()
        for (item in list){
            if(item.name!!.toLowerCase().contains(str.toLowerCase())){
                myList.add(item)
            }
            adapter = ContactAdapter(myList, email, displayName, self)
            //adapter = ContactAdapter(options, user.email, user.displayName, this)
            //Log.d("Liste contact adapter",adapter.toString())
            binding.listeContactProgressBar.visibility = ProgressBar.INVISIBLE
            manager = LinearLayoutManager(requireContext())
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
        //adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        //adapter.startListening()
    }

}