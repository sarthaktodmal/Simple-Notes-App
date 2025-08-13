package com.sarthak.takenotes.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sarthak.takenotes.R
import com.sarthak.takenotes.adapter.NoteAdapter
import com.sarthak.takenotes.databinding.MainActivityBinding
import com.sarthak.takenotes.extra.MyToast
import com.sarthak.takenotes.holder.NoteDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity:AppCompatActivity() {
    val binding by lazy{
        MainActivityBinding.inflate(layoutInflater)
    }
    private lateinit var database: DatabaseReference
    private lateinit var adapter:NoteAdapter
    private val searchQuery = MutableStateFlow("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        loadNotes("")
        binding.addNote.setOnClickListener{
            binding.search.clearFocus()
            if(adapter.toBeDeleted.isEmpty()) {
                val intent = Intent(this@MainActivity, EditNoteActivity::class.java)
                startActivityForResult(intent, 0)
            }else{
                adapter.deleteSelected(){
                    loadNotes(binding.search.text.toString())
                }
            }
        }

        binding.search.addTextChangedListener {
            searchQuery.value = binding.search.text.toString()
        }
        lifecycleScope.launch {
            searchQuery.debounce(300) // Wait for 300ms of inactivity
                .distinctUntilChanged()
                .collect { query ->
                    loadNotes(query)
                }
        }
    }

    private fun loadNotes(query:String){
        binding.progress.visibility = View.VISIBLE
        binding.addText.visibility = View.GONE

        binding.rc.layoutManager = LinearLayoutManager(this)
        val list:MutableList<NoteDisplay> = mutableListOf()
        adapter = NoteAdapter(binding,this@MainActivity,list)
        binding.rc.adapter = adapter

        database = FirebaseDatabase.getInstance().reference
        val uid = FirebaseAuth.getInstance().uid?:""

        adapter.deselectNotes()

        database.child("users").child(uid).child("notes")
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(adapter.isNotesSelected()){
                    adapter.deselectNotes()
                }
                list.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val item = postSnapshot.getValue(NoteDisplay::class.java)
                    if(item != null && item.title.isEmpty() && item.des.isEmpty()){
                        database.child("users").child(uid).child("notes").child(item.id).removeValue()
                    }
                    if (item != null && query.isNotEmpty()) {
                        if (item.title.contains(query, ignoreCase = true) || item.des.contains(query, ignoreCase = true)) {
                            list.add(item)
                        }
                    }else {
                        item?.let { list.add(it) }
                    }
                }
                adapter.notifyDataSetChanged()
                binding.progress.visibility = View.GONE
                if(list.isEmpty()){
                    binding.addText.visibility = View.VISIBLE
                }else{
                    binding.addText.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                MyToast.showCustomToast(applicationContext,"Failed to load Notes",false)
                binding.progress.visibility = View.GONE // Hide ProgressBar
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0){
            loadNotes(binding.search.text.toString())
        }
    }
}