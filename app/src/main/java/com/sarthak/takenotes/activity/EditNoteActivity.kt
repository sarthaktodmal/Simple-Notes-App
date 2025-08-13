package com.sarthak.takenotes.activity

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sarthak.takenotes.R
import com.sarthak.takenotes.databinding.EditNoteBinding
import com.sarthak.takenotes.extra.MyToast
import com.sarthak.takenotes.holder.NoteDisplay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditNoteActivity:AppCompatActivity() {
    private val binding by lazy{
        EditNoteBinding.inflate(layoutInflater)
    }
    private lateinit var note:NoteDisplay

    private lateinit var database: DatabaseReference
    private lateinit var noteId:String
    private lateinit var uid: String
    private lateinit var dialog:Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        uid = FirebaseAuth.getInstance().uid?:""

        val title = intent.getStringExtra("TITLE").toString()
        val id = intent.getStringExtra("ID").toString()
        val des = intent.getStringExtra("DES").toString()
        val time = intent.getStringExtra("TIME").toString()

        if(intent.hasExtra("ID")){
            binding.noteTitle.setText(title)
            binding.noteDes.setText(des)
            binding.noteTime.setText("Edited  $time")
            note = NoteDisplay(id,title,des,time)
            noteId = id
        }else{
            noteId = database.child("users").child(uid).child("notes").push().key.toString()
            note = NoteDisplay(noteId,"","","")
        }

        binding.backButton.setOnClickListener{
            finish()
        }
        binding.noteTitle.addTextChangedListener{
                note.title = binding.noteTitle.text.toString()
                note.time = getCurrentDateTime()
                saveChanges()
        }
        binding.noteDes.addTextChangedListener{
                note.des = binding.noteDes.text.toString()
                note.time = getCurrentDateTime()
                saveChanges()
        }

        initDeleteFunctionality()
    }

    private fun initDeleteFunctionality() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(this,R.drawable.custom_dialog_back))
        dialog.findViewById<TextView>(R.id.cancelBut).setOnClickListener{
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.deleteBut).setOnClickListener{
            database.child("users").child(uid).child("notes").child(noteId).removeValue()
            finish()
        }
        binding.deleteButton.setOnClickListener{
            dialog.show()
        }
    }

    private fun saveChanges(){
        database.child("users").child(uid).child("notes").child(noteId).setValue(note)
    }
    private fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yy  h:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun finish() {
        super.finish()

    }
}